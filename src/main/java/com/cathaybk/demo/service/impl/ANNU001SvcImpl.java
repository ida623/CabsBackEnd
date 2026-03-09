package com.cathaybk.demo.service.impl;

import java.sql.Timestamp;

import com.cathaybk.demo.dto.ANNU001Tranrq;
import com.cathaybk.demo.dto.EmptyTranrs;
import com.cathaybk.demo.dto.RequestTemplate;
import com.cathaybk.demo.dto.ResponseTemplate;
import com.cathaybk.demo.entity.AnnouncementEntity;
import com.cathaybk.demo.exception.DataNotFoundException;
import com.cathaybk.demo.exception.RestException;
import com.cathaybk.demo.factory.NormalResponseFactory;
import com.cathaybk.demo.repository.AnnouncementRepository;
import com.cathaybk.demo.service.ANNU001Svc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

/**
 * CABS-B-ANNU001 修改公告
 *
 * @author 張育誠
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ANNU001SvcImpl implements ANNU001Svc {

    /** NormalResponseFactory */
    private final NormalResponseFactory normalRespFactory;

    /** AnnouncementRepository */
    private final AnnouncementRepository announcementRepository;

    @Override
    public ResponseTemplate<EmptyTranrs> update(RequestTemplate<ANNU001Tranrq> req)
            throws DataNotFoundException, RestException {

        ANNU001Tranrq tranrq = req.getTranrq();

        // STEP1: 取得登入者資訊
        String currentUser = getCurrentUserName();
        if (currentUser == null || currentUser.trim().isEmpty()) {
            throw new RestException("E300", "未取得使用者資訊");
        }

        // STEP2: 讀取資料（依 ID）
        AnnouncementEntity entity = announcementRepository.findById(tranrq.getId())
                .orElseThrow(() -> new DataNotFoundException("E200", "查無該筆公告事項"));

        // STEP3: 更新欄位並保存
        entity.setContent(tranrq.getContent());
        entity.setUpdatedBy(currentUser);
        entity.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        announcementRepository.save(entity);

        return normalRespFactory.genNormalResponse(new EmptyTranrs(), req);
    }

    /**
     * 取得目前登入者姓名
     *
     * @return 登入者姓名
     */
    private String getCurrentUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return null;
    }

}