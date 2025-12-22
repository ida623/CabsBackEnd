package com.cathaybk.demo.service.impl;

import com.cathaybk.demo.common.ReturnCodeAndDescEnum;
import com.cathaybk.demo.dto.*;
import com.cathaybk.demo.entity.CustomerInfoEntity;
import com.cathaybk.demo.exception.DataNotFoundException;
import com.cathaybk.demo.repository.CustomerInfoRepository;
import com.cathaybk.demo.service.CIFQ001SvcModelMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CIFQ001SvcImplModelMapper implements CIFQ001SvcModelMapper {

    private final CustomerInfoRepository customerInfoRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    public ResponseTemplate<CIFQ001Tranrs> getOneModelMapper(RequestTemplate<CIFQ001Tranrq> request) throws DataNotFoundException {

        CIFQ001Tranrq tranrq = request.getTranrq();

        CustomerInfoEntity customerInfoEntity = customerInfoRepository.findById(tranrq.getOrderId())
                .orElseThrow(() -> new DataNotFoundException("查無資料"));

        // 使用 ModelMapper 進行映射
        CIFQ001TranrsData data = modelMapper.map(customerInfoEntity, CIFQ001TranrsData.class);

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