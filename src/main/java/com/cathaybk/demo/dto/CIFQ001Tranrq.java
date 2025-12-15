package com.cathaybk.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * XXA-C-CIFQ001 根據orderId查詢
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
    private Long orderId; // TODO 流水號 建議接 Long，可以接更長的數字
    // Integer a = 7; Long b = 7L;
}