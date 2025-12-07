package com.cathaybk.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * XXA-C-CIFQ004 查詢共用代碼回應
 */
@Data
public class CIFQ004Tranrs implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("Postal")
    private List<CIFQ004TranrsItems> postal;

    @JsonProperty("Education")
    private List<CIFQ004TranrsItems> education;
}