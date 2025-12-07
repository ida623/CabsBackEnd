package com.cathaybk.demo.service.impl;

import com.cathaybk.demo.common.ReturnCodeAndDescEnum;
import com.cathaybk.demo.dto.*;
import com.cathaybk.demo.entity.CustomerInfo;
import com.cathaybk.demo.exception.DataNotFoundException;
import com.cathaybk.demo.repository.CustomerInfoRepository;
import com.cathaybk.demo.service.CifQ001Svc;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * XXA-C-CIFQ001 根據orderId查詢客戶
 */
@Service
@RequiredArgsConstructor
public class CifQ001SvcImpl implements CifQ001Svc {

    private final CustomerInfoRepository customerInfoRepository;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public ResponseTemplate<CIFQ001Tranrs> getOne(RequestTemplate<CIFQ001Tranrq> request) throws DataNotFoundException, IOException {

        CIFQ001Tranrq tranrq = request.getTranrq();

        // 使用 JPA Repository 查詢資料
        CustomerInfo customerInfo = customerInfoRepository.findById(tranrq.getOrderId())
                .orElse(null);

        if (customerInfo == null) {
            throw new DataNotFoundException("查無資料");
        }

        // 轉換為 DTO
        CIFQ001TranrsData data = new CIFQ001TranrsData();
        data.setOrderId(customerInfo.getOrderId());
        data.setIdNum(customerInfo.getIdNum());
        data.setChineseName(customerInfo.getChineseName());
        data.setGender(customerInfo.getGender());
        data.setEducation(customerInfo.getEducation());
        data.setZipCode1(customerInfo.getZipCode1());
        data.setAddress1(customerInfo.getAddress1());
        data.setTelephone1(customerInfo.getTelephone1());
        data.setZipCode2(customerInfo.getZipCode2());
        data.setAddress2(customerInfo.getAddress2());
        data.setTelephone2(customerInfo.getTelephone2());
        data.setMobile(customerInfo.getMobile());
        data.setEmail(customerInfo.getEmail());
        data.setYear(customerInfo.getYear());

        // 建立 TRANRS
        CIFQ001Tranrs tranrs = new CIFQ001Tranrs();
        List<CIFQ001TranrsData> dataList = new ArrayList<>();
        dataList.add(data);
        tranrs.setData(dataList);

        // 建立 ResponseTemplate
        ResponseTemplate<CIFQ001Tranrs> response = new ResponseTemplate<>();
        HEADER header = new HEADER();
        header.setMsgid(request.getMwheader().getMsgid());
        header.setReturncode(ReturnCodeAndDescEnum.SUCCESS.getCode());
        header.setReturndesc(ReturnCodeAndDescEnum.SUCCESS.getDesc());
        response.setMwheader(header);
        response.setTranrs(tranrs);

        return response;
    }
}