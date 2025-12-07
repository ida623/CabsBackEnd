package com.cathaybk.demo.service.impl;

import com.cathaybk.demo.common.ReturnCodeAndDescEnum;
import com.cathaybk.demo.dto.*;
import com.cathaybk.demo.entity.CustomerInfo;
import com.cathaybk.demo.exception.DataNotFoundException;
import com.cathaybk.demo.repository.CustomerInfoRepository;
import com.cathaybk.demo.service.CifT002Svc;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * XXA-C-CIFT002 修改客戶資料
 */
@Service
@RequiredArgsConstructor
public class CifT002SvcImpl implements CifT002Svc {

    private final CustomerInfoRepository customerInfoRepository;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public ResponseTemplate<EmptyTranrs> editInfo(RequestTemplate<CIFT002Tranrq> request) throws DataNotFoundException, IOException {

        CIFT002Tranrq tranrq = request.getTranrq();
        CIFT002TranrqData data = tranrq.getData();

        // 查詢客戶資料是否存在
        CustomerInfo customerInfo = customerInfoRepository.findById(data.getOrderId())
                .orElse(null);

        if (customerInfo == null) {
            throw new DataNotFoundException("查無資料");
        }

        // 更新資料
        customerInfo.setIdNum(data.getIdNum());
        customerInfo.setChineseName(data.getChineseName());
        customerInfo.setGender(data.getGender());
        customerInfo.setEducation(data.getEducation());
        customerInfo.setZipCode1(data.getZipCode1());
        customerInfo.setAddress1(data.getAddress1());
        customerInfo.setTelephone1(data.getTelephone1());
        customerInfo.setZipCode2(data.getZipCode2());
        customerInfo.setAddress2(data.getAddress2());
        customerInfo.setTelephone2(data.getTelephone2());
        customerInfo.setMobile(data.getMobile());
        customerInfo.setEmail(data.getEmail());
        customerInfo.setYear(data.getYear());

        // JPA 會自動更新（因為在 @Transactional 內）
        customerInfoRepository.save(customerInfo);

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