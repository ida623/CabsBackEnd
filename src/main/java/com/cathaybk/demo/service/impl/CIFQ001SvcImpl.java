package com.cathaybk.demo.service.impl;

import com.cathaybk.demo.common.ReturnCodeAndDescEnum;
import com.cathaybk.demo.dto.*;
import com.cathaybk.demo.entity.CustomerInfo;
import com.cathaybk.demo.exception.DataNotFoundException;
import com.cathaybk.demo.repository.CustomerInfoRepository;
import com.cathaybk.demo.service.CifQ001Svc;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * XXA-C-CIFQ001 根據orderId查詢客戶
 * 提供單筆客戶資料查詢功能
 */
@Service
@RequiredArgsConstructor
public class CifQ001SvcImpl implements CifQ001Svc {

    // 注入客戶資料Repository，用於資料庫操作
    private final CustomerInfoRepository customerInfoRepository;

    /**
     * 根據 orderId 查詢單筆客戶資料
     *
     * @param request 包含查詢條件的請求物件，其中 tranrq 包含 orderId
     * @return 包含客戶詳細資料的回應物件
     * @throws DataNotFoundException 當找不到對應的客戶資料時拋出
     * @throws IOException IO例外
     */
    @Override
    @Transactional(rollbackOn = Exception.class)
    public ResponseTemplate<CIFQ001Tranrs> getOne(RequestTemplate<CIFQ001Tranrq> request) throws DataNotFoundException, IOException {

        // 取得請求中的交易資料
        CIFQ001Tranrq tranrq = request.getTranrq();

        // 使用 JPA Repository 根據 orderId 查詢客戶資料
        // 若查無資料則回傳 null
        CustomerInfo customerInfo = customerInfoRepository.findById(tranrq.getOrderId())
                .orElse(null);

        // 若查無資料，拋出自訂例外
        if (customerInfo == null) {
            throw new DataNotFoundException("查無資料");
        }

        // 將 Entity 轉換為 DTO (Data Transfer Object)
        CIFQ001TranrsData data = new CIFQ001TranrsData();
        data.setOrderId(customerInfo.getOrderId());           // 訂單編號
        data.setIdNum(customerInfo.getIdNum());               // 身分證字號
        data.setChineseName(customerInfo.getChineseName());   // 中文姓名
        data.setGender(customerInfo.getGender());             // 性別
        data.setEducation(customerInfo.getEducation());       // 教育程度
        data.setZipCode1(customerInfo.getZipCode1());         // 戶籍地郵遞區號
        data.setAddress1(customerInfo.getAddress1());         // 戶籍地址
        data.setTelephone1(customerInfo.getTelephone1());     // 戶籍電話
        data.setZipCode2(customerInfo.getZipCode2());         // 通訊地郵遞區號
        data.setAddress2(customerInfo.getAddress2());         // 通訊地址
        data.setTelephone2(customerInfo.getTelephone2());     // 通訊電話
        data.setMobile(customerInfo.getMobile());             // 手機
        data.setEmail(customerInfo.getEmail());               // 電子郵件
        data.setYear(customerInfo.getYear());                 // 年度

        // 建立 TRANRS 回應資料物件
        CIFQ001Tranrs tranrs = new CIFQ001Tranrs();
        List<CIFQ001TranrsData> dataList = new ArrayList<>();
        dataList.add(data);
        tranrs.setData(dataList);

        // 建立 ResponseTemplate 並設定表頭資訊
        ResponseTemplate<CIFQ001Tranrs> response = new ResponseTemplate<>();
        HEADER header = new HEADER();
        header.setMsgid(request.getMwheader().getMsgid());                        // 訊息編號
        header.setReturncode(ReturnCodeAndDescEnum.SUCCESS.getCode());            // 回應代碼：0000(成功)
        header.setReturndesc(ReturnCodeAndDescEnum.SUCCESS.getDesc());            // 回應描述：交易成功
        response.setMwheader(header);
        response.setTranrs(tranrs);

        return response;
    }
}