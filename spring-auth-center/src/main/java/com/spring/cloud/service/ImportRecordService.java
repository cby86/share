package com.spring.cloud.service;

import com.spring.cloud.entity.ImportRecord;
import com.spring.cloud.entity.ImportType;
import com.spring.cloud.entity.QueryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ImportRecordService {
    Page<ImportRecord> loadImportByPage(ImportType type, int userId, QueryStatus queryStatus, Pageable pageable);
}
