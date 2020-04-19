package com.spring.cloud.controller;

import com.spring.cloud.controller.command.DriverCommand;
import com.spring.cloud.controller.command.DriverExportCommand;
import com.spring.cloud.entity.Driver;
import com.spring.cloud.entity.Status;
import com.spring.cloud.entity.User;
import com.spring.cloud.service.DriverService;
import com.spring.cloud.utils.CommandUtils;
import com.spring.cloud.utils.POIUtils;
import com.spring.cloud.utils.SecurityUtils;
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

@RestController
@RequestMapping("/driver")
public class DriverController extends ImportController {
    @Autowired
    private DriverService driverService;


    @RequestMapping("/loadDrivers")
    public Map<String, Object> loadDrivers(String cardNumber, Status status,int importId, Integer page, Integer pageSize) {
        Sort sort = new Sort(Sort.Direction.DESC, "createDate");
        Pageable pageable = PageRequest.of(page, pageSize,sort);
        User user = SecurityUtils.currentUser();
        Page<Driver> driverList = driverService.loadDriversByPage(cardNumber, status, user.getId(),importId, pageable);
        return this.resultMap(CommandUtils.responsePage(driverList.getTotalElements(), driverList.getTotalPages(),
                CommandUtils.toCommands(driverList.getContent(), DriverCommand.class)));
    }

    @RequestMapping("/import")
    public Map<String, Object> loadDrivers(MultipartFile file) throws IOException {
        List<String[]> excelData = POIUtils.readExcel(file);
        List<Driver> drivers = new ArrayList<>();
        excelData.forEach(item -> {
            Driver driver = new Driver();
            driver.setStatus(Status.WARITIN);
            driver.setCardNumber(item[0]);
            driver.setUser(SecurityUtils.currentUser());
            drivers.add(driver);
        });
        driverService.importDriver(drivers);
        return this.resultMap(true);
    }

    @RequestMapping("/export")
    public void loadDrivers(String cardNumber, Status status,int importId ,HttpServletResponse response) throws IOException {
        User user = SecurityUtils.currentUser();
        List<Driver> drivers = driverService.loadDrivers(cardNumber, status, user.getId(),importId);
        DriverExportCommand exportCommand = new DriverExportCommand(response, "司机信息", drivers);
        exportCommand.export();
    }

//    @RequestMapping("/clear")
//    public Map<String, Object> clear() throws IOException {
//        driverService.clear();
//        return this.resultMap(true);
//    }

    @RequestMapping("/batchQuery")
    public Map<String, Object> batchQuery(int importId) throws IOException {
        synchronized (SecurityUtils.currentUser()) {
            driverService.batchQuery(importId);
        }
        return this.resultMap(true);
    }
}
