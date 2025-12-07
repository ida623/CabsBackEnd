package com.cathaybk.demo.service;

import com.cathaybk.demo.dto.CIFT001Tranrq;
import com.cathaybk.demo.dto.EmptyTranrs;
import com.cathaybk.demo.dto.RequestTemplate;
import com.cathaybk.demo.dto.ResponseTemplate;
import com.cathaybk.demo.exception.InsertFailException;

import java.io.IOException;

public interface CifT001Svc {
    ResponseTemplate<EmptyTranrs> createInfo(RequestTemplate<CIFT001Tranrq> request) throws IOException, InsertFailException;
}