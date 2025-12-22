package com.cathaybk.demo.service.impl;

import com.cathaybk.demo.common.ReturnCodeAndDescEnum;
import com.cathaybk.demo.dto.*;
import com.cathaybk.demo.entity.CustomerInfoEntity;
import com.cathaybk.demo.exception.DataNotFoundException;
import com.cathaybk.demo.exception.UpdateFailException;
import com.cathaybk.demo.repository.CustomerInfoRepository;
import com.cathaybk.demo.service.CIFT002SvcOrika;
import jakarta.transaction.Transactional;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.impl.generator.EclipseJdtCompilerStrategy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CIFT002SvcImplOrika implements CIFT002SvcOrika {

    private final CustomerInfoRepository customerInfoRepository;
    private final MapperFacade orikaMapper;

    public CIFT002SvcImplOrika(CustomerInfoRepository customerInfoRepository) {
        this.customerInfoRepository = customerInfoRepository;

        // 初始化 Orika Mapper
        MapperFactory factory = new DefaultMapperFactory.Builder()
                .compilerStrategy(new EclipseJdtCompilerStrategy())  // 加上這一行
                .useBuiltinConverters(false)
                .mapNulls(false)  // 不映射 null 值
                .build();

        // 註冊映射關係
        factory.classMap(CIFT002TranrqData.class, CustomerInfoEntity.class)
                .byDefault()
                .register();

        this.orikaMapper = factory.getMapperFacade();
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public ResponseTemplate<EmptyTranrs> editInfoOrika(RequestTemplate<CIFT002Tranrq> request) throws DataNotFoundException {

        CIFT002Tranrq tranrq = request.getTranrq();
        CIFT002TranrqData data = tranrq.getData();

        // 使用 ORDER_ID 查詢客戶資料是否存在
        CustomerInfoEntity customerInfoEntity = customerInfoRepository.findById(data.getOrderId())
                .orElseThrow(() -> new DataNotFoundException("查無資料"));

        // 若要更新身份證號，檢查身份證號是否重複
        if (!customerInfoEntity.getIdNum().equals(data.getIdNum())) {
            List<CustomerInfoEntity> existingCustomers = customerInfoRepository.findByIdNum(data.getIdNum());
            if (!existingCustomers.isEmpty()) {
                throw new UpdateFailException("更新失敗：身份證號已被其他客戶使用");
            }
        }

        // 使用 Orika 進行映射更新
        orikaMapper.map(data, customerInfoEntity);

        // 執行更新
        try {
            customerInfoRepository.save(customerInfoEntity);
        } catch (Exception e) {
            throw new UpdateFailException("更新失敗");
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