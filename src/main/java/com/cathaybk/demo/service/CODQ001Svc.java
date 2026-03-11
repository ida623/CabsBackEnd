package com.cathaybk.demo.service;

import com.cathaybk.demo.dto.CODQ001Tranrq;
import com.cathaybk.demo.dto.CODQ001Tranrs;

/**
 * CABS-B-CODQ001 代碼查詢
 * @author 00550381
 */
public interface CODQ001Svc {

    CODQ001Tranrs queryCodeList(CODQ001Tranrq tranrq);

}