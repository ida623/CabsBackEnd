package com.cathaybk.demo.service;

import com.cathaybk.demo.dto.CIFT003Tranrq;
import com.cathaybk.demo.dto.EmptyTranrs;
import com.cathaybk.demo.dto.RequestTemplate;
import com.cathaybk.demo.dto.ResponseTemplate;
import com.cathaybk.demo.exception.DataNotFoundException;

public interface CIFT003Svc {
    ResponseTemplate<EmptyTranrs> deleteInfo(RequestTemplate<CIFT003Tranrq> request) throws DataNotFoundException;
}