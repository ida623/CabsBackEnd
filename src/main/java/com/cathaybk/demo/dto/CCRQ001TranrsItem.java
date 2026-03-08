package com.cathaybk.demo.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CABS-B-CCRQ001 查詢聯繫單列表 下行/回應電文項目
 *
 * @author 00550396
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CCRQ001TranrsItem implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    /** 聯繫單號 */
    @JsonProperty("eformId")
    private String eformId;

    /** AP ID */
    @JsonProperty("apid")
    private String apid;

    /** 系統名稱 */
    @JsonProperty("sysName")
    private String sysName;

    /** BIA 等級ID */
    @JsonProperty("biaLevelId")
    private String biaLevelId;

    /** 變更執行起日（yyyy/MM/dd HH:mm:ss） */
    @JsonProperty("executeDate")
    private String executeDate;

    /** 變更執行迄日（yyyy/MM/dd HH:mm:ss） */
    @JsonProperty("endDate")
    private String endDate;

    /** 變更內容摘要 */
    @JsonProperty("changeDetails")
    private String changeDetails;

    /** 變更種類ID */
    @JsonProperty("changeTypeId")
    private String changeTypeId;

    /** 風險評估ID */
    @JsonProperty("riskAssessmentId")
    private String riskAssessmentId;

    /** 工作項目性質ID */
    @JsonProperty("attributesId")
    private String attributesId;

    /** 是否需停機（0/1） */
    @JsonProperty("isDowntimeRequires")
    private String isDowntimeRequires;

    /** 是否變更資料庫（0/1） */
    @JsonProperty("isChangedb")
    private String isChangedb;

    /** 是否公告（0/1） */
    @JsonProperty("isAnn")
    private String isAnn;

    /** 程式聯絡電話 */
    @JsonProperty("programPhone")
    private String programPhone;

    /** 影響範圍備註 */
    @JsonProperty("affectedNotes")
    private String affectedNotes;

    /** 建立人員工編號 */
    @JsonProperty("createdEmpId")
    private String createdEmpId;

    /** 建立人分機 */
    @JsonProperty("createdPhoneExt")
    private String createdPhoneExt;

    /** 建立人姓名 */
    @JsonProperty("createdEmpName")
    private String createdEmpName;

    /** 建立日期（yyyy/MM/dd HH:mm:ss） */
    @JsonProperty("createdDate")
    private String createdDate;

    /** 建立人單位 */
    @JsonProperty("createdDept")
    private String createdDept;

    /** 修改日期（yyyy/MM/dd HH:mm:ss） */
    @JsonProperty("modifyDate")
    private String modifyDate;

    /** HRI（JSON String） */
    @JsonProperty("hri")
    private String hri;

    /** 審核狀態（Y/N） */
    @JsonProperty("reviewStatus")
    private String reviewStatus;

    /** 審核日期（yyyy/MM/dd HH:mm:ss） */
    @JsonProperty("reviewDate")
    private String reviewDate;

    /** 審核修改人員工編號 */
    @JsonProperty("reviewModifyEmpId")
    private String reviewModifyEmpId;

    /** 審核修改時間（yyyy/MM/dd HH:mm:ss） */
    @JsonProperty("reviewModifyTime")
    private String reviewModifyTime;

    /** 是否為AI CAB（0/1） */
    @JsonProperty("isAiCab")
    private String isAiCab;

    /** AI CAB聯繫單號 */
    @JsonProperty("aiCabEformId")
    private String aiCabEformId;

    /** 是否啟用（Y/N） */
    @JsonProperty("isActive")
    private String isActive;

    /** 狀態（RELEASE_ACTION_LOG 最新 AFTER_STATUS） */
    @JsonProperty("status")
    private String status;

}