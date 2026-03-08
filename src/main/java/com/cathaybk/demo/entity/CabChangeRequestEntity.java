package com.cathaybk.demo.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CAB_CHANGE_REQUEST 聯繫單主檔
 *
 * @author 00550396
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "CAB_CHANGE_REQUEST")
public class CabChangeRequestEntity implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    /** 聯繫單號 */
    @Id
    @Column(name = "EFORM_ID", length = 17, nullable = false)
    private String eformId;

    /** APID */
    @Column(name = "APID", length = 13)
    private String apid;

    /** 系統名稱 */
    @Column(name = "SYS_NAME", length = 50)
    private String sysName;

    /** BIA 等級ID */
    @Column(name = "BIA_LEVEL_ID", length = 5)
    private String biaLevelId;

    /** 變更執行起日 */
    @Column(name = "EXECUTE_DATE")
    private LocalDateTime executeDate;

    /** 變更執行迄日 */
    @Column(name = "END_DATE")
    private LocalDateTime endDate;

    /** 變更內容 */
    @Column(name = "CHANGE_DETAILS", length = 2000)
    private String changeDetails;

    /** 變更種類ID */
    @Column(name = "CHANGE_TYPE_ID", length = 5)
    private String changeTypeId;

    /** 風險評估ID */
    @Column(name = "RISK_ASSESSMENT_ID", length = 5)
    private String riskAssessmentId;

    /** 工作項目性質ID */
    @Column(name = "ATTRIBUTES_ID", length = 5)
    private String attributesId;

    /** 需要中斷或重啟（0/1） */
    @Column(name = "IS_DOWNTIME_REQUIRES", length = 1)
    private String isDowntimeRequires;

    /** 影響資料庫異動（0/1） */
    @Column(name = "IS_CHANGEDB", length = 1)
    private String isChangedb;

    /** 需要公告（0/1） */
    @Column(name = "IS_ANN", length = 1)
    private String isAnn;

    /** 程式人員分機 */
    @Column(name = "PROGRAM_PHONE", length = 20)
    private String programPhone;

    /** 受影響範圍 */
    @Column(name = "AFFECTED_NOTES", length = 2000)
    private String affectedNotes;

    /** 建立人員工編號 */
    @Column(name = "CREATED_EMPID", length = 10)
    private String createdEmpid;

    /** 建立人分機 */
    @Column(name = "CREATED_PHONE_EXT", length = 10)
    private String createdPhoneExt;

    /** 建立人姓名 */
    @Column(name = "CREATED_EMPNAME", length = 10)
    private String createdEmpname;

    /** 建立日期 */
    @Column(name = "CREATED_DATE")
    private LocalDateTime createdDate;

    /** 建立人單位 */
    @Column(name = "CREATED_DEPT", length = 100)
    private String createdDept;

    /** 修改日期 */
    @Column(name = "MODIFY_DATE")
    private LocalDateTime modifyDate;

    /** HRI（JSON String） */
    @Column(name = "HRI", length = 4000)
    private String hri;

    /** 審核狀態（Y/N） */
    @Column(name = "REVIEW_STATUS", length = 1)
    private String reviewStatus;

    /** 審核日期 */
    @Column(name = "REVIEW_DATE")
    private LocalDateTime reviewDate;

    /** 審核修改人員工編號 */
    @Column(name = "REVIEW_MODIFY_EMPID", length = 10)
    private String reviewModifyEmpid;

    /** 審核修改時間 */
    @Column(name = "REVIEW_MODIFY_TIME")
    private LocalDateTime reviewModifyTime;

    /** 是否為AI CAB（0/1） */
    @Column(name = "IS_AI_CAB", length = 1)
    private String isAiCab;

    /** AI CAB聯繫單號 */
    @Column(name = "AI_CAB_EFORM_ID", length = 17)
    private String aiCabEformId;

    /** 是否啟用（Y/N） */
    @Column(name = "IS_ACTIVE", length = 1)
    private String isActive;

}