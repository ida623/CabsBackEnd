package com.cathaybk.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 排序資訊
 */
@Data
public class SortInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 排序方式 (ASC/DESC)
     */
    @JsonProperty("sortBy")
    private String sortBy;

    /**
     * 排序欄位
     */
    @JsonProperty("sortColumn")
    private String sortColumn;
}