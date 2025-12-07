package com.cathaybk.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * XXA-C-CIFQ003 檢查身份證號是否存在
 */
@Data
public class CIFQ003Tranrq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 身份證號
     */
    @NotBlank(message = "idNum為必填欄位")
    @JsonProperty("idNum")
    private String idNum;
}