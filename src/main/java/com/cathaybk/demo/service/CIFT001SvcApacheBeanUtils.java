package com.cathaybk.demo.service;

import com.cathaybk.demo.dto.CIFT001Tranrq;
import com.cathaybk.demo.dto.EmptyTranrs;
import com.cathaybk.demo.dto.RequestTemplate;
import com.cathaybk.demo.dto.ResponseTemplate;
import com.cathaybk.demo.exception.InsertFailException;

public interface CIFT001SvcApacheBeanUtils {
    ResponseTemplate<EmptyTranrs> createInfoApacheBeanUtils(RequestTemplate<CIFT001Tranrq> request) throws InsertFailException;
}