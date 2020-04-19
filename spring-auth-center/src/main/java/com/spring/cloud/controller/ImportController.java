package com.spring.cloud.controller;

import com.spring.cloud.base.BaseController;
import com.spring.cloud.controller.command.DriverCommand;
import com.spring.cloud.controller.command.ImportCommand;
import com.spring.cloud.entity.*;
import com.spring.cloud.service.ImportRecordService;
import com.spring.cloud.utils.CommandUtils;
import com.spring.cloud.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

public abstract class ImportController extends BaseController {
    @Autowired
    private ImportRecordService importRecordService;

    @RequestMapping("/loadImport")
    Map<String, Object> loadImport(ImportType type, Integer page, Integer pageSize) {
        Sort sort = new Sort(Sort.Direction.DESC, "createDate");

        Pageable pageable = PageRequest.of(page, pageSize,sort);
        User user = SecurityUtils.currentUser();
        Page<ImportRecord> importByPage = importRecordService.loadImportByPage(type,user.getId(),pageable);
        return this.resultMap(CommandUtils.responsePage(importByPage.getTotalElements(), importByPage.getTotalPages(),
                CommandUtils.toCommands(importByPage.getContent(), ImportCommand.class)));
    }
}
