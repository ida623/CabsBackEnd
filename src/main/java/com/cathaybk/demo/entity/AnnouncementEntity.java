package com.cathaybk.demo.entity;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * ANNOUNCEMENT 公告明細主檔
 *
 * @author
 */
@Data
@Entity
@Table(name = "ANNOUNCEMENT")
public class AnnouncementEntity implements Serializable {

    /** serialVersionUID */
    @Serial
    private static final long serialVersionUID = 1L;

    /** 公告 ID */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ANNOUNCEMENT_SEQ_GEN")
    @SequenceGenerator(
            name = "ANNOUNCEMENT_SEQ_GEN",
            sequenceName = "ANNOUNCEMENT_SEQ",
            allocationSize = 1
    )
    @Column(name = "ID")
    private Long id;

    /** 公告內容 */
    @Column(name = "CONTENT", length = 1000, nullable = false)
    private String content;

    /** 建立者 */
    @Column(name = "CREATED_BY", length = 100)
    private String createdBy;

    /** 建立時間 */
    @Column(name = "CREATED_AT")
    private Timestamp createdAt;

    /** 更新者 */
    @Column(name = "UPDATED_BY", length = 100)
    private String updatedBy;

    /** 更新時間 */
    @Column(name = "UPDATED_AT")
    private Timestamp updatedAt;

}