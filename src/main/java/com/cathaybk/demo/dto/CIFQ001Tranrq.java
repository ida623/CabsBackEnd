package com.cathaybk.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * XXA-C-CIFQ001 根據orderId查詢客戶
 */
@Data
public class CIFQ001Tranrq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 流水號
     */
    @NotNull(message = "orderId為必填欄位")
    @JsonProperty("orderId")
    private Integer orderId;
}