package com.cathaybk.demo.service;

import com.cathaybk.demo.dto.CIFT002Tranrq;
import com.cathaybk.demo.dto.EmptyTranrs;
import com.cathaybk.demo.dto.RequestTemplate;
import com.cathaybk.demo.dto.ResponseTemplate;
import com.cathaybk.demo.exception.DataNotFoundException;

public interface CIFT002Svc {
    ResponseTemplate<EmptyTranrs> editInfo(RequestTemplate<CIFT002Tranrq> request) throws DataNotFoundException;
}