package com.cathaybk.demo.service.impl;

import com.cathaybk.demo.common.ReturnCodeAndDescEnum;
import com.cathaybk.demo.dto.*;
import com.cathaybk.demo.entity.CustomerInfo;
import com.cathaybk.demo.exception.DataNotFoundException;
import com.cathaybk.demo.exception.DeleteFailException;
import com.cathaybk.demo.repository.CustomerInfoRepository;
import com.cathaybk.demo.service.CifT003Svc;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * XXA-C-CIFT003 刪除客戶資料
 * 提供刪除客戶資料功能
 *
 * 業務邏輯：
 * 1. 根據 ORDER_ID 查詢客戶資料是否存在
 * 2. 若存在則執行刪除
 * 3. 若不存在則拋出 DataNotFoundException
 */
@Service
@RequiredArgsConstructor
public class CifT003SvcImpl implements CifT003Svc {

    // 注入客戶資料Repository，用於資料庫操作
    private final CustomerInfoRepository customerInfoRepository;

    /**
     * 刪除客戶資料
     *
     * 執行流程：
     * 1. 根據 ORDER_ID 查詢客戶資料是否存在
     * 2. 若客戶資料不存在，拋出 DataNotFoundException
     * 3. 若客戶資料存在，執行刪除作業
     * 4. 回傳成功訊息
     *
     * @param request 包含待刪除客戶的 ORDER_ID 的請求物件
     * @return 包含執行結果的回應物件
     * @throws DataNotFoundException 當找不到對應的客戶資料時拋出
     * @throws IOException IO例外
     */
    @Override
    @Transactional(rollbackOn = Exception.class)
    public ResponseTemplate<EmptyTranrs> deleteInfo(RequestTemplate<CIFT003Tranrq> request) throws DataNotFoundException, IOException {

        // 取得請求中的交易資料
        CIFT003Tranrq tranrq = request.getTranrq();

        // 第一步：根據 ORDER_ID 查詢客戶資料是否存在
        CustomerInfo customerInfo = customerInfoRepository.findById(tranrq.getOrderId())
                .orElse(null);

        // 若查無資料，拋出 DataNotFoundException
        // 這樣可以避免刪除不存在的資料，提供明確的錯誤訊息
        if (customerInfo == null) {
            throw new DataNotFoundException("查無資料");
        }

        // 第二步：執行刪除作業
        // 使用 try-catch 捕捉資料庫操作異常
        try {
            customerInfoRepository.delete(customerInfo);
        } catch (Exception e) {
            // 若刪除失敗（例如：資料庫連線問題、外鍵約束等），拋出刪除失敗例外
            throw new DeleteFailException("刪除失敗");
        }

        // 第三步：建立成功回應
        ResponseTemplate<EmptyTranrs> response = new ResponseTemplate<>();
        HEADER header = new HEADER();
        header.setMsgid(request.getMwheader().getMsgid());                        // 訊息編號
        header.setReturncode(ReturnCodeAndDescEnum.SUCCESS.getCode());            // 回應代碼：0000(成功)
        header.setReturndesc(ReturnCodeAndDescEnum.SUCCESS.getDesc());            // 回應描述：交易成功
        response.setMwheader(header);
        response.setTranrs(new EmptyTranrs());  // 刪除成功不需回傳資料，使用空物件

        return response;
    }
}