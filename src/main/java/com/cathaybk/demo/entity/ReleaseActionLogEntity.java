package com.cathaybk.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * RELEASE_ACTION_LOG 放行處理結果紀錄
 * @author System
 */
@Entity
@Data
@Table(name = "RELEASE_ACTION_LOG")
public class ReleaseActionLogEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主鍵ID */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RELEASE_ACTION_LOG_SEQ_GEN")
    @SequenceGenerator(
            name = "RELEASE_ACTION_LOG_SEQ_GEN",
            sequenceName = "RELEASE_ACTION_LOG_SEQ",
            allocationSize = 1
    )
    @Column(name = "ID", nullable = false, precision = 19)
    private Long id;

    /** 聯繫單號 */
    @Column(name = "EFORM_ID", nullable = false, length = 17)
    private String eformId;

    /** 動作：PASS/REJECT/RESEND */
    @Column(name = "ACTION_TYPE", nullable = false, length = 10)
    private String actionType;

    /** 操作前狀態：Y/N */
    @Column(name = "BEFORE_STATUS", length = 10)
    private String beforeStatus;

    /** 操作後狀態：Y/N */
    @Column(name = "AFTER_STATUS", length = 10)
    private String afterStatus;

    /** 操作者姓名 */
    @Column(name = "ACTION_BY", nullable = false, length = 10)
    private String actionBy;

    /** 來源：MANUAL/BATCH/RESEND */
    @Column(name = "REQUEST_SOURCE", nullable = false, length = 10)
    private String requestSource;

    /** iContact 回傳狀態代碼 */
    @Column(name = "ICONTACT_REF_STATUS", length = 100)
    private String iContactRefStatus;

    /** iContact 回傳訊息 */
    @Column(name = "ICONTACT_REF_MESSAGE", length = 100)
    private String iContactRefMessage;

    /** 操作時間 */
    @Column(name = "ACTION_AT", nullable = false)
    private LocalDateTime actionAt;

    /** 審核完成時間 */
    @Column(name = "APPROVED_AT")
    private LocalDateTime approvedAt;

}