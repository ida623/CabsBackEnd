package com.cathaybk.demo.service;

import com.cathaybk.demo.dto.ANNU001Tranrq;
import com.cathaybk.demo.dto.EmptyTranrs;
import com.cathaybk.demo.exception.DataNotFoundException;
import com.cathaybk.demo.exception.RestException;

/**
 * CABS-B-ANNU001 修改公告
 * @author
 */
public interface ANNU001Svc {
    EmptyTranrs updateAnnouncement(ANNU001Tranrq tranrq) throws RestException, DataNotFoundException;
}