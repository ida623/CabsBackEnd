package com.cathaybk.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CABS-B-ANNQ001 SQL 查詢結果 mapping
 * @author
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ANNQ001QueryResult {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    @JsonProperty("totalElements")
    private Long totalElements;

    /** 公告 ID */
    @JsonProperty("id")
    private Long id;

    /** 公告內容 */
    @JsonProperty("content")
    private String content;

    /** 建立者姓名 */
    @JsonProperty("createdBy")
    private String createdBy;

    /** 建立時間 */
    @JsonProperty("createdAt")
    private String createdAt;

    /** 最後異動者姓名 */
    @JsonProperty("updatedBy")
    private String updatedBy;

    /** 最後異動時間 */
    @JsonProperty("updatedAt")
    private String updatedAt;

}