package com.cathaybk.demo.service;

import com.cathaybk.demo.dto.CIFQ002Tranrq;
import com.cathaybk.demo.dto.CIFQ002Tranrs;
import com.cathaybk.demo.dto.RequestTemplate;
import com.cathaybk.demo.dto.ResponseTemplate;
import com.cathaybk.demo.exception.DataNotFoundException;

import java.io.IOException;

public interface CIFQ002Svc {
    ResponseTemplate<CIFQ002Tranrs> filter(RequestTemplate<CIFQ002Tranrq> request) throws DataNotFoundException, IOException;
}