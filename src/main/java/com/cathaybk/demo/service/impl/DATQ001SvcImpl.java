package com.cathaybk.demo.service.impl;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

import com.cathaybk.demo.dto.DATQ001Tranrs;
import com.cathaybk.demo.dto.EmptyTranrq;
import com.cathaybk.demo.dto.RequestTemplate;
import com.cathaybk.demo.dto.ResponseTemplate;
import com.cathaybk.demo.entity.DateSettingWindowEntity;
import com.cathaybk.demo.exception.DataNotFoundException;
import com.cathaybk.demo.exception.RestException;
import com.cathaybk.demo.factory.NormalResponseFactory;
import com.cathaybk.demo.repository.DateSettingWindowRepository;
import com.cathaybk.demo.service.DATQ001Svc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

/**
 * CABS-B-DATQ001 查詢日期設定
 *
 * @author system
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DATQ001SvcImpl implements DATQ001Svc {

    /** DateSettingWindowRepository */
    private final DateSettingWindowRepository dateSettingWindowRepository;

    /** NormalResponseFactory */
    private final NormalResponseFactory normalResponseFactory;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");

    @Override
    public ResponseTemplate<DATQ001Tranrs> queryLatestDateSetting(RequestTemplate<EmptyTranrq> req)
            throws IOException, DataNotFoundException, RestException {

        // STEP1. 查詢最新一筆
        DateSettingWindowEntity entity = dateSettingWindowRepository
                .findTopByOrderByCreatedAtDescIdDesc()
                .orElseThrow(() -> new DataNotFoundException("查無日期設定資料"));

        // STEP2. 組下行
        DATQ001Tranrs tranrs = new DATQ001Tranrs();
        tranrs.setWindowStartDate(entity.getWindowStartDate().format(DATE_FORMATTER));
        tranrs.setWindowEndDate(entity.getWindowEndDate().format(DATE_FORMATTER));
        tranrs.setCutoffDate(entity.getCutoffDate().format(DATETIME_FORMATTER));

        return normalResponseFactory.genNormalResponse(tranrs, req);
    }
}