package com.cathaybk.demo.service;

import com.cathaybk.demo.dto.ANNC001Tranrq;
import com.cathaybk.demo.dto.EmptyTranrs;
import com.cathaybk.demo.dto.RequestTemplate;
import com.cathaybk.demo.dto.ResponseTemplate;
import com.cathaybk.demo.exception.RestException;

import java.io.IOException;


/**
 * CABS-B-ANNC001 新增公告
 *
 * @author
 */
public interface ANNC001Svc {

    ResponseTemplate<EmptyTranrs> createAnnouncement(RequestTemplate<ANNC001Tranrq> req)
            throws IOException, RestException;

}