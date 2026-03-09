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
 *
 * @author system
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DATQ001Tranrs implements Serializable {

    /** serialVersionUID */
    @Serial
    private static final long serialVersionUID = 1L;

    /** 預計執行變更區間－起日 */
    @JsonProperty("windowStartDate")
    private String windowStartDate;

    /** 預計執行變更區間－迄日 */
    @JsonProperty("windowEndDate")
    private String windowEndDate;

    /** 截止申請日時間（YYYY/MM/DD HH:mm） */
    @JsonProperty("cutoffDate")
    private String cutoffDate;
}