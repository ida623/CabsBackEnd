package com.cathaybk.demo.service;

import com.cathaybk.demo.dto.CIFQ001Tranrq;
import com.cathaybk.demo.dto.CIFQ001Tranrs;
import com.cathaybk.demo.dto.RequestTemplate;
import com.cathaybk.demo.dto.ResponseTemplate;
import com.cathaybk.demo.exception.DataNotFoundException;

import java.io.IOException;

public interface CifQ001Svc {
    ResponseTemplate<CIFQ001Tranrs> getOne(RequestTemplate<CIFQ001Tranrq> request) throws DataNotFoundException, IOException;
}