package com.cathaybk.demo.service;

import com.cathaybk.demo.dto.CIFT001Tranrq;
import com.cathaybk.demo.dto.EmptyTranrs;
import com.cathaybk.demo.dto.RequestTemplate;
import com.cathaybk.demo.dto.ResponseTemplate;
import com.cathaybk.demo.exception.InsertFailException;

public interface CIFT001SvcOrika {
    ResponseTemplate<EmptyTranrs> createInfoOrika(RequestTemplate<CIFT001Tranrq> request) throws InsertFailException;
}