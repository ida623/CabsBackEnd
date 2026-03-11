package com.cathaybk.demo.service;

import com.cathaybk.demo.dto.ANNC001Tranrq;
import com.cathaybk.demo.dto.EmptyTranrs;
import com.cathaybk.demo.exception.RestException;


/**
 * CABS-B-ANNC001 新增公告
 * @author 00550381
 */
public interface ANNC001Svc {
    EmptyTranrs createAnnouncement(ANNC001Tranrq tranrq) throws RestException;
}