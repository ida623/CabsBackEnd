package com.cathaybk.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * XXA-C-CIFQ002 條件過濾查詢客戶
 */
@Data
public class CIFQ002Tranrq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 分頁資訊
     */
    @Valid
    @NotNull(message = "PAGE為必填欄位")
    @JsonProperty("PAGE")
    private Page page;

    /**
     * 查詢條件
     */
    @JsonProperty("DATA")
    private CIFQ002TranrqData data;

    /**
     * 排序資訊
     */
    @JsonProperty("SORTINFO")
    private SortInfo sortInfo;
}