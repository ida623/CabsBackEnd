package com.cathaybk.demo.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CABS-B-CCRQ001 查詢聯繫單列表 上行／請求電文
 *
 * @author 00550396
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CCRQ001Tranrq implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    /** 聯繫單號 */
    @JsonProperty("eformId")
    private String eformId;

    /** BIA 等級ID */
    @JsonProperty("biaLevelId")
    private String biaLevelId;

    /** 變更執行起日(起)（yyyy/MM/dd HH:mm:ss） */
    @JsonProperty("executeDate")
    private String executeDate;

    /** 變更執行迄日(迄)（yyyy/MM/dd HH:mm:ss） */
    @JsonProperty("endDate")
    private String endDate;

    /** 變更種類ID */
    @JsonProperty("changeTypeId")
    private String changeTypeId;

    /** 工作項目性質ID */
    @JsonProperty("attributesId")
    private String attributesId;

    /** 申報人姓名 */
    @JsonProperty("createdEmpName")
    private String createdEmpName;

    /** 申報單位 */
    @JsonProperty("createdDept")
    private String createdDept;

}