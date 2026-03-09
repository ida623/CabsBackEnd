package com.cathaybk.demo.service;


import com.cathaybk.demo.dto.ANNQ002Tranrq;
import com.cathaybk.demo.dto.ANNQ002Tranrs;
import com.cathaybk.demo.dto.RequestTemplate;
import com.cathaybk.demo.dto.ResponseTemplate;
import com.cathaybk.demo.exception.DataNotFoundException;

/**
 * CABS-B-ANNQ002 查詢公告
 *
 * @author 張育誠
 */
public interface ANNQ002Svc {

    ResponseTemplate<ANNQ002Tranrs> query(RequestTemplate<ANNQ002Tranrq> req)
            throws DataNotFoundException;

}