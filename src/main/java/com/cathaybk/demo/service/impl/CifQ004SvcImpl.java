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
 */
@Service
@RequiredArgsConstructor
public class CifQ004SvcImpl implements CifQ004Svc {

    private final MsgCodeRepository msgCodeRepository;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public ResponseTemplate<CIFQ004Tranrs> commCode(RequestTemplate<CIFQ004Tranrq> request) throws IOException {

        // 查詢 Postal 郵遞區號
        List<MsgCode> postalCodes = msgCodeRepository.findByMsgCode("Postal");
        List<CIFQ004TranrsItems> postalItems = postalCodes.stream()
                .map(this::convertToTranrsItem)
                .collect(Collectors.toList());

        // 查詢 Education 教育程度
        List<MsgCode> educationCodes = msgCodeRepository.findByMsgCode("Education");
        List<CIFQ004TranrsItems> educationItems = educationCodes.stream()
                .map(this::convertToTranrsItem)
                .collect(Collectors.toList());

        // 建立 TRANRS
        CIFQ004Tranrs tranrs = new CIFQ004Tranrs();
        tranrs.setPostal(postalItems);
        tranrs.setEducation(educationItems);

        // 建立 ResponseTemplate
        ResponseTemplate<CIFQ004Tranrs> response = new ResponseTemplate<>();
        HEADER header = new HEADER();
        header.setMsgid(request.getMwheader().getMsgid());
        header.setReturncode(ReturnCodeAndDescEnum.SUCCESS.getCode());
        header.setReturndesc(ReturnCodeAndDescEnum.SUCCESS.getDesc());
        response.setMwheader(header);
        response.setTranrs(tranrs);

        return response;
    }

    /**
     * 轉換 Entity 為 DTO
     */
    private CIFQ004TranrsItems convertToTranrsItem(MsgCode msgCode) {
        CIFQ004TranrsItems item = new CIFQ004TranrsItems();
        item.setMsgOption(msgCode.getMsgOption());
        item.setMsgOptionMemo(msgCode.getMsgOptionMemo());
        item.setMsgOptionSerno(msgCode.getMsgOptionSerno());
        return item;
    }
}