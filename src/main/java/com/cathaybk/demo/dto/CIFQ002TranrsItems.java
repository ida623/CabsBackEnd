package com.cathaybk.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * XXA-C-CIFQ002 條件過濾查詢客戶項目
 */
@Data
public class CIFQ002TranrsItems implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("orderId")
    private Integer orderId;

    @JsonProperty("idNum")
    private String idNum;

    @JsonProperty("chineseName")
    private String chineseName;

    @JsonProperty("gender")
    private String gender;

    @JsonProperty("education")
    private String education;

    @JsonProperty("zipCode1")
    private String zipCode1;

    @JsonProperty("address1")
    private String address1;

    @JsonProperty("telephone1")
    private String telephone1;

    @JsonProperty("zipCode2")
    private String zipCode2;

    @JsonProperty("address2")
    private String address2;

    @JsonProperty("telephone2")
    private String telephone2;

    @JsonProperty("mobile")
    private String mobile;

    @JsonProperty("email")
    private String email;

    @JsonProperty("year")
    private Integer year;
}