package com.cathaybk.demo.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * RELEASE_ACTION_LOG 放行處理結果紀錄
 *
 * @author 00550396
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "RELEASE_ACTION_LOG")
public class ReleaseActionLogEntity implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    /** 聯繫單號 */
    @Column(name = "EFORM_ID", length = 17, nullable = false)
    private String eformId;

    /** 動作類型 */
    @Column(name = "ACTION_TYPE", length = 20)
    private String actionType;

    /** 變更前狀態 */
    @Column(name = "BEFORE_STATUS", length = 10)
    private String beforeStatus;

    /** 變更後狀態 */
    @Column(name = "AFTER_STATUS", length = 10)
    private String afterStatus;

    /** 操作人 */
    @Column(name = "ACTION_BY", length = 50)
    private String actionBy;

    /** 請求來源 */
    @Column(name = "REQUEST_SOURCE", length = 50)
    private String requestSource;

    /** 操作時間 */
    @Column(name = "ACTION_AT")
    private LocalDateTime actionAt;

    /** 核准時間 */
    @Column(name = "APPROVED_AT")
    private LocalDateTime approvedAt;

    /** iContact 參考狀態 */
    @Column(name = "ICONTACT_REF_STATUS", length = 10)
    private String icontactRefStatus;

    /** iContact 參考訊息 */
    @Column(name = "ICONTACT_REF_MESSAGE", length = 500)
    private String icontactRefMessage;

}