package com.cathaybk.demo.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CABS-B-CODQ001 代碼查詢
 * 下行/回應電文
 *
 * @author
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CODQ001Tranrs implements Serializable {

    /** serialVersionUID */
    @Serial
    private static final long serialVersionUID = 1L;

    /** 下拉選項清單 */
    @JsonProperty("items")
    private List<CODQ001TranrsItem> items;

}