package com.cathaybk.demo.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "CODE_LOOKUP")
@IdClass(CodeLookupEntityPK.class)
public class CodeLookupEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 代碼類別
     */
    @Id
    @Column(name = "CODE_TYPE", nullable = false, length = 50)
    private String codeType;

    /**
     * 代碼值
     */
    @Id
    @Column(name = "CODE_VALUE", nullable = false, length = 20)
    private String codeValue;

    /**
     * 顯示中文
     */
    @Column(name = "CODE_NAME", nullable = false, length = 100)
    private String codeName;

}