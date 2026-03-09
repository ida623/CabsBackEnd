package com.cathaybk.demo.service;


import com.cathaybk.demo.dto.ANND001Tranrq;
import com.cathaybk.demo.dto.EmptyTranrs;
import com.cathaybk.demo.dto.RequestTemplate;
import com.cathaybk.demo.dto.ResponseTemplate;
import com.cathaybk.demo.exception.DataNotFoundException;
import com.cathaybk.demo.exception.RestException;

/**
 * CABS-B-ANND001 刪除公告
 *
 * @author 張育誠
 */
public interface ANND001Svc {

    ResponseTemplate<EmptyTranrs> delete(RequestTemplate<ANND001Tranrq> req)
            throws DataNotFoundException, RestException;

}