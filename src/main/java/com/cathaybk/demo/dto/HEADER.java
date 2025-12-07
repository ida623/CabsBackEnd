package com.cathaybk.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * EDU_C_CUSTQ001 根據客戶編號查詢客戶
 */
@Data
public class HEADER implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank
    @JsonProperty("MSGID")
    private String msgid;

    @JsonProperty("RETURNCODE")
    private String returncode;

    @JsonProperty("RETURNDESC")
    private String returndesc;
}
