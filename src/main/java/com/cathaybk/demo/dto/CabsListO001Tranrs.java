package com.cathaybk.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CFS-C-CABSLISTO001 下行
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CabsListO001Tranrs {

    /** 回傳碼 */
    private String returnCode;

    /** 回傳訊息 */
    private String returnDesc;

}