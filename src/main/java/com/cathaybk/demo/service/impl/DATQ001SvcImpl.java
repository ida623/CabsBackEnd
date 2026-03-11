package com.cathaybk.demo.service.impl;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import com.cathaybk.demo.dto.DATQ001Tranrs;
import com.cathaybk.demo.dto.EmptyTranrq;
import com.cathaybk.demo.dto.RequestTemplate;
import com.cathaybk.demo.dto.ResponseTemplate;
import com.cathaybk.demo.entity.DataSettingWindowEntity;
import com.cathaybk.demo.entity.DateSettingWindowEntity;
import com.cathaybk.demo.exception.DataNotFoundException;
import com.cathaybk.demo.exception.RestException;
import com.cathaybk.demo.factory.NormalResponseFactory;
import com.cathaybk.demo.repository.DataSettingWindowRepo;
import com.cathaybk.demo.repository.DateSettingWindowRepository;
import com.cathaybk.demo.service.DATQ001Svc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

/**
 * CABS-B-DATQ001 查詢日期設定
 * @author system
 */
@RequiredArgsConstructor
@Service
public class DATQ001SvcImpl implements DATQ001Svc {

    /** DataSettingWindowRepo */
    private final DataSettingWindowRepo dataSettingWindowRepo;

    /**
     * CABS-B-DATQ001 查詢日期設定
     * @author 00550354
     */
    @Override
    public DATQ001Tranrs queryDateSettings(EmptyTranrq tranrq) throws DataNotFoundException {

        DataSettingWindowEntity entity = Optional.ofNullable(dataSettingWindowRepo.findTopByOrderByCreatedAtDescIdDesc() // ⭐ 這個方法的返回值
        ).orElseThrow(() -> new DataNotFoundException("查無目前最新日期設定"));

        DATQ001Tranrs tranrs = new DATQ001Tranrs();
        tranrs.setWindowStartDate(DateFormatUtil.formatLocalDate(entity.getWindowStartDate()));
        tranrs.setWindowEndDate(DateFormatUtil.formatLocalDate(entity.getWindowEndDate()));
        tranrs.setCutoffDate(DateFormatUtil.formatLocalDateTime(entity.getCutoffDate()));

        return tranrs;
    }

}