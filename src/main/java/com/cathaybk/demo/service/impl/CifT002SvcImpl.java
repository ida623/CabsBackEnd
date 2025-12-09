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
 * 提供修改客戶資料功能，並確保身份證號不與其他客戶重複
 *
 * 業務邏輯：
 * 1. 根據 ORDER_ID 查詢客戶資料是否存在
 * 2. 若要修改身份證號，檢查新身份證號是否已被其他客戶使用
 * 3. 更新客戶所有欄位資料
 * 4. 儲存到資料庫
 */
@Service
@RequiredArgsConstructor
public class CifT002SvcImpl implements CifT002Svc {

    // 注入客戶資料Repository，用於資料庫操作
    private final CustomerInfoRepository customerInfoRepository;

    /**
     * 修改客戶資料
     *
     * 執行流程：
     * 1. 根據 ORDER_ID 查詢客戶資料是否存在
     * 2. 若要修改身份證號，檢查新身份證號是否已被其他客戶使用
     * 3. 更新實體物件的所有欄位
     * 4. 儲存更新到資料庫
     * 5. 回傳成功訊息
     *
     * @param request 包含修改客戶資料的請求物件（必須包含 ORDER_ID）
     * @return 包含執行結果的回應物件
     * @throws DataNotFoundException 當找不到對應的客戶資料時拋出
     * @throws IOException IO例外
     */
    @Override
    @Transactional(rollbackOn = Exception.class)
    public ResponseTemplate<EmptyTranrs> editInfo(RequestTemplate<CIFT002Tranrq> request) throws DataNotFoundException, IOException {

        // 取得請求中的交易資料
        CIFT002Tranrq tranrq = request.getTranrq();
        CIFT002TranrqData data = tranrq.getData();

        // 第一步：根據 ORDER_ID 查詢客戶資料是否存在
        CustomerInfo customerInfo = customerInfoRepository.findById(data.getOrderId())
                .orElse(null);

        // 若查無資料，拋出 DataNotFoundException
        if (customerInfo == null) {
            throw new DataNotFoundException("查無資料");
        }

        // 第二步：若要修改身份證號，檢查新的身份證號是否已被其他客戶使用
        // 比較原身份證號與新身份證號，若不同則需要檢查
        if (!customerInfo.getIdNum().equals(data.getIdNum())) {
            // 查詢新身份證號是否已存在於系統中
            List<CustomerInfo> existingCustomers = customerInfoRepository.findByIdNum(data.getIdNum());
            if (!existingCustomers.isEmpty()) {
                // 若新身份證號已被其他客戶使用，拋出更新失敗例外
                throw new UpdateFailException("更新失敗：身份證號已被其他客戶使用");
            }
        }

        // 第三步：更新客戶資料的所有欄位
        customerInfo.setIdNum(data.getIdNum());                   // 身份證字號
        customerInfo.setChineseName(data.getChineseName());       // 中文姓名
        customerInfo.setGender(data.getGender());                 // 性別
        customerInfo.setEducation(data.getEducation());           // 教育程度
        customerInfo.setZipCode1(data.getZipCode1());             // 戶籍地郵遞區號
        customerInfo.setAddress1(data.getAddress1());             // 戶籍地址
        customerInfo.setTelephone1(data.getTelephone1());         // 戶籍電話
        customerInfo.setZipCode2(data.getZipCode2());             // 通訊地郵遞區號
        customerInfo.setAddress2(data.getAddress2());             // 通訊地址
        customerInfo.setTelephone2(data.getTelephone2());         // 通訊電話
        customerInfo.setMobile(data.getMobile());                 // 手機號碼
        customerInfo.setEmail(data.getEmail());                   // 電子郵件
        customerInfo.setYear(data.getYear());                     // 年度

        // 第四步：儲存更新到資料庫
        // 使用 try-catch 捕捉資料庫操作異常
        try {
            customerInfoRepository.save(customerInfo);
        } catch (Exception e) {
            // 若儲存失敗，拋出更新失敗例外
            throw new UpdateFailException("更新失敗");
        }

        // 第五步：建立成功回應
        ResponseTemplate<EmptyTranrs> response = new ResponseTemplate<>();
        HEADER header = new HEADER();
        header.setMsgid(request.getMwheader().getMsgid());                        // 訊息編號
        header.setReturncode(ReturnCodeAndDescEnum.SUCCESS.getCode());            // 回應代碼：0000(成功)
        header.setReturndesc(ReturnCodeAndDescEnum.SUCCESS.getDesc());            // 回應描述：交易成功
        response.setMwheader(header);
        response.setTranrs(new EmptyTranrs());  // 更新成功不需回傳資料，使用空物件

        return response;
    }
}