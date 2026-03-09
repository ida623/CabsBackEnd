package com.cathaybk.demo.service.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.cathaybk.demo.dto.DATC001Tranrq;
import com.cathaybk.demo.dto.EmptyTranrs;
import com.cathaybk.demo.dto.RequestTemplate;
import com.cathaybk.demo.dto.ResponseTemplate;
import com.cathaybk.demo.entity.DateSettingWindowEntity;
import com.cathaybk.demo.exception.RestException;
import com.cathaybk.demo.factory.NormalResponseFactory;
import com.cathaybk.demo.repository.DateSettingWindowRepository;
import com.cathaybk.demo.service.DATC001Svc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

/**
 * CABS-B-DATC001 新增日期設定
 *
 * @author system
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DATC001SvcImpl implements DATC001Svc {

    /** DateSettingWindowRepository */
    private final DateSettingWindowRepository dateSettingWindowRepository;

    /** NormalResponseFactory */
    private final NormalResponseFactory normalResponseFactory;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");

    @Override
    public ResponseTemplate<EmptyTranrs> createDateSetting(RequestTemplate<DATC001Tranrq> req)
            throws IOException, RestException {
        DATC001Tranrq tranrq = req.getTranrq();

        // STEP1. 取得操作者資訊
        String createdBy = getCreatedBy(req);

        // STEP2. 日期轉換
        LocalDate windowStartDate = LocalDate.parse(tranrq.getWindowStartDate(), DATE_FORMATTER);
        LocalDate windowEndDate = LocalDate.parse(tranrq.getWindowEndDate(), DATE_FORMATTER);
        LocalDateTime cutoffDate = LocalDateTime.parse(tranrq.getCutoffDate(), DATETIME_FORMATTER);

        // STEP3. 建立 Entity
        DateSettingWindowEntity entity = new DateSettingWindowEntity();
        entity.setWindowStartDate(windowStartDate);
        entity.setWindowEndDate(windowEndDate);
        entity.setCutoffDate(cutoffDate);
        entity.setCreatedBy(createdBy);
        entity.setCreatedAt(LocalDateTime.now());

        // STEP4. 呼叫 Repository 儲存
        dateSettingWindowRepository.save(entity);

        return normalResponseFactory.genNormalResponse(new EmptyTranrs(), req);
    }

    /**
     * 取得建立者資訊
     * @param req
     * @return
     */
    private String getCreatedBy(RequestTemplate<?> req) {
        // 從登入資訊取得建立者姓名或員編
        // 實際實作需依系統架構調整
        return "SYSTEM"; // 預設值，實際應從 request context 或 security context 取得
    }
}