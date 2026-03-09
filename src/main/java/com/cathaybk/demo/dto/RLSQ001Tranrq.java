package com.cathaybk.demo.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CABS-B-RLSQ001 產生放行清單 上行／請求電文
 *
 * @author System
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RLSQ001Tranrq implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    /** 聯繫單號清單 */
    @NotEmpty(message = "聯繫單號清單 不得為空")
    @Size(min = 1, max = 200, message = "聯繫單號清單 數量需介於1~200")
    @JsonProperty("eformIds")
    private List<String> eformIds;
}
