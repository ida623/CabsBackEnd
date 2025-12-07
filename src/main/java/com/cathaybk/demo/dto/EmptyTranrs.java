package com.cathaybk.demo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;


/**
 * 空的 Tranrs
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
public class EmptyTranrs implements Serializable {
}
