package com.cathaybk.demo.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CABS-B-RLSQ001 產生放行清單 下行/回應電文 項目
 * @author System
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RLSQ001TranrsItem implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    /** 聯繫單號 */
    @JsonProperty("eformId")
    private String eformId;

    /** 變更執行起日 */
    @JsonProperty("executeDate")
    private String executeDate;

    /** 變更執行迄日 */
    @JsonProperty("endDate")
    private String endDate;

    /** 申報人id */
    @JsonProperty("createdEmpid")
    private String createdEmpid;

    /** 申報人姓名 */
    @JsonProperty("createdEmpName")
    private String createdEmpName;

    /** 申報單位 */
    @JsonProperty("createdDept")
    private String createdDept;

    /** 狀態 */
    @JsonProperty("status")
    private String status;

    /** 變更內容 */
    @JsonProperty("changeDetails")
    private String changeDetails;

    /** APID */
    @JsonProperty("apid")
    private String apid;

    /** 系統名稱 */
    @JsonProperty("sysName")
    private String sysName;
}
