package com.cathaybk.demo.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CABS-B-ANNQ001 查詢公告清單
 * 下行/回應電文
 *
 * @author
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ANNQ001Tranrs implements Serializable {

    /** serialVersionUID */
    @Serial
    private static final long serialVersionUID = 1L;

    /** 本次回傳頁碼 */
    @JsonProperty("pageNumber")
    private Integer pageNumber;

    /** 本次回傳每頁筆數 */
    @JsonProperty("pageSize")
    private Integer pageSize;

    /** 總筆數 */
    @JsonProperty("totalElements")
    private Long totalElements;

    /** 總頁數 */
    @JsonProperty("totalPages")
    private Integer totalPages;

    /** 公告清單 */
    @JsonProperty("items")
    private List<ANNQ001TranrsItem> items;

}