package com.cathaybk.demo.service.impl;

import com.cathaybk.demo.common.ReturnCodeAndDescEnum;
import com.cathaybk.demo.dto.*;
import com.cathaybk.demo.entity.CustomerInfoEntity;
import com.cathaybk.demo.exception.InsertFailException;
import com.cathaybk.demo.mapper.CustomerInfoMapper;
import com.cathaybk.demo.repository.CustomerInfoRepository;
import com.cathaybk.demo.service.CIFT001SvcMapStruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CIFT001SvcImplMapStruct implements CIFT001SvcMapStruct {

    private final CustomerInfoRepository customerInfoRepository;
    private final CustomerInfoMapper customerInfoMapper;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public ResponseTemplate<EmptyTranrs> createInfoMapStruct(RequestTemplate<CIFT001Tranrq> request) throws InsertFailException {

        CIFT001Tranrq tranrq = request.getTranrq();
        CIFT001TranrqData data = tranrq.getData();

        // 檢查身份證號是否已存在
        List<CustomerInfoEntity> existingCustomers = customerInfoRepository.findByIdNum(data.getIdNum());
        if (!existingCustomers.isEmpty()) {
            throw new InsertFailException("身份證號已存在");
        }

        // 使用 MapStruct 進行映射
        CustomerInfoEntity customerInfoEntity = customerInfoMapper.toEntity(data);

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