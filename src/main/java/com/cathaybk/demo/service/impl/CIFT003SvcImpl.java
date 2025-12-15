package com.cathaybk.demo.service.impl;

import com.cathaybk.demo.common.ReturnCodeAndDescEnum;
import com.cathaybk.demo.dto.*;
import com.cathaybk.demo.entity.CustomerInfoEntity;
import com.cathaybk.demo.exception.DataNotFoundException;
import com.cathaybk.demo.exception.DeleteFailException;
import com.cathaybk.demo.repository.CustomerInfoRepository;
import com.cathaybk.demo.service.CIFT003Svc;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * XXA-C-CIFT003 刪除客戶資料
 */
@Service
@RequiredArgsConstructor
public class CIFT003SvcImpl implements CIFT003Svc {

    private final CustomerInfoRepository customerInfoRepository;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public ResponseTemplate<EmptyTranrs> deleteInfo(RequestTemplate<CIFT003Tranrq> request) throws DataNotFoundException {

        // 取得請求
        CIFT003Tranrq tranrq = request.getTranrq();

        // TODO 拋出例外 合併寫法
        // 使用 ORDER_ID 查詢客戶資料是否存在
        CustomerInfoEntity customerInfoEntity = customerInfoRepository.findById(tranrq.getOrderId())
                .orElseThrow(() -> new DataNotFoundException("查無資料"));

        // 執行刪除
        try {
            customerInfoRepository.delete(customerInfoEntity);
        } catch (Exception e) {
            throw new DeleteFailException("刪除失敗");
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
        response.setTranrs(new EmptyTranrs());

        return response;
    }
}