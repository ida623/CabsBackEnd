package com.cathaybk.demo.service;

import com.cathaybk.demo.dto.CCRQ002Tranrq;
import com.cathaybk.demo.dto.CCRQ002Tranrs;
import com.cathaybk.demo.dto.RequestTemplate;
import com.cathaybk.demo.dto.ResponseTemplate;
import com.cathaybk.demo.exception.DataNotFoundException;
import com.cathaybk.demo.exception.RestException;

/**
 * CABS-B-CCRQ002 查詢聯繫單
 *
 * @author 00550396
 */
public interface CCRQ002Svc {

    ResponseTemplate<CCRQ002Tranrs> query(RequestTemplate<CCRQ002Tranrq> req)
            throws RestException, DataNotFoundException;

}