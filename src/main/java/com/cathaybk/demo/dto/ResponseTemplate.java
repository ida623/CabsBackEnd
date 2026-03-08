package com.cathaybk.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 下行電文包裝模板
 *
 * @author 00550396
 * @param <T> TRANRS 類型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseTemplate<T> implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    /** 電文表頭 */
    @JsonProperty("MWHEADER")
    private MwHeader mwheader;

    /** 下行電文內容 */
    @JsonProperty("TRANRS")
    private T tranrs;

}
