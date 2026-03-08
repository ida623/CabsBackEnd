// file: src/main/java/cfc/bff/platform/service/CCRE001Svc.java
package com.cathaybk.demo.service;


import com.cathaybk.demo.dto.CCRE001Tranrq;
import com.cathaybk.demo.dto.RequestTemplate;
import com.cathaybk.demo.exception.RestException;

/**
 * CABS-B-CCRE001 匯出聯繫單清單
 *
 * @author 00550396
 */
public interface CCRE001Svc {

    byte[] exportExcel(RequestTemplate<CCRE001Tranrq> req) throws RestException;

}