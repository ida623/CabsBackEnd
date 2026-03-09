package com.cathaybk.demo.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * 日期設定視窗 Entity
 *
 * @author system
 */
@Data
@Entity
@Table(name = "DATE_SETTING_WINDOW")
public class DateSettingWindowEntity implements Serializable {

    /** serialVersionUID */
    @Serial
    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "date_setting_window_seq")
    @SequenceGenerator(name = "date_setting_window_seq", sequenceName = "DATE_SETTING_WINDOW_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    /** 預計執行變更區間－起日 */
    @Column(name = "WINDOW_START_DATE", nullable = false)
    private LocalDate windowStartDate;

    /** 預計執行變更區間－迄日 */
    @Column(name = "WINDOW_END_DATE", nullable = false)
    private LocalDate windowEndDate;

    /** 截止申請日時間 */
    @Column(name = "CUTOFF_DATE", nullable = false)
    private LocalDateTime cutoffDate;

    /** 建立者 */
    @Column(name = "CREATED_BY", length = 50)
    private String createdBy;

    /** 建立時間 */
    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;
}