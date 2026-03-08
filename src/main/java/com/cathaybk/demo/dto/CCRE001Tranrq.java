package com.cathaybk.demo.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CABS-B-CCRE001 匯出聯繫單清單 上行／請求電文
 *
 * @author 00550396
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CCRE001Tranrq implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    /** 聯繫單清單（當頁資料） */
    @NotNull(message = "items is required")
    @JsonProperty("items")
    private List<CCRQ001TranrsItem> items;

}