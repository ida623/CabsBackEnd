package com.cathaybk.demo.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * MsgCode 的複合主鍵 ID
 */
// TODO 使用 @Data = @ToString、@EqualsAndHashCode、@Getter、@Setter 和 @RequiredArgsConstructor
@Data
public class MsgCodeIdEntity implements Serializable {

    private String msgCode;
    private String msgOptionSerno;

}