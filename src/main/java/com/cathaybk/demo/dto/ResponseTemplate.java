package com.cathaybk.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * EDU_C_CUSTQ001 根據客戶編號查詢客戶
 */
@Data
public class ResponseTemplate<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("MWHEADER")
    private HEADER mwheader;

    @JsonProperty("TRANRS")
    private T tranrs;
}
