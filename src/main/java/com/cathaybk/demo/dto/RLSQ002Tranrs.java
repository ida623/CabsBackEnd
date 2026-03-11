package com.cathaybk.demo.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CABS-B-RLSQ002 匯入EXCEL產生放行清單 下行/回應電文
 * @author System
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RLSQ002Tranrs implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    /** 聯繫單清單 */
    @JsonProperty("items")
    private List<RLSQ001TranrsItem> items;
}
