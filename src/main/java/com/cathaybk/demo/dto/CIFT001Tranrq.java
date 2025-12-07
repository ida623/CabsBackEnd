package com.cathaybk.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * XXA-C-CIFT001 新增客戶資料
 */
@Data
public class CIFT001Tranrq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Valid
    @NotNull(message = "DATA為必填欄位")
    @JsonProperty("DATA")
    private CIFT001TranrqData data;
}