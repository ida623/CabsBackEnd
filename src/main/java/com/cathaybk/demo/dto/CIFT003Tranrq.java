package com.cathaybk.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * XXA-C-CIFT003 刪除客戶資料
 */
@Data
public class CIFT003Tranrq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "orderId為必填欄位")
    @JsonProperty("orderId")
    private Long orderId;
}