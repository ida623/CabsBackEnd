package com.cathaybk.demo.service.impl;

import java.io.IOException;
import java.sql.Timestamp;

import com.cathaybk.demo.dto.ANNC001Tranrq;
import com.cathaybk.demo.dto.EmptyTranrs;
import com.cathaybk.demo.dto.RequestTemplate;
import com.cathaybk.demo.dto.ResponseTemplate;
import com.cathaybk.demo.entity.AnnouncementEntity;
import com.cathaybk.demo.exception.RestException;
import com.cathaybk.demo.factory.NormalResponseFactory;
import com.cathaybk.demo.repository.AnnouncementRepository;
import com.cathaybk.demo.service.ANNC001Svc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

/**
 * CABS-B-ANNC001 新增公告
 *
 * @author
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ANNC001SvcImpl implements ANNC001Svc {

    /** NormalResponseFactory */
    private final NormalResponseFactory normalRespFactory;

    /** AnnouncementRepository */
    private final AnnouncementRepository announcementRepository;

    @Override
    public ResponseTemplate<EmptyTranrs> createAnnouncement(RequestTemplate<ANNC001Tranrq> req)
            throws IOException, RestException {

        ANNC001Tranrq tranrq = req.getTranrq();

        // STEP1: 取得登入者資訊
        String currentUser = getCurrentUserName();
        if (currentUser == null || currentUser.trim().isEmpty()) {
            throw new RestException("E300", "未取得使用者資訊");
        }

        // STEP2: 建立 ANNOUNCEMENT Entity
        AnnouncementEntity entity = new AnnouncementEntity();
        entity.setContent(tranrq.getContent());
        entity.setCreatedBy(currentUser);
        entity.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        entity.setUpdatedBy(currentUser);
        entity.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        // STEP3: JPA save
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