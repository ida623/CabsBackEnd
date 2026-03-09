package com.cathaybk.demo.service;

import java.io.IOException;

import com.cathaybk.demo.dto.RLSQ002Tranrs;
import com.cathaybk.demo.dto.ResponseTemplate;
import com.cathaybk.demo.exception.RestException;
import org.springframework.web.multipart.MultipartFile;

/**
 * CABS-B-RLSQ002 匯入EXCEL產生放行清單
 *
 * @author System
 */
public interface RLSQ002Svc {

    ResponseTemplate<RLSQ002Tranrs> importExcel(MultipartFile file)
            throws IOException, RestException;
}
