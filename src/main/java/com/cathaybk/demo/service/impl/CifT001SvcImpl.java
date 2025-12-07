package com.cathaybk.demo.service.impl;

import com.cathaybk.demo.common.ReturnCodeAndDescEnum;
import com.cathaybk.demo.dto.*;
import com.cathaybk.demo.entity.CustomerInfo;
import com.cathaybk.demo.exception.InsertFailException;
import com.cathaybk.demo.repository.CustomerInfoRepository;
import com.cathaybk.demo.service.CifT001Svc;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * XXA-C-CIFT001 新增客戶資料
 */
@Service
@RequiredArgsConstructor
public class CifT001SvcImpl implements CifT001Svc {

    private final CustomerInfoRepository customerInfoRepository;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public ResponseTemplate<EmptyTranrs> createInfo(RequestTemplate<CIFT001Tranrq> request) throws IOException, InsertFailException {

        CIFT001Tranrq tranrq = request.getTranrq();
        CIFT001TranrqData data = tranrq.getData();

        // 檢查身份證號是否已存在
        List<CustomerInfo> existingCustomers = customerInfoRepository.findByIdNum(data.getIdNum());
        if (!existingCustomers.isEmpty()) {
            throw new InsertFailException("身份證號已存在");
        }

        // 建立新的客戶資料
        CustomerInfo customerInfo = new CustomerInfo();
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

        // 儲存資料
        try {
            customerInfoRepository.save(customerInfo);
        } catch (Exception e) {
            throw new InsertFailException("新增失敗");
        }

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