package com.cathaybk.demo.service;

import com.cathaybk.demo.dto.DATQ001Tranrs;
import com.cathaybk.demo.dto.EmptyTranrq;
import com.cathaybk.demo.dto.RequestTemplate;
import com.cathaybk.demo.dto.ResponseTemplate;
import com.cathaybk.demo.exception.DataNotFoundException;
import com.cathaybk.demo.exception.RestException;

import java.io.IOException;

/**
 * CABS-B-DATQ001 查詢日期設定
 *
 * @author system
 */
public interface DATQ001Svc {
    ResponseTemplate<DATQ001Tranrs> queryLatestDateSetting(RequestTemplate<EmptyTranrq> req)
            throws IOException, DataNotFoundException, RestException;
}