package com.cathaybk.demo.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CABS-B-CCRC001 iContact聯繫單資料匯入 上行／請求電文
 *
 * @author 00550396
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CCRC001Tranrq implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    /** 聯繫單號（EFORM_ID） */
    @NotBlank(message = "eformId is required")
    @Size(min = 17, max = 17, message = "eformId length must be 17")
    @JsonProperty("eformId")
    private String eformId;

    /** 系統名稱（SYS_NAME） */
    @NotBlank(message = "sysName is required")
    @JsonProperty("sysName")
    private String sysName;

    /** APID */
    @NotBlank(message = "apid is required")
    @JsonProperty("apid")
    private String apid;

    /** BIA 等級ID */
    @NotBlank(message = "biaLevelId is required")
    @JsonProperty("biaLevelId")
    private String biaLevelId;

    /** 變更執行起日（yyyy/MM/dd HH:mm:ss） */
    @NotBlank(message = "executeDate is required")
    @JsonProperty("executeDate")
    private String executeDate;

    /** 變更執行迄日（yyyy/MM/dd HH:mm:ss） */
    @NotBlank(message = "endDate is required")
    @JsonProperty("endDate")
    private String endDate;

    /** 變更內容 */
    @NotBlank(message = "changeDetails is required")
    @JsonProperty("changeDetails")
    private String changeDetails;

    /** 變更種類ID */
    @NotBlank(message = "changeTypeId is required")
    @JsonProperty("changeTypeId")
    private String changeTypeId;

    /** 風險評估ID */
    @NotBlank(message = "riskAssessmentId is required")
    @JsonProperty("riskAssessmentId")
    private String riskAssessmentId;

    /** 工作項目性質ID */
    @NotBlank(message = "attributesId is required")
    @JsonProperty("attributesId")
    private String attributesId;

    /** 需要中斷或重啟（0/1） */
    @NotBlank(message = "isDowntimeRequires is required")
    @Pattern(regexp = "^[01]$", message = "isDowntimeRequires must be 0 or 1")
    @JsonProperty("isDowntimeRequires")
    private String isDowntimeRequires;

    /** 影響資料庫異動（0/1） */
    @NotBlank(message = "isChangedb is required")
    @Pattern(regexp = "^[01]$", message = "isChangedb must be 0 or 1")
    @JsonProperty("isChangedb")
    private String isChangedb;

    /** 需要公告（0/1） */
    @NotBlank(message = "isAnn is required")
    @Pattern(regexp = "^[01]$", message = "isAnn must be 0 or 1")
    @JsonProperty("isAnn")
    private String isAnn;

    /** 程式人員分機 */
    @NotBlank(message = "programPhone is required")
    @JsonProperty("programPhone")
    private String programPhone;

    /** 受影響範圍 */
    @JsonProperty("affectedNotes")
    private String affectedNotes;

    /** 申報人姓名 */
    @NotBlank(message = "createdEmpName is required")
    @JsonProperty("createdEmpName")
    private String createdEmpName;

    /** 申報人ID */
    @NotBlank(message = "createdEmpid is required")
    @JsonProperty("createdEmpid")
    private String createdEmpid;

    /** 申報日期（yyyy/MM/dd HH:mm:ss） */
    @NotBlank(message = "createdDate is required")
    @JsonProperty("createdDate")
    private String createdDate;

    /** 申報單位 */
    @NotBlank(message = "createdDept is required")
    @JsonProperty("createdDept")
    private String createdDept;

    /** 申報人分機 */
    @JsonProperty("createdPhoneExt")
    private String createdPhoneExt;

    /** 更新日期（yyyy/MM/dd HH:mm:ss） */
    @NotBlank(message = "modifyDate is required")
    @JsonProperty("modifyDate")
    private String modifyDate;

    /** 是否有效（Y/N；預設 Y） */
    @Pattern(regexp = "^[YN]$", message = "isActive must be Y or N")
    @JsonProperty("isActive")
    private String isActive;

    /** HRI JSON 字串 */
    @JsonProperty("hri")
    private String hri;

    /** 審核狀態（畫面狀態） */
    @JsonProperty("reviewStatus")
    private String reviewStatus;

    /** 審核日期（yyyy/MM/dd HH:mm:ss） */
    @JsonProperty("reviewDate")
    private String reviewDate;

    /** 審核異動人 */
    @JsonProperty("reviewModifyEmpid")
    private String reviewModifyEmpid;

    /** 審核異動時間（yyyy/MM/dd HH:mm:ss） */
    @JsonProperty("reviewModifyTime")
    private String reviewModifyTime;

    /** 變更系統涉及AI（0/1） */
    @NotBlank(message = "isAiCab is required")
    @Pattern(regexp = "^[01]$", message = "isAiCab must be 0 or 1")
    @JsonProperty("isAiCab")
    private String isAiCab;

    /** AI CAB 聯繫單號 */
    @JsonProperty("aiCabEformId")
    private String aiCabEformId;

}