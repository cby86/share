package com.spring.cloud.controller;

import com.spring.cloud.controller.command.CarCommand;
import com.spring.cloud.controller.command.CarExportCommand;
import com.spring.cloud.entity.*;
import com.spring.cloud.repository.ImportRecordRepository;
import com.spring.cloud.service.CarService;
import com.spring.cloud.service.UserService;
import com.spring.cloud.utils.CommandUtils;
import com.spring.cloud.utils.POIUtils;
import com.spring.cloud.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@RestController
@RequestMapping("/car")
@Slf4j
public class CarController extends ImportController {
    @Autowired
    private CarService carService;
    @Autowired
    private ImportRecordRepository importRecordRepository;

    @Autowired
    private UserService userService;


    @RequestMapping("/loadCars")
    public Map<String, Object> loadDrivers(String cardNumber, Status status, int importId, Integer page, Integer pageSize) {
        Sort sort = new Sort(Sort.Direction.DESC, "createDate");
        Pageable pageable = PageRequest.of(page, pageSize, sort);
        User user = SecurityUtils.currentUser();
        Page<Car> carList = carService.loadCarsByPage(cardNumber, status, user.getId(), importId, pageable);
        return this.resultMap(CommandUtils.responsePage(carList.getTotalElements(), carList.getTotalPages(),
                CommandUtils.toCommands(carList.getContent(), CarCommand.class)));
    }

    @RequestMapping("/import")
    public Map<String, Object> loadDrivers(MultipartFile file) throws IOException, ExecutionException, InterruptedException {
        User user = SecurityUtils.currentUser();
        Future<Object> submit = executor.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                ImportRecord importRecord = new ImportRecord();
                importRecord.setType(ImportType.CAR);
                importRecord.setUser(userService.findUser(user.getId()));
                importRecord.setQueryStatus(QueryStatus.IMPORT);
                try {
                    List<String[]> excelData = POIUtils.readExcel(file);
                    carService.importDriver(excelData, importRecord,user.getId());
                    return 1;
                } catch (Exception ex) {
                    importRecord.setImportStatus(false);
                    importRecord.setReason("导入错误，请检查数据");
                    log.error("导入错误", ex);
                    return ex;
                }
            }
        });
        try {
            Object o = submit.get(2, TimeUnit.SECONDS);
            if (o instanceof UnsupportedOperationException) {
                throw (UnsupportedOperationException) o;
            }
        } catch (TimeoutException ex) {
            throw new UnsupportedOperationException("数据量比较大或查询较慢，将转为后台执行，请稍后刷新");
        }
        return this.resultMap(true);
    }

    @RequestMapping("/export")
    public void loadDrivers(String cardNumber, Status status, int importId, HttpServletResponse response) throws IOException {
        User user = SecurityUtils.currentUser();
        List<Car> cars = carService.loadCars(cardNumber, status, user.getId(), importId);
        CarExportCommand exportCommand = new CarExportCommand(response, "车辆信息", cars);
        exportCommand.export();
    }

    @RequestMapping("/clear")
    public Map<String, Object> clear() throws IOException {
        carService.clear();
        return this.resultMap(true);
    }

    @RequestMapping("/batchQuery")
    public Map<String, Object> batchQuery(int importId) throws IOException, ExecutionException, InterruptedException {
        User user = SecurityUtils.currentUser();
        synchronized (user) {
            Future<Object> submit = executor.submit(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    try {
                        carService.batchQuery(importId, user.getId());
                        return 1;
                    } catch (UnsupportedOperationException ex) {
                        return ex;
                    }

                }
            });
            try {
                Object object = submit.get(2, TimeUnit.SECONDS);
                if (object instanceof UnsupportedOperationException) {
                    throw (UnsupportedOperationException) object;
                }
            } catch (TimeoutException e) {
                throw new UnsupportedOperationException("数据量比较大，将转为后台执行，请稍后查看");
            }
        }
        return this.resultMap(true);
    }
}
