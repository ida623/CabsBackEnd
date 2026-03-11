package com.cathaybk.demo.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "DATE_SETTING_WINDOW")
public class DataSettingWindowEntity implements Serializable {

private static final long serialVersionUID = 1L;

/**
* 主鍵ID
*/
@Id
@Column(name = "ID", nullable = false, precision = 19)
private Long id;

/**
* 預計執行變更區間－起日
*/
@Column(name = "WINDOW_START_DATE", nullable = false)
private LocalDate windowStartDate;

/**
* 預計執行變更區間－迄日
*/
@Column(name = "WINDOW_END_DATE", nullable = false)
private LocalDate windowEndDate;

/**
* 截止申請日（日期）
*/
@Column(name = "CUTOFF_DATE", nullable = false)
private LocalDateTime cutoffDate;

/**
* 建立者姓名
*/
@Column(name = "CREATED_BY", nullable = false, length = 10)
private String createdBy;

/**
* 建立時間（用於判斷最新一筆）
*/
@Column(name = "CREATED_AT", nullable = false, updatable = false)
private LocalDateTime createdAt;

}