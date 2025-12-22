package com.cathaybk.demo.service.impl;

import com.cathaybk.demo.common.ReturnCodeAndDescEnum;
import com.cathaybk.demo.dto.*;
import com.cathaybk.demo.entity.CustomerInfoEntity;
import com.cathaybk.demo.exception.DataNotFoundException;
import com.cathaybk.demo.repository.CustomerInfoRepository;
import com.cathaybk.demo.service.CIFQ001SvcSpringBeanUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CIFQ001SvcImplSpringBeanUtils implements CIFQ001SvcSpringBeanUtils {

    private final CustomerInfoRepository customerInfoRepository;

    @Override
    public ResponseTemplate<CIFQ001Tranrs> getOneSpringBeanUtils(RequestTemplate<CIFQ001Tranrq> request) throws DataNotFoundException {

        CIFQ001Tranrq tranrq = request.getTranrq();

        CustomerInfoEntity customerInfoEntity = customerInfoRepository.findById(tranrq.getOrderId())
                .orElseThrow(() -> new DataNotFoundException("查無資料"));

        // 使用 Spring BeanUtils 進行映射
        CIFQ001TranrsData data = new CIFQ001TranrsData();
        BeanUtils.copyProperties(customerInfoEntity, data);

        CIFQ001Tranrs tranrs = new CIFQ001Tranrs();
        List<CIFQ001TranrsData> dataList = new ArrayList<>();
        dataList.add(data);
        tranrs.setData(dataList);

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