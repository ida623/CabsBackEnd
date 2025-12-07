package com.cathaybk.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * EDU_C_CUSTQ001 根據客戶編號查詢客戶
 */
@Data
public class Page implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * pageNumber
     */
    @NotNull(message = "pageNumber為必填欄位")
    @JsonProperty("pageNumber")
    private Integer pageNumber;

    /**
     * pageSize
     */
    @NotNull(message = "pageSize為必填欄位")
    @JsonProperty("pageSize")
    private Integer pageSize;

}
