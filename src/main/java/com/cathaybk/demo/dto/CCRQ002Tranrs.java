package com.cathaybk.demo.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CABS-B-CCRQ002 查詢聯繫單 下行/回應電文
 *
 * @author 00550396
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CCRQ002Tranrs implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    /** 聯繫單號 */
    @JsonProperty("eformId")
    private String eformId;

    /** APID */
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

    /** 變更內容 */
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

    /** 需要中斷或重啟（0/1） */
    @JsonProperty("isDowntimeRequires")
    private String isDowntimeRequires;

    /** 影響資料庫異動（0/1） */
    @JsonProperty("isChangedb")
    private String isChangedb;

    /** 需要公告（0/1） */
    @JsonProperty("isAnn")
    private String isAnn;

    /** 程式人員分機 */
    @JsonProperty("programPhone")
    private String programPhone;

    /** 受影響範圍 */
    @JsonProperty("affectedNotes")
    private String affectedNotes;

    /** HRI項目 */
    @JsonProperty("hri")
    private String hri;

    /** 申報人 */
    @JsonProperty("createdEmpid")
    private String createdEmpid;

    /** 申報人姓名 */
    @JsonProperty("createdEmpName")
    private String createdEmpName;

    /** 申報單位（名稱） */
    @JsonProperty("createdDept")
    private String createdDept;

    /** 申報人分機 */
    @JsonProperty("createdPhoneExt")
    private String createdPhoneExt;

    /** 申報日期（yyyy/MM/dd HH:mm:ss） */
    @JsonProperty("createdDate")
    private String createdDate;

    /** 更新日期（yyyy/MM/dd HH:mm:ss） */
    @JsonProperty("modifyDate")
    private String modifyDate;

    /** 是否有效（Y/N） */
    @JsonProperty("isActive")
    private String isActive;

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
    @JsonProperty("isAiCab")
    private String isAiCab;

    /** AI CAB 聯繫單號 */
    @JsonProperty("aiCabEformId")
    private String aiCabEformId;

}