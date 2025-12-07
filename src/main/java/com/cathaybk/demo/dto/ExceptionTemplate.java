package com.cathaybk.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class ExceptionTemplate implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("RETURNCODE")
    private String returnCode;

    @JsonProperty("RETURNDESC")
    private String returnDesc;
}