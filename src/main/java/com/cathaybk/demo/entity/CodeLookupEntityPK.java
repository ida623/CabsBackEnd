package com.cathaybk.demo.entity;

import java.io.Serializable;

import lombok.Data;

@Data
public class CodeLookupEntityPK implements Serializable {

    private static final long serialVersionUID = 1L;

    private String codeType;
    private String codeValue;

}