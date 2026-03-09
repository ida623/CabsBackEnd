package com.cathaybk.demo.service;

import com.cathaybk.demo.dto.DATC001Tranrq;
import com.cathaybk.demo.dto.EmptyTranrs;
import com.cathaybk.demo.dto.RequestTemplate;
import com.cathaybk.demo.dto.ResponseTemplate;
import com.cathaybk.demo.exception.RestException;

import java.io.IOException;

/**
 * CABS-B-DATC001 新增日期設定
 *
 * @author system
 */
public interface DATC001Svc {
    ResponseTemplate<EmptyTranrs> createDateSetting(RequestTemplate<DATC001Tranrq> req)
            throws IOException, RestException;
}