package com.cathaybk.demo.service;

import com.cathaybk.demo.dto.CCRC001Tranrq;
import com.cathaybk.demo.dto.EmptyTranrs;
import com.cathaybk.demo.dto.RequestTemplate;
import com.cathaybk.demo.dto.ResponseTemplate;
import com.cathaybk.demo.exception.RestException;

/**
 * CABS-B-CCRC001 iContact聯繫單資料匯入
 *
 * @author 00550396
 */
public interface CCRC001Svc {

    ResponseTemplate<EmptyTranrs> sync(RequestTemplate<CCRC001Tranrq> req)
            throws RestException;

}