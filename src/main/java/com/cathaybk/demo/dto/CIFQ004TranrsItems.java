package com.cathaybk.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * XXA-C-CIFQ004 共用代碼項目
 */
@Data
public class CIFQ004TranrsItems implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("MsgOption")
    private String msgOption;

    @JsonProperty("MsgOptionMemo")
    private String msgOptionMemo;

    @JsonProperty("MsgOptionSerno")
    private String msgOptionSerno;
}