package com.cathaybk.demo.service.impl;

import com.cathaybk.demo.common.ReturnCodeAndDescEnum;
import com.cathaybk.demo.dto.*;
import com.cathaybk.demo.entity.CustomerInfoEntity;
import com.cathaybk.demo.exception.DataNotFoundException;
import com.cathaybk.demo.exception.UpdateFailException;
import com.cathaybk.demo.repository.CustomerInfoRepository;
import com.cathaybk.demo.service.CIFT002Svc;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * XXA-C-CIFT002 修改客戶資料
 */
@Service
@RequiredArgsConstructor
public class CIFT002SvcImpl implements CIFT002Svc {

    private final CustomerInfoRepository customerInfoRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public ResponseTemplate<EmptyTranrs> editInfo(RequestTemplate<CIFT002Tranrq> request) throws DataNotFoundException {

        // 取得請求
        CIFT002Tranrq tranrq = request.getTranrq();
        CIFT002TranrqData data = tranrq.getData();

        // TODO 拋出例外 合併寫法
        // 使用 ORDER_ID 查詢客戶資料是否存在
        CustomerInfoEntity customerInfoEntity = customerInfoRepository.findById(data.getOrderId())
                .orElseThrow(() -> new DataNotFoundException("查無資料"));

        // 若要更新身份證號，檢查身份證號是否重複
        // 規格書：前端畫面不開放修改身份證號
        if (!customerInfoEntity.getIdNum().equals(data.getIdNum())) {
            List<CustomerInfoEntity> existingCustomers = customerInfoRepository.findByIdNum(data.getIdNum());
            if (!existingCustomers.isEmpty()) {
                throw new UpdateFailException("更新失敗：身份證號已被其他客戶使用");
            }
        }

        // TODO 透過 objectMapper set 值
        // 更新資料（保留 ORDER_ID）
        Long orderId = customerInfoEntity.getOrderId();
        customerInfoEntity = objectMapper.convertValue(data, CustomerInfoEntity.class);
        customerInfoEntity.setOrderId(orderId);

        // 執行更新
        try {
            customerInfoRepository.save(customerInfoEntity);
        } catch (Exception e) {
            throw new UpdateFailException("更新失敗");
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
        response.setTranrs(new EmptyTranrs());  // 更新成功 Tranrs 使用空物件

        return response;
    }
}