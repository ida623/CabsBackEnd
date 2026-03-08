// file: src/main/java/cfc/bff/platform/service/CCRQ001Svc.java
package com.cathaybk.demo.service;

import com.cathaybk.demo.dto.CCRQ001Tranrq;
import com.cathaybk.demo.dto.CCRQ001Tranrs;
import com.cathaybk.demo.dto.RequestTemplate;
import com.cathaybk.demo.dto.ResponseTemplate;
import com.cathaybk.demo.exception.RestException;

import java.io.IOException;


/**
 * CABS-B-CCRQ001 查詢聯繫單列表
 *
 * @author 00550396
 */
public interface CCRQ001Svc {

    ResponseTemplate<CCRQ001Tranrs> queryList(RequestTemplate<CCRQ001Tranrq> req)
            throws IOException, RestException;

}