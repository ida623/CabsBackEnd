package com.cathaybk.demo.dto;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CABS-B-CODQ001 代碼查詢 下行
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CODQ001Tranrs implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    /**
     * 下拉選項清單
     */
    private List<CODQ001TranrsItem> items;

}