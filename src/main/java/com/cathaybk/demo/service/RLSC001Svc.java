package com.cathaybk.demo.service;

import com.cathaybk.demo.dto.RLSC001Tranrq;
import com.cathaybk.demo.dto.RLSC001Tranrs;
import com.cathaybk.demo.dto.RequestTemplate;
import com.cathaybk.demo.dto.ResponseTemplate;
import com.cathaybk.demo.exception.RestException;

import java.io.IOException;

/**
 * CABS-B-RLSC001 放行處理
 *
 * @author System
 */
public interface RLSC001Svc {

    ResponseTemplate<RLSC001Tranrs> actions(RequestTemplate<RLSC001Tranrq> req)
            throws IOException, RestException;
}
