package com.cathaybk.demo.service;

import com.cathaybk.demo.dto.CIFQ001Tranrq;
import com.cathaybk.demo.dto.CIFQ001Tranrs;
import com.cathaybk.demo.dto.RequestTemplate;
import com.cathaybk.demo.dto.ResponseTemplate;
import com.cathaybk.demo.exception.DataNotFoundException;

public interface CIFQ001SvcApacheBeanUtils {
    ResponseTemplate<CIFQ001Tranrs> getOneApacheBeanUtils(RequestTemplate<CIFQ001Tranrq> request) throws DataNotFoundException;
}