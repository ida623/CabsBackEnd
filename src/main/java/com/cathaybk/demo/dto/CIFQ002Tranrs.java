package com.cathaybk.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * XXA-C-CIFQ002 條件過濾查詢客戶回應
 */
@Data
public class CIFQ002Tranrs implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("pageSize")
    private int pageSize;

    @JsonProperty("pageNumber")
    private int pageNumber;

    @JsonProperty("totalPage")
    private int totalPage;

    @JsonProperty("totalCount")
    private int totalCount;

    @JsonProperty("ITEMS")
    private List<CIFQ002TranrsItems> items;
}