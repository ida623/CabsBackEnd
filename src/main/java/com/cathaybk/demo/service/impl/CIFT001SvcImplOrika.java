package com.cathaybk.demo.service.impl;

import com.cathaybk.demo.common.ReturnCodeAndDescEnum;
import com.cathaybk.demo.dto.*;
import com.cathaybk.demo.entity.CustomerInfoEntity;
import com.cathaybk.demo.exception.InsertFailException;
import com.cathaybk.demo.repository.CustomerInfoRepository;
import com.cathaybk.demo.service.CIFT001SvcOrika;
import jakarta.transaction.Transactional;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.impl.generator.EclipseJdtCompilerStrategy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CIFT001SvcImplOrika implements CIFT001SvcOrika {

    private final CustomerInfoRepository customerInfoRepository;
    private final MapperFacade orikaMapper;

    public CIFT001SvcImplOrika(CustomerInfoRepository customerInfoRepository) {
        this.customerInfoRepository = customerInfoRepository;

        // 初始化 Orika Mapper
        MapperFactory factory = new DefaultMapperFactory.Builder()
                .compilerStrategy(new EclipseJdtCompilerStrategy())  // 加上這一行
                .useBuiltinConverters(false)
                .build();

        // 註冊映射關係
        factory.classMap(CIFT001TranrqData.class, CustomerInfoEntity.class)
                .byDefault()
                .register();

        this.orikaMapper = factory.getMapperFacade();
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public ResponseTemplate<EmptyTranrs> createInfoOrika(RequestTemplate<CIFT001Tranrq> request) throws InsertFailException {

        CIFT001Tranrq tranrq = request.getTranrq();
        CIFT001TranrqData data = tranrq.getData();

        // 檢查身份證號是否已存在
        List<CustomerInfoEntity> existingCustomers = customerInfoRepository.findByIdNum(data.getIdNum());
        if (!existingCustomers.isEmpty()) {
            throw new InsertFailException("身份證號已存在");
        }

        // 使用 Orika 進行映射
        CustomerInfoEntity customerInfoEntity = orikaMapper.map(data, CustomerInfoEntity.class);

        // 執行新增
        try {
            customerInfoRepository.save(customerInfoEntity);
        } catch (Exception e) {
            throw new InsertFailException("新增失敗");
        }

        ResponseTemplate<EmptyTranrs> response = new ResponseTemplate<>();
        HEADER header = new HEADER(
                request.getMwheader().getMsgid(),
                ReturnCodeAndDescEnum.SUCCESS.getCode(),
                ReturnCodeAndDescEnum.SUCCESS.getDesc()
        );
        response.setMwheader(header);
        response.setTranrs(new EmptyTranrs());

        return response;
    }
}