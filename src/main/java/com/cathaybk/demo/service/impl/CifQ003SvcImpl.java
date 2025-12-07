package com.cathaybk.demo.service.impl;

import com.cathaybk.demo.common.ReturnCodeAndDescEnum;
import com.cathaybk.demo.dto.*;
import com.cathaybk.demo.entity.CustomerInfo;
import com.cathaybk.demo.exception.InsertFailException;
import com.cathaybk.demo.repository.CustomerInfoRepository;
import com.cathaybk.demo.service.CifQ003Svc;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * XXA-C-CIFQ003 身份ID驗證
 * 用於驗證身份證ID_NUM是否存在
 * 若查無資料則RETURNCODE = 0000，RETURNDESC = 交易成功
 * 查到資料視為失敗，RETURNCODE = E003，RETURNDESC = 新增失敗
 */
@Service
@RequiredArgsConstructor
public class CifQ003SvcImpl implements CifQ003Svc {

    private final CustomerInfoRepository customerInfoRepository;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public ResponseTemplate<EmptyTranrs> checkId(RequestTemplate<CIFQ003Tranrq> request) throws InsertFailException, IOException {

        CIFQ003Tranrq tranrq = request.getTranrq();

        // 查詢TB_CUSTOMER_INFO，檢查身份證號是否已存在
        List<CustomerInfo> existingCustomers = customerInfoRepository.findByIdNum(tranrq.getIdNum());

        // 若查到資料，表示身份證號已存在，視為失敗
        if (!existingCustomers.isEmpty()) {
            throw new InsertFailException("新增失敗，資料重複");
        }

        // 查無資料，表示身份證號可以使用，返回成功
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