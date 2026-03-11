package com.cathaybk.demo.service.impl;

import com.cathaybk.demo.dto.ANND001Tranrq;
import com.cathaybk.demo.dto.EmptyTranrs;
import com.cathaybk.demo.entity.AnnouncementEntity;
import com.cathaybk.demo.exception.DataNotFoundException;
import com.cathaybk.demo.exception.RestException;
import com.cathaybk.demo.repository.AnnouncementRepo;
import com.cathaybk.demo.service.ANND001Svc;
import com.cathaybk.demo.user.UserObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * CABS-B-ANND001 刪除公告
 * @author
 */
@RequiredArgsConstructor
@Service
@Transactional(rollbackFor = Exception.class)
public class ANND001SvcImpl implements ANND001Svc {

    private final AnnouncementRepo announcementRepo;
    private final UserObject userObject;

    @Override
    public EmptyTranrs deleteAnnouncement(ANND001Tranrq tranrq) throws RestException, DataNotFoundException {

        String empName = userObject.getEmpName();
        if (empName == null || empName.isBlank()) {
            throw new RestException("E300", "未取得使用者資訊");
        }

        AnnouncementEntity entity = announcementRepo.findById(tranrq.getId())
                .orElseThrow(() -> new DataNotFoundException("查無該筆公告事項"));

        announcementRepo.delete(entity);

        return new EmptyTranrs();
    }

}