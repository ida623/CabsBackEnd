package com.cathaybk.demo.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CABS-B-CCRQ001 查詢聯繫單列表 下行/回應電文
 *
 * @author 00550396
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CCRQ001Tranrs implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    /** 聯繫單清單（當頁資料） */
    @JsonProperty("items")
    private List<CCRQ001TranrsItem> items;

}