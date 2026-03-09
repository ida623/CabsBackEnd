package com.cathaybk.demo.dto;

import java.io.Serial;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CABS-B-CODQ001 代碼查詢
 * 上行／請求電文
 *
 * @author
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CODQ001Tranrq implements Serializable {

    /** serialVersionUID */
    @Serial
    private static final long serialVersionUID = 1L;

    /** 代碼類別 */
    @NotBlank(message = "代碼類別 不得為空")
    @Size(max = 50, message = "代碼類別 最大長度為50")
    @JsonProperty("codeType")
    private String codeType;

}