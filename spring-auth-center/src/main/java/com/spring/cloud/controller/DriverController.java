package com.spring.cloud.controller;

import com.spring.cloud.controller.command.DriverCommand;
import com.spring.cloud.controller.command.DriverExportCommand;
import com.spring.cloud.entity.*;
import com.spring.cloud.repository.ImportRecordRepository;
import com.spring.cloud.service.DriverService;
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
@RequestMapping("/driver")
@Slf4j
public class DriverController extends ImportController {
    @Autowired
    private DriverService driverService;

    @Autowired
    private ImportRecordRepository importRecordRepository;

    @Autowired
    private UserService userService;

    @RequestMapping("/loadDrivers")
    public Map<String, Object> loadDrivers(String cardNumber, Status status, int importId, Integer page, Integer pageSize) {
        Sort sort = new Sort(Sort.Direction.DESC, "createDate");
        Pageable pageable = PageRequest.of(page, pageSize, sort);
        User user = SecurityUtils.currentUser();
        Page<Driver> driverList = driverService.loadDriversByPage(cardNumber, status, user.getId(), importId, pageable);
        return this.resultMap(CommandUtils.responsePage(driverList.getTotalElements(), driverList.getTotalPages(),
                CommandUtils.toCommands(driverList.getContent(), DriverCommand.class)));
    }

    @RequestMapping("/import")
    public Map<String, Object> loadDrivers(MultipartFile file) throws IOException, ExecutionException, InterruptedException {
        User user = SecurityUtils.currentUser();

        Future<Object> submit = executor.submit(new Callable<Object>() {
            @Override
            public Object call() {
                ImportRecord importRecord = new ImportRecord();
                importRecord.setType(ImportType.DRIVER);
                importRecord.setUser(userService.findUser(user.getId()));
                importRecord.setQueryStatus(QueryStatus.IMPORT);
                try {
                    List<String[]> excelData = POIUtils.readExcel(file);
                    driverService.importDriver(excelData, importRecord,user.getId());
                } catch (Exception ex) {
                    importRecord.setImportStatus(false);
                    importRecord.setReason("导入错误，请检查数据");
                    log.error("导入错误", ex);
                } finally {
                    if (importRecord.getId() <= 0) {
                        importRecord.setQueryStatus(QueryStatus.READY);
                        importRecordRepository.save(importRecord);
                    }
                }
                return 1;
            }
        });
        try {
            submit.get(2, TimeUnit.SECONDS);
        } catch (TimeoutException ex) {
            throw new UnsupportedOperationException("数据量比较大，将转为后台执行，请稍后刷新");
        }
        return this.resultMap(true);
    }

    @RequestMapping("/export")
    public void loadDrivers(String cardNumber, Status status, int importId, HttpServletResponse response) throws IOException {
        User user = SecurityUtils.currentUser();
        List<Driver> drivers = driverService.loadDrivers(cardNumber, status, user.getId(), importId);
        DriverExportCommand exportCommand = new DriverExportCommand(response, "司机信息", drivers);
        exportCommand.export();
    }

//    @RequestMapping("/clear")
//    public Map<String, Object> clear() throws IOException {
//        driverService.clear();
//        return this.resultMap(true);
//    }

    @RequestMapping("/batchQuery")
    public Map<String, Object> batchQuery(int importId) throws IOException, ExecutionException, InterruptedException {
        User user = SecurityUtils.currentUser();
        synchronized (SecurityUtils.currentUser()) {
            Future<Object> submit = executor.submit(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    try {
                        driverService.batchQuery(importId, user.getId());
                        return 1;
                    } catch (UnsupportedOperationException ex) {
                        return ex;
                    }
                }
            });
            try {
                Object object = submit.get(2, TimeUnit.SECONDS);
                if (object instanceof UnsupportedOperationException) {
                    throw (UnsupportedOperationException)object;
                }
            } catch (TimeoutException e) {
                throw new UnsupportedOperationException("数据量比较大，将转为后台执行，请稍后查看");
            }
        }
        return this.resultMap(true);
    }
}
