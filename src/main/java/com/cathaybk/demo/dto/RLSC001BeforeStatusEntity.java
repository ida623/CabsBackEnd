package com.cathaybk.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * RLSC001 @SQL1 查詢前次狀態 mapping
 * @author System
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RLSC001BeforeStatusEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 聯繫單號 */
    @JsonProperty("EFORM_ID")
    private String eformId;

    /** 前次最新狀態（Y/N） */
    @JsonProperty("STATUS")
    private String status;

}