package com.cathaybk.demo.dto;

import java.io.Serial;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CABS-B-ANNQ002 查詢公告
 * 上行／請求電文
 *
 * @author
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ANNQ002Tranrq implements Serializable {

    /** serialVersionUID */
    @Serial
    private static final long serialVersionUID = 1L;

    /** 公告 ID */
    @NotNull(message = "公告ID 不得為空")
    @Min(value = 1, message = "公告ID 必須為正整數")
    @JsonProperty("id")
    private Long id;

}