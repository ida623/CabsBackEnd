package com.cathaybk.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "TB_COMMCODE")
@IdClass(MsgCodeId.class)
public class MsgCode {

    /**
     * 代碼
     */
    @Id
    @Column(name = "MSG_CODE")
    private String msgCode;

    /**
     * 代碼說明
     */
    @Column(name = "MSG_CODE_MEMO")
    private String msgCodeMemo;

    /**
     * 選項
     */
    @Column(name = "MSG_OPTION")
    private String msgOption;

    /**
     * 選項說明
     */
    @Column(name = "MSG_OPTION_MEMO")
    private String msgOptionMemo;

    /**
     * 代碼選項順序
     */
    @Id
    @Column(name = "MSG_OPTION_SERNO")
    private String msgOptionSerno;

    /**
     * 員工代碼
     */
    @Column(name = "UP_EMPID")
    private String upEmpid;

    /**
     * 異動日期
     */
    @Column(name = "UPD_DATE")
    private LocalDateTime updDate; // TODO Timestamp


}
