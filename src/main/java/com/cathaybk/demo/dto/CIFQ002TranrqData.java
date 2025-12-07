package com.cathaybk.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * XXA-C-CIFQ002 查詢條件資料
 */
@Data
public class CIFQ002TranrqData implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("idNum")
    private String idNum;

    @JsonProperty("chineseName")
    private String chineseName;

    @JsonProperty("gender")
    private String gender;

    @JsonProperty("education")
    private String education;

    @JsonProperty("mobile")
    private String mobile;

    @JsonProperty("email")
    private String email;

    @JsonProperty("year")
    private Integer year;
}