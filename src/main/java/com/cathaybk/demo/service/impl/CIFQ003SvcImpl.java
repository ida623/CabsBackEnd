package com.cathaybk.demo.service.impl;

import com.cathaybk.demo.common.ReturnCodeAndDescEnum;
import com.cathaybk.demo.dto.*;
import com.cathaybk.demo.entity.CustomerInfoEntity;
import com.cathaybk.demo.exception.InsertFailException;
import com.cathaybk.demo.repository.CustomerInfoRepository;
import com.cathaybk.demo.service.CIFQ003Svc;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * XXA-C-CIFQ003 身份ID驗證
 */
@Service
@RequiredArgsConstructor
public class CIFQ003SvcImpl implements CIFQ003Svc {

    private final CustomerInfoRepository customerInfoRepository;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public ResponseTemplate<EmptyTranrs> checkId(RequestTemplate<CIFQ003Tranrq> request) throws InsertFailException {

        // 取得請求
        CIFQ003Tranrq tranrq = request.getTranrq();

        // 單表查詢，檢查身份證號是否存在
        List<CustomerInfoEntity> existingCustomers = customerInfoRepository.findByIdNum(tranrq.getIdNum());

        // TODO CollectionUtils.isNotEmpty(existingCustomers);
        // 查到身分證資料，資料重複，導致驗證失敗
        if (CollectionUtils.isNotEmpty(existingCustomers)) {
            throw new InsertFailException("新增失敗，資料重複");
        }

        // TODO 善用 @AllArgsConstructor , @NoArgsConstructor
        ResponseTemplate<EmptyTranrs> response = new ResponseTemplate<>();
        HEADER header = new HEADER(
                request.getMwheader().getMsgid(),
                ReturnCodeAndDescEnum.SUCCESS.getCode(),
                ReturnCodeAndDescEnum.SUCCESS.getDesc()
        );
        response.setMwheader(header);
        response.setTranrs(new EmptyTranrs());  // 驗證成功，使用空物件

        return response;
    }
}