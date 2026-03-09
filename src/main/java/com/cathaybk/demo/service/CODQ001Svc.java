package com.cathaybk.demo.service;

import com.cathaybk.demo.dto.CODQ001Tranrq;
import com.cathaybk.demo.dto.CODQ001Tranrs;
import com.cathaybk.demo.dto.RequestTemplate;
import com.cathaybk.demo.dto.ResponseTemplate;

import java.io.IOException;


/**
 * CABS-B-CODQ001 代碼查詢
 *
 * @author 張育誠
 */
public interface CODQ001Svc {

    ResponseTemplate<CODQ001Tranrs> queryList(RequestTemplate<CODQ001Tranrq> req)
            throws IOException;

}