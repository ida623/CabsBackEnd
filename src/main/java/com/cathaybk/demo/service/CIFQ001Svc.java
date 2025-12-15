package com.cathaybk.demo.service;

import com.cathaybk.demo.dto.CIFQ001Tranrq;
import com.cathaybk.demo.dto.CIFQ001Tranrs;
import com.cathaybk.demo.dto.RequestTemplate;
import com.cathaybk.demo.dto.ResponseTemplate;
import com.cathaybk.demo.exception.DataNotFoundException;

public interface CIFQ001Svc {
    // TODO 未用到的 Exception 記得移除
    // TODO 修改svc / SvcImpl 命名 (XXAC)CIFQ001Svc
    ResponseTemplate<CIFQ001Tranrs> getOne(RequestTemplate<CIFQ001Tranrq> request) throws DataNotFoundException;
}