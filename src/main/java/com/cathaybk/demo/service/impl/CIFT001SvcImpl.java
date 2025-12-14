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
 * 提供新增客戶資料功能，並確保身份證號不重複
 *
 * 業務邏輯：
 * 1. 檢查身份證號是否已存在
 * 2. 若身份證號不存在，則新增客戶資料
 * 3. 若身份證號已存在，則拋出 InsertFailException
 */
@Service
@RequiredArgsConstructor
public class CifT001SvcImpl implements CifT001Svc {

    // 注入客戶資料Repository，用於資料庫操作
    private final CustomerInfoRepository customerInfoRepository;

    /**
     * 新增客戶資料
     *
     * 執行流程：
     * 1. 檢查身份證號是否已存在（防止重複註冊）
     * 2. 建立新的 CustomerInfo 實體物件
     * 3. 將請求資料複製到實體物件
     * 4. 儲存到資料庫
     * 5. 回傳成功訊息
     *
     * @param request 包含新增客戶資料的請求物件
     * @return 包含執行結果的回應物件
     * @throws IOException IO例外
     * @throws InsertFailException 當身份證號已存在或新增失敗時拋出
     */
    @Override
    @Transactional(rollbackOn = Exception.class)
    public ResponseTemplate<EmptyTranrs> createInfo(RequestTemplate<CIFT001Tranrq> request) throws IOException, InsertFailException {

        // 取得請求中的交易資料
        CIFT001Tranrq tranrq = request.getTranrq();
        CIFT001TranrqData data = tranrq.getData();

        // 第一步：檢查身份證號是否已存在
        // 避免同一身份證號重複註冊，確保資料唯一性
        List<CustomerInfo> existingCustomers = customerInfoRepository.findByIdNum(data.getIdNum());
        if (!existingCustomers.isEmpty()) {
            throw new InsertFailException("身份證號已存在");
        }

        // 第二步：建立新的客戶資料實體物件
        CustomerInfo customerInfo = new CustomerInfo();
        // 將請求資料複製到實體物件（ORDER_ID 由資料庫自動產生）
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

        // 第三步：儲存資料到資料庫
        // 使用 try-catch 捕捉資料庫操作異常
        try {
            customerInfoRepository.save(customerInfo);
        } catch (Exception e) {
            // 若儲存失敗（例如：資料庫連線問題、欄位限制違反等），拋出新增失敗例外
            throw new InsertFailException("新增失敗");
        }

        // 第四步：建立成功回應
        ResponseTemplate<EmptyTranrs> response = new ResponseTemplate<>();
        HEADER header = new HEADER();
        header.setMsgid(request.getMwheader().getMsgid());                        // 訊息編號
        header.setReturncode(ReturnCodeAndDescEnum.SUCCESS.getCode());            // 回應代碼：0000(成功)
        header.setReturndesc(ReturnCodeAndDescEnum.SUCCESS.getDesc());            // 回應描述：交易成功
        response.setMwheader(header);
        response.setTranrs(new EmptyTranrs());  // 新增成功不需回傳資料，使用空物件

        return response;
    }
}