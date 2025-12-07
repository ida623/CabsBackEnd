package com.cathaybk.demo.service.impl;

import com.cathaybk.demo.common.ReturnCodeAndDescEnum;
import com.cathaybk.demo.dto.*;
import com.cathaybk.demo.entity.CustomerInfo;
import com.cathaybk.demo.exception.DataNotFoundException;
import com.cathaybk.demo.repository.CustomerInfoRepository;
import com.cathaybk.demo.service.CifT003Svc;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * XXA-C-CIFT003 刪除客戶資料
 */
@Service
@RequiredArgsConstructor
public class CifT003SvcImpl implements CifT003Svc {

    private final CustomerInfoRepository customerInfoRepository;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public ResponseTemplate<EmptyTranrs> deleteInfo(RequestTemplate<CIFT003Tranrq> request) throws DataNotFoundException, IOException {

        CIFT003Tranrq tranrq = request.getTranrq();

        // 查詢客戶資料是否存在
        CustomerInfo customerInfo = customerInfoRepository.findById(tranrq.getOrderId())
                .orElse(null);

        if (customerInfo == null) {
            throw new DataNotFoundException("查無資料");
        }

        // 刪除資料
        customerInfoRepository.delete(customerInfo);

        // 建立 ResponseTemplate
        ResponseTemplate<EmptyTranrs> response = new ResponseTemplate<>();
        HEADER header = new HEADER();
        header.setMsgid(request.getMwheader().getMsgid());
        header.setReturncode(ReturnCodeAndDescEnum.SUCCESS.getCode());
        header.setReturndesc(ReturnCodeAndDescEnum.SUCCESS.getDesc());
        response.setMwheader(header);
        response.setTranrs(new EmptyTranrs());

        return response;
    }
}