package com.cathaybk.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * XXA-C-CIFT002 修改客戶資料
 */
@Data
public class CIFT002Tranrq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Valid
    @NotNull(message = "DATA為必填欄位")
    @JsonProperty("DATA")
    private CIFT002TranrqData data;
}