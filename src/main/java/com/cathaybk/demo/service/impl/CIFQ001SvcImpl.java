package com.cathaybk.demo.service.impl;

import com.cathaybk.demo.common.ReturnCodeAndDescEnum;
import com.cathaybk.demo.dto.*;
import com.cathaybk.demo.entity.CustomerInfoEntity;
import com.cathaybk.demo.exception.DataNotFoundException;
import com.cathaybk.demo.repository.CustomerInfoRepository;
import com.cathaybk.demo.service.CIFQ001Svc;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * XXA-C-CIFQ001 用orderId查詢
 */
@Service
@RequiredArgsConstructor
public class CIFQ001SvcImpl implements CIFQ001Svc {

    private final CustomerInfoRepository customerInfoRepository;
    private final ObjectMapper objectMapper;

    @Override
    // TODO 目前 Query 均不需要 @Transactional (check SvcImpl)
    public ResponseTemplate<CIFQ001Tranrs> getOne(RequestTemplate<CIFQ001Tranrq> request) throws DataNotFoundException {

        // 取得請求
        CIFQ001Tranrq tranrq = request.getTranrq();

        // 用 orderId 查詢客戶資料
        CustomerInfoEntity customerInfoEntity = customerInfoRepository.findById(tranrq.getOrderId())
                .orElseThrow(() -> new DataNotFoundException("查無資料"));

        // 建立 TRANRS
        CIFQ001Tranrs tranrs = new CIFQ001Tranrs();
        List<CIFQ001TranrsData> dataList = new ArrayList<>();
        // TODO 透過 objectMapper set 值
        dataList.add(objectMapper.convertValue(customerInfoEntity, CIFQ001TranrsData.class));
        tranrs.setData(dataList);

        // TODO 善用 @AllArgsConstructor , @NoArgsConstructor
        // 建立回應，設定 HEADER
        ResponseTemplate<CIFQ001Tranrs> response = new ResponseTemplate<>();
        HEADER header = new HEADER(
                request.getMwheader().getMsgid(),
                ReturnCodeAndDescEnum.SUCCESS.getCode(),
                ReturnCodeAndDescEnum.SUCCESS.getDesc()
        );
        response.setMwheader(header);
        response.setTranrs(tranrs);

        return response;
    }
}