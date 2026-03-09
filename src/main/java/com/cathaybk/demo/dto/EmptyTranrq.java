package com.cathaybk.demo.dto;

import java.io.Serial;
import java.io.Serializable;

/**
 * 空的請求電文
 *
 * @author system
 */
public class EmptyTranrq implements Serializable {

    /** serialVersionUID */
    @Serial
    private static final long serialVersionUID = 1L;

    public static final EmptyTranrq INSTANCE = new EmptyTranrq();
}