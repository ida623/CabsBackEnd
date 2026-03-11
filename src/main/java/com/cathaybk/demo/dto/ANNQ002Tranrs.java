package com.cathaybk.demo.dto;

import java.io.Serial;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CABS-B-ANNQ002 查詢公告
 * 下行/回應電文
 * @author
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ANNQ002Tranrs implements Serializable {

    /** serialVersionUID */
    @Serial
    private static final long serialVersionUID = 1L;

    /** 公告 ID */
    @JsonProperty("id")
    private Long id;

    /** 公告內容 */
    @JsonProperty("content")
    private String content;

    /** 建立者姓名 */
    @JsonProperty("createdBy")
    private String createdBy;

    /** 建立時間（yyyy/MM/dd HH:mm:ss） */
    @JsonProperty("createdAt")
    private String createdAt;

    /** 最後異動者姓名 */
    @JsonProperty("updatedBy")
    private String updatedBy;

    /** 最後異動時間（yyyy/MM/dd HH:mm:ss） */
    @JsonProperty("updatedAt")
    private String updatedAt;

}