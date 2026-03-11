package com.cathaybk.demo.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CABS-B-RLSC001 放行處理 上行／請求電文
 * @author System
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RLSC001Tranrq implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    /** 動作：PASS / REJECT */
    @NotBlank(message = "動作 不得為空")
    @Pattern(regexp = "^(PASS|REJECT)$", message = "動作 必須為 PASS 或 REJECT")
    @JsonProperty("actionType")
    private String actionType;

    /** 來源：MANUAL / BATCH / RESEND */
    @NotBlank(message = "來源 不得為空")
    @Pattern(regexp = "^(MANUAL|BATCH|RESEND)$", message = "來源 必須為 MANUAL、BATCH 或 RESEND")
    @JsonProperty("requestSource")
    private String requestSource;

    /** 審核完成時間 */
    @NotBlank(message = "審核完成時間 不得為空")
    @Size(max = 8, message = "審核完成時間 最大長度為8")
    @JsonProperty("approvedAt")
    private String approvedAt;

    /** 聯繫單號清單 */
    @NotEmpty(message = "聯繫單號清單 不得為空")
    @JsonProperty("eformIds")
    private List<String> eformIds;
}
