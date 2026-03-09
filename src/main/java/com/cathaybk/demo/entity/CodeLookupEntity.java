package com.cathaybk.demo.entity;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * CODE_LOOKUP 通用代碼對照表
 *
 * @author
 */
@Data
@Entity
@Table(name = "CODE_LOOKUP")
@IdClass(CodeLookupEntity.CodeLookupId.class)
public class CodeLookupEntity implements Serializable {

    /** serialVersionUID */
    @Serial
    private static final long serialVersionUID = 1L;

    /** 代碼類別 */
    @Id
    @Column(name = "CODE_TYPE", length = 50)
    private String codeType;

    /** 代碼值 */
    @Id
    @Column(name = "CODE_VALUE", length = 20)
    private String codeValue;

    /** 代碼名稱 */
    @Column(name = "CODE_NAME", length = 100)
    private String codeName;

    /**
     * 複合主鍵類別
     */
    @Data
    public static class CodeLookupId implements Serializable {

        /** serialVersionUID */
        @Serial
        private static final long serialVersionUID = 1L;

        /** 代碼類別 */
        private String codeType;

        /** 代碼值 */
        private String codeValue;
    }

}