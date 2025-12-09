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
 *
 * 業務邏輯：
 * - 若查無資料（身份證號可用）：RETURNCODE = 0000，RETURNDESC = 交易成功
 * - 若查到資料（身份證號已存在）：視為失敗，RETURNCODE = E003，RETURNDESC = 新增失敗
 */
@Service
@RequiredArgsConstructor
public class CifQ003SvcImpl implements CifQ003Svc {

    // 注入客戶資料Repository，用於資料庫操作
    private final CustomerInfoRepository customerInfoRepository;

    /**
     * 檢查身份證號是否已存在於系統中
     * 此功能通常用於新增客戶前的驗證，確保身份證號不重複
     *
     * @param request 包含待驗證身份證號的請求物件
     * @return 包含驗證結果的回應物件
     * @throws InsertFailException 當身份證號已存在時拋出（視為新增失敗）
     * @throws IOException IO例外
     */
    @Override
    @Transactional(rollbackOn = Exception.class)
    public ResponseTemplate<EmptyTranrs> checkId(RequestTemplate<CIFQ003Tranrq> request) throws InsertFailException, IOException {

        // 取得請求中的交易資料
        CIFQ003Tranrq tranrq = request.getTranrq();

        // 查詢 TB_CUSTOMER_INFO，檢查身份證號是否已存在
        // 使用 findByIdNum 方法查詢所有符合該身份證號的客戶資料
        List<CustomerInfo> existingCustomers = customerInfoRepository.findByIdNum(tranrq.getIdNum());

        // 若查到資料，表示身份證號已存在於系統中，視為驗證失敗
        // 拋出新增失敗例外，防止資料重複
        if (!existingCustomers.isEmpty()) {
            throw new InsertFailException("新增失敗，資料重複");
        }

        // 查無資料，表示身份證號可以使用（尚未被註冊）
        // 返回成功回應，允許後續新增作業
        ResponseTemplate<EmptyTranrs> response = new ResponseTemplate<>();
        HEADER header = new HEADER();
        header.setMsgid(request.getMwheader().getMsgid());                        // 訊息編號
        header.setReturncode(ReturnCodeAndDescEnum.SUCCESS.getCode());            // 回應代碼：0000(成功)
        header.setReturndesc(ReturnCodeAndDescEnum.SUCCESS.getDesc());            // 回應描述：交易成功
        response.setMwheader(header);
        response.setTranrs(new EmptyTranrs());  // 驗證成功不需回傳資料，使用空物件

        return response;
    }
}