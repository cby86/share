package com.spring.cloud.service.impl;

import com.spring.cloud.entity.ImportRecord;
import com.spring.cloud.entity.ImportType;
import com.spring.cloud.repository.ImportRecordRepository;
import com.spring.cloud.service.ImportRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
public class ImportRecordServiceImpl implements ImportRecordService {
    @Autowired
    private ImportRecordRepository importRecordRepository;
    @Override
    public Page<ImportRecord> loadImportByPage(ImportType type, int userId, Pageable pageable) {
        return importRecordRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("deleted"), false));
            Join<Object, Object> user = root.join("user", JoinType.LEFT);
            if (type != null) {
                predicates.add(criteriaBuilder.equal(root.get("type"), type));
            }
            predicates.add(criteriaBuilder.equal(user.get("id"), userId));
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        }, pageable);
    }
}
