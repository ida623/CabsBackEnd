package com.cathaybk.demo.service.impl;

import com.cathaybk.demo.common.ReturnCodeAndDescEnum;
import com.cathaybk.demo.dto.*;
import com.cathaybk.demo.repository.MsgCodeRepository;
import com.cathaybk.demo.service.CIFQ004Svc;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * XXA-C-CIFQ004 查詢代碼
 */
@Service
@RequiredArgsConstructor
public class CIFQ004SvcImpl implements CIFQ004Svc {

    private final MsgCodeRepository msgCodeRepository;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public ResponseTemplate<CIFQ004Tranrs> commCode(RequestTemplate<CIFQ004Tranrq> request) {

        // TODO java17 以上 可用 .toList(); == .collect(Collectors.toList())
        // 查詢 Postal 郵遞區號代碼，轉換為 DTO
        List<CIFQ004TranrsItems> postalItems = msgCodeRepository.findByMsgCode("Postal").stream()
                .map(msgCode -> {
                    CIFQ004TranrsItems item = new CIFQ004TranrsItems();
                    item.setMsgOption(msgCode.getMsgOption());
                    item.setMsgOptionMemo(msgCode.getMsgOptionMemo());
                    item.setMsgOptionSerno(msgCode.getMsgOptionSerno());
                    return item;
                })
                .toList();

        // 查詢 Education 教育程度代碼，轉換為 DTO
        List<CIFQ004TranrsItems> educationItems = msgCodeRepository.findByMsgCode("Education").stream()
                .map(msgCode -> {
                    CIFQ004TranrsItems item = new CIFQ004TranrsItems();
                    item.setMsgOption(msgCode.getMsgOption());
                    item.setMsgOptionMemo(msgCode.getMsgOptionMemo());
                    item.setMsgOptionSerno(msgCode.getMsgOptionSerno());
                    return item;
                })
                .toList();

        // 建立 TRANRS
        CIFQ004Tranrs tranrs = new CIFQ004Tranrs();
        tranrs.setPostal(postalItems);
        tranrs.setEducation(educationItems);

        // TODO 善用 @AllArgsConstructor , @NoArgsConstructor
        // 建立回應，設定 HEADER
        ResponseTemplate<CIFQ004Tranrs> response = new ResponseTemplate<>();
        HEADER header = new HEADER(
                request.getMwheader().getMsgid(),
                ReturnCodeAndDescEnum.SUCCESS.getCode(),
                ReturnCodeAndDescEnum.SUCCESS.getDesc()
        );
        response.setMwheader(header);
        response.setTranrs(tranrs);

        return response;
    }
}