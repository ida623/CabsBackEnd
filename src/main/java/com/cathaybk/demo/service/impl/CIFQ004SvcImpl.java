package com.cathaybk.demo.service.impl;

import com.cathaybk.demo.common.ReturnCodeAndDescEnum;
import com.cathaybk.demo.dto.*;
import com.cathaybk.demo.entity.MsgCode;
import com.cathaybk.demo.repository.MsgCodeRepository;
import com.cathaybk.demo.service.CifQ004Svc;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * XXA-C-CIFQ004 查詢共用代碼
 * 提供系統共用代碼查詢功能，包含郵遞區號(Postal)和教育程度(Education)
 */
@Service
@RequiredArgsConstructor
public class CifQ004SvcImpl implements CifQ004Svc {

    // 注入訊息代碼Repository，用於查詢共用代碼資料
    private final MsgCodeRepository msgCodeRepository;

    /**
     * 查詢共用代碼資料
     * 一次回傳兩類代碼：郵遞區號(Postal)和教育程度(Education)
     * 供前端下拉選單或選項使用
     *
     * @param request 請求物件
     * @return 包含郵遞區號和教育程度代碼清單的回應物件
     * @throws IOException IO例外
     */
    @Override
    @Transactional(rollbackOn = Exception.class)
    public ResponseTemplate<CIFQ004Tranrs> commCode(RequestTemplate<CIFQ004Tranrq> request) throws IOException {

        // 查詢 Postal 郵遞區號代碼
        // 從 TB_MSG_CODE 資料表中取得 MSG_CODE = "Postal" 的所有記錄
        List<MsgCode> postalCodes = msgCodeRepository.findByMsgCode("Postal");
        // 使用 Stream API 將 Entity 轉換為 DTO
        List<CIFQ004TranrsItems> postalItems = postalCodes.stream()
                .map(this::convertToTranrsItem)  // 呼叫轉換方法
                .collect(Collectors.toList());

        // 查詢 Education 教育程度代碼
        // 從 TB_MSG_CODE 資料表中取得 MSG_CODE = "Education" 的所有記錄
        List<MsgCode> educationCodes = msgCodeRepository.findByMsgCode("Education");
        // 使用 Stream API 將 Entity 轉換為 DTO
        List<CIFQ004TranrsItems> educationItems = educationCodes.stream()
                .map(this::convertToTranrsItem)  // 呼叫轉換方法
                .collect(Collectors.toList());

        // 建立 TRANRS 回應資料物件，並設定兩類代碼清單
        CIFQ004Tranrs tranrs = new CIFQ004Tranrs();
        tranrs.setPostal(postalItems);           // 設定郵遞區號清單
        tranrs.setEducation(educationItems);     // 設定教育程度清單

        // 建立 ResponseTemplate 並設定表頭資訊
        ResponseTemplate<CIFQ004Tranrs> response = new ResponseTemplate<>();
        HEADER header = new HEADER();
        header.setMsgid(request.getMwheader().getMsgid());                        // 訊息編號
        header.setReturncode(ReturnCodeAndDescEnum.SUCCESS.getCode());            // 回應代碼：0000(成功)
        header.setReturndesc(ReturnCodeAndDescEnum.SUCCESS.getDesc());            // 回應描述：交易成功
        response.setMwheader(header);
        response.setTranrs(tranrs);

        return response;
    }

    /**
     * 轉換 Entity 為 DTO
     * 將 MsgCode 實體物件轉換為前端使用的 CIFQ004TranrsItems 物件
     *
     * @param msgCode 訊息代碼實體物件
     * @return 轉換後的 DTO 物件
     */
    private CIFQ004TranrsItems convertToTranrsItem(MsgCode msgCode) {
        CIFQ004TranrsItems item = new CIFQ004TranrsItems();
        item.setMsgOption(msgCode.getMsgOption());               // 代碼選項值
        item.setMsgOptionMemo(msgCode.getMsgOptionMemo());       // 代碼選項說明
        item.setMsgOptionSerno(msgCode.getMsgOptionSerno());     // 代碼選項序號
        return item;
    }
}