package com.cathaybk.demo.dto;

import java.io.Serial;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CABS-B-DATQ001 查詢日期設定
 * 下行／回應電文
 * @author system
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DATQ001Tranrs implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    /**
     * 預計執行變更區間－起日
     * 格式: YYYY/MM/DD
     */
    private String windowStartDate;

    /**
     * 預計執行變更區間－迄日
     * 格式: YYYY/MM/DD
     */
    private String windowEndDate;

    /**
     * 截止申請日時間
     * 格式: YYYY/MM/DD HH:mm
     */
    private String cutoffDate;

}