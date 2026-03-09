package com.cathaybk.demo.service.impl;

import java.text.SimpleDateFormat;

import com.cathaybk.demo.dto.ANNQ002Tranrq;
import com.cathaybk.demo.dto.ANNQ002Tranrs;
import com.cathaybk.demo.dto.RequestTemplate;
import com.cathaybk.demo.dto.ResponseTemplate;
import com.cathaybk.demo.entity.AnnouncementEntity;
import com.cathaybk.demo.exception.DataNotFoundException;
import com.cathaybk.demo.factory.NormalResponseFactory;
import com.cathaybk.demo.repository.AnnouncementRepository;
import com.cathaybk.demo.service.ANNQ002Svc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

/**
 * CABS-B-ANNQ002 查詢公告
 *
 * @author
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ANNQ002SvcImpl implements ANNQ002Svc {

    /** NormalResponseFactory */
    private final NormalResponseFactory normalResponseFactory;

    /** AnnouncementRepository */
    private final AnnouncementRepository announcementRepository;

    @Override
    public ResponseTemplate<ANNQ002Tranrs> query(RequestTemplate<ANNQ002Tranrq> req)
            throws DataNotFoundException {

        ANNQ002Tranrq tranrq = req.getTranrq();

        // STEP1: 透過 ID 查詢 ANNOUNCEMENT
        AnnouncementEntity entity = announcementRepository.findById(tranrq.getId())
                .orElseThrow(() -> new DataNotFoundException("E200", "查無該筆公告事項"));

        // STEP2: 轉換為下行電文
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        
        ANNQ002Tranrs tranrs = new ANNQ002Tranrs();
        tranrs.setId(entity.getId());
        tranrs.setContent(entity.getContent());
        tranrs.setCreatedBy(entity.getCreatedBy());
        tranrs.setCreatedAt(entity.getCreatedAt() != null ? sdf.format(entity.getCreatedAt()) : null);
        tranrs.setUpdatedBy(entity.getUpdatedBy());
        tranrs.setUpdatedAt(entity.getUpdatedAt() != null ? sdf.format(entity.getUpdatedAt()) : null);

        return normalResponseFactory.genNormalResponse(tranrs, req);
    }

}