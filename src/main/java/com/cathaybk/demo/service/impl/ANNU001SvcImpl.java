package com.cathaybk.demo.service.impl;

import com.cathaybk.demo.dto.ANNU001Tranrq;
import com.cathaybk.demo.dto.EmptyTranrs;
import com.cathaybk.demo.entity.AnnouncementEntity;
import com.cathaybk.demo.exception.DataNotFoundException;
import com.cathaybk.demo.exception.RestException;
import com.cathaybk.demo.repository.AnnouncementRepo;
import com.cathaybk.demo.service.ANNU001Svc;
import com.cathaybk.demo.user.UserObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * CABS-B-ANNU001 修改公告
 * @author
 */
@RequiredArgsConstructor
@Service
@Transactional(rollbackFor = Exception.class)
public class ANNU001SvcImpl implements ANNU001Svc {

    private final AnnouncementRepo announcementRepo;
    private final UserObject userObject;

    @Override
    public EmptyTranrs updateAnnouncement(ANNU001Tranrq tranrq) throws RestException, DataNotFoundException {

        String empName = userObject.getEmpName();
        if (empName == null || empName.isBlank()) {
            throw new RestException("E300", "未取得使用者資訊");
        }

        AnnouncementEntity entity = announcementRepo.findById(tranrq.getId())
                .orElseThrow(() -> new DataNotFoundException("查無該筆公告事項"));

        entity.setContent(tranrq.getContent());
        entity.setUpdatedBy(empName);
        entity.setUpdatedAt(LocalDateTime.now());

        announcementRepo.save(entity);

        return new EmptyTranrs();
    }

}