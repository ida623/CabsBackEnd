package com.cathaybk.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 放行清單 SQL 查詢結果 mapping（RLSQ001 / RLSQ002 共用）
 * @author System
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RLSQueryEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 聯繫單號 */
    @JsonProperty("EFORM_ID")
    private String eformId;

    /** 變更執行起日（yyyy/MM/dd HH:mm:ss） */
    @JsonProperty("EXECUTE_DATE")
    private String executeDate;

    /** 變更執行迄日（yyyy/MM/dd HH:mm:ss） */
    @JsonProperty("END_DATE")
    private String endDate;

    /** 申報人ID */
    @JsonProperty("CREATED_EMPID")
    private String createdEmpid;

    /** 申報人姓名 */
    @JsonProperty("CREATED_EMPNAME")
    private String createdEmpName;

    /** 申報單位 */
    @JsonProperty("CREATED_DEPT")
    private String createdDept;

    /** 狀態（AFTER_STATUS） */
    @JsonProperty("STATUS")
    private String status;

    /** 變更內容 */
    @JsonProperty("CHANGE_DETAILS")
    private String changeDetails;

    /** APID */
    @JsonProperty("APID")
    private String apid;

    /** 系統名稱 */
    @JsonProperty("SYS_NAME")
    private String sysName;

}