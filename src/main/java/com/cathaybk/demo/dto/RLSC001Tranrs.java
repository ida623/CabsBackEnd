package com.cathaybk.demo.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CABS-B-RLSC001 放行處理 下行/回應電文
 *
 * @author System
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RLSC001Tranrs implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    /** 回寫是否成功（Y/N） */
    @JsonProperty("iContactCallSuccess")
    private String iContactCallSuccess;

    /** 回傳碼 */
    @JsonProperty("iContactReturnCode")
    private String iContactReturnCode;

    /** 回傳訊息 */
    @JsonProperty("iContactReturnDesc")
    private String iContactReturnDesc;
}
