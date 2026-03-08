package com.cathaybk.demo.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 電文表頭
 *
 * @author 00550396
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MwHeader implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    /** 訊息ID */
    @NotBlank(message = "MSGID is required")
    @JsonProperty("MSGID")
    private String msgid;

    /** 來源端系統AP ID */
    @NotBlank(message = "SOURCECHANNEL is required")
    @JsonProperty("SOURCECHANNEL")
    private String sourcechannel;

    /** 交易序號 */
    @NotBlank(message = "TXNSEQ is required")
    @JsonProperty("TXNSEQ")
    private String txnseq;

    /** 處理結果代碼 */
    @JsonProperty("RETURNCODE")
    private String returncode;

    /** 處理結果訊息 */
    @JsonProperty("RETURNDESC")
    private String returndesc;

    /** O360序號（選用） */
    @JsonProperty("O360SEQ")
    private String o360seq;

}