package com.spring.cloud.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.persistence.Entity;
import java.util.Date;

@Entity
@Table(name = "import_record")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter
@Setter
public class ImportRecord  extends IntIdBaseEntity{

    private int importCount;
    private int usedTimes;
    private int queryCount;
    @Enumerated(value = EnumType.STRING)
    ImportType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;


    public void addQueryCount() {
        this.queryCount++;
    }
}
