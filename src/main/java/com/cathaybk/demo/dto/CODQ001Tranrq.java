package com.cathaybk.demo.dto;
import java.io.Serializable;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * CABS-B-CODQ001 代碼查詢 上行
 */
@Data
public class CODQ001Tranrq implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    /**
     * 代碼類別（CODE_LOOKUP.CODE_TYPE）
     */
    @NotBlank(message = "代碼類別不可為空")
    @Size(max = 50, message = "代碼類別長度不可超過 50")
    private String codeType;

}