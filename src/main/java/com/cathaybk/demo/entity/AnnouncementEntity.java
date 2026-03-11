package com.cathaybk.demo.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "ANNOUNCEMENT")
public class AnnouncementEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主鍵ID */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ANNOUNCEMENT_SEQ_GEN")
    @SequenceGenerator(
            name = "ANNOUNCEMENT_SEQ_GEN",
            sequenceName = "ANNOUNCEMENT_SEQ",
            allocationSize = 1
    )
    @Column(name = "ID", nullable = false, precision = 19)
    private Long id;

    /** 公告內容 */
    @Column(name = "CONTENT", nullable = false, length = 1000)
    private String content;

    /** 建立者姓名 */
    @Column(name = "CREATED_BY", nullable = false, length = 50)
    private String createdBy;

    /** 建立時間 */
    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** 最後異動者姓名 */
    @Column(name = "UPDATED_BY", nullable = false, length = 50)
    private String updatedBy;

    /** 最後異動時間 */
    @Column(name = "UPDATED_AT", nullable = false)
    private LocalDateTime updatedAt;

}