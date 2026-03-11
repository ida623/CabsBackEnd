package com.cathaybk.demo.dto;

import java.io.Serial;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * CABS-B-ANND001 刪除公告 上行
 * 上行／請求電文
 *
 * @author
 */
@Data
public class ANND001Tranrq implements Serializable {

    /** serialVersionUID */
    @Serial
    private static final long serialVersionUID = 1L;

    /** 公告ID */
    @NotNull(message = "公告ID不可為空")
    @Min(value = 1, message = "公告ID必須為正整數")
    @JsonProperty("id")
    private Long id;

}