package com.cathaybk.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * RELEASE_ACTION_LOG 放行處理結果紀錄
 *
 * @author System
 */
@Data
@Entity
@Table(name = "RELEASE_ACTION_LOG")
public class ReleaseActionLogEntity implements Serializable {

    /** serialVersionUID */
    @Serial
    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RELEASE_ACTION_LOG_SEQ_GEN")
    @SequenceGenerator(name = "RELEASE_ACTION_LOG_SEQ_GEN", sequenceName = "RELEASE_ACTION_LOG_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    /** 聯繫單號 */
    @Column(name = "EFORM_ID", length = 17, nullable = false)
    private String eformId;

    /** 動作類型 */
    @Column(name = "ACTION_TYPE", length = 10)
    private String actionType;

    /** 請求來源 */
    @Column(name = "REQUEST_SOURCE", length = 10)
    private String requestSource;

    /** 處理前狀態 */
    @Column(name = "BEFORE_STATUS", length = 10)
    private String beforeStatus;

    /** 處理後狀態 */
    @Column(name = "AFTER_STATUS", length = 10)
    private String afterStatus;

    /** 操作者 */
    @Column(name = "ACTION_BY", length = 50)
    private String actionBy;

    /** 操作時間 */
    @Column(name = "ACTION_AT")
    private Timestamp actionAt;

    /** 審核完成時間 */
    @Column(name = "APPROVED_AT", length = 8)
    private String approvedAt;

    /** iContact回寫狀態 */
    @Column(name = "ICONTACT_REF_STATUS", length = 10)
    private String iContactRefStatus;

    /** iContact回寫訊息 */
    @Column(name = "ICONTACT_REF_MESSAGE", length = 500)
    private String iContactRefMessage;
}
