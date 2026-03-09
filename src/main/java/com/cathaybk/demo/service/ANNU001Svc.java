package com.cathaybk.demo.service;


import com.cathaybk.demo.dto.ANNU001Tranrq;
import com.cathaybk.demo.dto.EmptyTranrs;
import com.cathaybk.demo.dto.RequestTemplate;
import com.cathaybk.demo.dto.ResponseTemplate;
import com.cathaybk.demo.exception.DataNotFoundException;
import com.cathaybk.demo.exception.RestException;

/**
 * CABS-B-ANNU001 修改公告
 *
 * @author 張育誠
 */
public interface ANNU001Svc {

    ResponseTemplate<EmptyTranrs> update(RequestTemplate<ANNU001Tranrq> req)
            throws DataNotFoundException, RestException;

}