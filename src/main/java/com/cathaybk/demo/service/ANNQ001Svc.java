package com.cathaybk.demo.service;

import com.cathaybk.demo.dto.ANNQ001Tranrq;
import com.cathaybk.demo.dto.ANNQ001Tranrs;
import com.cathaybk.demo.dto.RequestTemplate;
import com.cathaybk.demo.dto.ResponseTemplate;

import java.io.IOException;


/**
 * CABS-B-ANNQ001 查詢公告清單
 *
 * @author
 */
public interface ANNQ001Svc {

    ResponseTemplate<ANNQ001Tranrs> queryList(RequestTemplate<ANNQ001Tranrq> req)
            throws IOException;

}