package com.cathaybk.demo.service;

import com.cathaybk.demo.dto.CIFQ004Tranrq;
import com.cathaybk.demo.dto.CIFQ004Tranrs;
import com.cathaybk.demo.dto.RequestTemplate;
import com.cathaybk.demo.dto.ResponseTemplate;

import java.io.IOException;

public interface CifQ004Svc {
    ResponseTemplate<CIFQ004Tranrs> commCode(RequestTemplate<CIFQ004Tranrq> request) throws IOException;
}