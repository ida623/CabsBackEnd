package com.cathaybk.demo.service.impl;

import com.cathaybk.demo.common.ReturnCodeAndDescEnum;
import com.cathaybk.demo.dto.*;
import com.cathaybk.demo.entity.CustomerInfo;
import com.cathaybk.demo.exception.DataNotFoundException;
import com.cathaybk.demo.exception.UpdateFailException;
import com.cathaybk.demo.repository.CustomerInfoRepository;
import com.cathaybk.demo.service.CifT002Svc;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

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

        // 1. 查詢客戶資料是否存在
        CustomerInfo customerInfo = customerInfoRepository.findById(data.getOrderId())
                .orElse(null);

        if (customerInfo == null) {
            throw new DataNotFoundException("查無資料");
        }

        // 2. 如果要修改身份證號，檢查新的身份證號是否已被其他人使用
        if (!customerInfo.getIdNum().equals(data.getIdNum())) {
            List<CustomerInfo> existingCustomers = customerInfoRepository.findByIdNum(data.getIdNum());
            if (!existingCustomers.isEmpty()) {
                throw new UpdateFailException("更新失敗：身份證號已被其他客戶使用");
            }
        }

        // 3. 更新資料
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

        // 4. 儲存更新（加入錯誤處理）
        try {
            customerInfoRepository.save(customerInfo);
        } catch (Exception e) {
            throw new UpdateFailException("更新失敗");
        }

        // 5. 建立成功回應
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