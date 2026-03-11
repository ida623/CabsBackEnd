package com.cathaybk.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * CABS-B-ANNQ001 SQL 查詢結果 mapping
 * @author
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ANNQ001TranrsEntity implements Serializable {

    /** serialVersionUID */
    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("TOTAL_ELEMENTS")
    private Long totalElements;

    /** 公告 ID */
    @JsonProperty("ID")
    private Long id;

    /** 公告內容 */
    @JsonProperty("CONTENT")
    private String content;

    /** 建立者姓名 */
    @JsonProperty("CREATED_BY")
    private String createdBy;

    /** 建立時間（yyyy/MM/dd HH:mm:ss） */
    @JsonProperty("CREATED_AT")
    private String createdAt;

    /** 最後異動者姓名 */
    @JsonProperty("UPDATED_BY")
    private String updatedBy;

    /** 最後異動時間（yyyy/MM/dd HH:mm:ss） */
    @JsonProperty("UPDATED_AT")
    private String updatedAt;

}