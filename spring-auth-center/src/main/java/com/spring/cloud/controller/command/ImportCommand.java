package com.spring.cloud.controller.command;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spring.cloud.base.Command;
import com.spring.cloud.entity.ImportRecord;
import com.spring.cloud.entity.ImportType;
import com.spring.cloud.entity.QueryStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ImportCommand  implements Command<ImportRecord> {
    private int id;
    @JsonFormat(pattern="yyyy-MM-dd",timezone = "GMT+8")
    private Date createDate;
    private int importCount;
    private int usedTimes;
    private int queryCount;
    private  int invalidCount;
    private QueryStatus queryStatus;
    ImportType type;
    @Override
    public ImportRecord toDomain() {
        return null;
    }

    @Override
    public Command<ImportRecord> fromDomain(ImportRecord domain) {
        this.importCount = domain.getImportCount();
        this.usedTimes = domain.getUsedTimes();
        this.queryCount = domain.getQueryCount();
        this.type = domain.getType();
        this.createDate = domain.getCreateDate();
        this.id = domain.getId();
        this.invalidCount = domain.getInvalidCount();
        this.queryStatus = domain.getQueryStatus();
        return this;
    }
}
