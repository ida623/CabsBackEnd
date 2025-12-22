package com.cathaybk.demo.service.impl;

import com.cathaybk.demo.common.ReturnCodeAndDescEnum;
import com.cathaybk.demo.dto.*;
import com.cathaybk.demo.entity.CustomerInfoEntity;
import com.cathaybk.demo.exception.DataNotFoundException;
import com.cathaybk.demo.repository.CustomerInfoRepository;
import com.cathaybk.demo.service.CIFQ001SvcOrika;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.impl.generator.EclipseJdtCompilerStrategy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CIFQ001SvcImplOrika implements CIFQ001SvcOrika {

    private final CustomerInfoRepository customerInfoRepository;
    private final MapperFacade orikaMapper;

    public CIFQ001SvcImplOrika(CustomerInfoRepository customerInfoRepository) {
        this.customerInfoRepository = customerInfoRepository;

        // 初始化 Orika Mapper
        MapperFactory factory = new DefaultMapperFactory.Builder()
                .compilerStrategy(new EclipseJdtCompilerStrategy()) //要加上 EclipseJdtCompilerStrategy()，不然會使用預設的 Javassist
                .useBuiltinConverters(false)
                .build();

        // 註冊映射關係
        factory.classMap(CustomerInfoEntity.class, CIFQ001TranrsData.class)
                .byDefault()
                .register();

        this.orikaMapper = factory.getMapperFacade();
    }

    @Override
    public ResponseTemplate<CIFQ001Tranrs> getOneOrika(RequestTemplate<CIFQ001Tranrq> request) throws DataNotFoundException {

        CIFQ001Tranrq tranrq = request.getTranrq();

        CustomerInfoEntity customerInfoEntity = customerInfoRepository.findById(tranrq.getOrderId())
                .orElseThrow(() -> new DataNotFoundException("查無資料"));

        // 使用 Orika 進行映射
        CIFQ001TranrsData data = orikaMapper.map(customerInfoEntity, CIFQ001TranrsData.class);

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