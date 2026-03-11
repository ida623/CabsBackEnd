package com.cathaybk.demo.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CFS-C-CABSLISTO001 上行
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CabsListO001Tranrq {

    /** 審核狀態（Y/N） */
    private String reviewStatus;

    /** 審核日期（yyyyMMdd） */
    private String reviewDate;

    /** 聯繫單號清單 */
    private List<String> eformList;

}