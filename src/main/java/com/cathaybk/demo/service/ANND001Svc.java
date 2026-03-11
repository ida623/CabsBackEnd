package com.cathaybk.demo.service;

import com.cathaybk.demo.dto.ANND001Tranrq;
import com.cathaybk.demo.dto.EmptyTranrs;
import com.cathaybk.demo.exception.DataNotFoundException;
import com.cathaybk.demo.exception.RestException;

/**
 * CABS-B-ANND001 刪除公告
 * @author
 */
public interface ANND001Svc {
    EmptyTranrs deleteAnnouncement(ANND001Tranrq tranrq) throws RestException, DataNotFoundException;
}