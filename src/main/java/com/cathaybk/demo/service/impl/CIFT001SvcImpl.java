package com.cathaybk.demo.service.impl;

import com.cathaybk.demo.common.ReturnCodeAndDescEnum;
import com.cathaybk.demo.dto.*;
import com.cathaybk.demo.entity.CustomerInfoEntity;
import com.cathaybk.demo.exception.InsertFailException;
import com.cathaybk.demo.repository.CustomerInfoRepository;
import com.cathaybk.demo.service.CIFT001Svc;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * XXA-C-CIFT001 新增客戶資料
 */
@Service
@RequiredArgsConstructor
public class CIFT001SvcImpl implements CIFT001Svc {

    private final CustomerInfoRepository customerInfoRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public ResponseTemplate<EmptyTranrs> createInfo(RequestTemplate<CIFT001Tranrq> request) throws InsertFailException {

        // 取得請求
        CIFT001Tranrq tranrq = request.getTranrq();
        CIFT001TranrqData data = tranrq.getData();

        // 檢查身份證號是否已存在
        List<CustomerInfoEntity> existingCustomers = customerInfoRepository.findByIdNum(data.getIdNum());
        if (!existingCustomers.isEmpty()) {
            throw new InsertFailException("身份證號已存在");
        }

        // TODO 透過 objectMapper set 值
        // Entity 轉換為 DTO
        CustomerInfoEntity customerInfoEntity = objectMapper.convertValue(data, CustomerInfoEntity.class);

        // 執行新增
        try {
            customerInfoRepository.save(customerInfoEntity);
        } catch (Exception e) {
            throw new InsertFailException("新增失敗");
        }

        // TODO 善用 @AllArgsConstructor , @NoArgsConstructor
        // 建立回應，設定 HEADER
        ResponseTemplate<EmptyTranrs> response = new ResponseTemplate<>();
        HEADER header = new HEADER(
                request.getMwheader().getMsgid(),
                ReturnCodeAndDescEnum.SUCCESS.getCode(),
                ReturnCodeAndDescEnum.SUCCESS.getDesc()
        );
        response.setMwheader(header);
        response.setTranrs(new EmptyTranrs());  // 新增成功 Tranrs 使用空物件

        return response;
    }
}