package com.cathaybk.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MsgCodeId implements Serializable {
    private String msgCode;
    private String msgOptionSerno;
}