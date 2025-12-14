package com.cathaybk.demo.service;

import com.cathaybk.demo.dto.CIFQ003Tranrq;
import com.cathaybk.demo.dto.EmptyTranrs;
import com.cathaybk.demo.dto.RequestTemplate;
import com.cathaybk.demo.dto.ResponseTemplate;
import com.cathaybk.demo.exception.InsertFailException;

import java.io.IOException;

public interface CifQ003Svc {
    ResponseTemplate<EmptyTranrs> checkId(RequestTemplate<CIFQ003Tranrq> request) throws InsertFailException, IOException;
}