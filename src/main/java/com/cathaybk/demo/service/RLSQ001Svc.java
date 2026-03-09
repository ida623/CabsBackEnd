package com.cathaybk.demo.service;

import com.cathaybk.demo.dto.RLSQ001Tranrq;
import com.cathaybk.demo.dto.RLSQ001Tranrs;
import com.cathaybk.demo.dto.RequestTemplate;
import com.cathaybk.demo.dto.ResponseTemplate;
import com.cathaybk.demo.exception.RestException;

import java.io.IOException;

/**
 * CABS-B-RLSQ001 產生放行清單
 *
 * @author System
 */
public interface RLSQ001Svc {

    ResponseTemplate<RLSQ001Tranrs> preview(RequestTemplate<RLSQ001Tranrq> req)
            throws IOException, RestException;
}
