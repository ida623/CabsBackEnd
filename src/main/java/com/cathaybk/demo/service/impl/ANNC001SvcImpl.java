package com.cathaybk.demo.service.impl;

import com.cathaybk.demo.dto.ANNC001Tranrq;
import com.cathaybk.demo.dto.EmptyTranrs;
import com.cathaybk.demo.entity.AnnouncementEntity;
import com.cathaybk.demo.exception.RestException;
import com.cathaybk.demo.repository.AnnouncementRepo;
import com.cathaybk.demo.service.ANNC001Svc;
import com.cathaybk.demo.user.UserObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

/**
 * CABS-B-ANNC001 新增公告
 * @author
 */
@RequiredArgsConstructor
@Service
@Transactional(rollbackFor = Exception.class)
public class ANNC001SvcImpl implements ANNC001Svc {

    private final AnnouncementRepo announcementRepo;
    private final UserObject userObject;

    @Override
    public EmptyTranrs createAnnouncement(ANNC001Tranrq tranrq) throws RestException {

        String empName = userObject.getEmpName();
        if (empName == null || empName.isBlank()) {
            throw new RestException("E300", "未取得使用者資訊");
        }

        LocalDateTime now = LocalDateTime.now();

        AnnouncementEntity entity = new AnnouncementEntity();
        entity.setContent(tranrq.getContent());
        entity.setCreatedBy(empName);
        entity.setCreatedAt(now);
        entity.setUpdatedBy(empName);
        entity.setUpdatedAt(now);

        announcementRepo.save(entity);

        return new EmptyTranrs();
    }

}