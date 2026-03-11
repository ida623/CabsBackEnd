package com.cathaybk.demo.service.impl;

import com.cathaybk.demo.dto.ANNQ002Tranrq;
import com.cathaybk.demo.dto.ANNQ002Tranrs;
import com.cathaybk.demo.entity.AnnouncementEntity;
import com.cathaybk.demo.exception.DataNotFoundException;
import com.cathaybk.demo.repository.AnnouncementRepo;
import com.cathaybk.demo.service.ANNQ002Svc;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

/**
 * CABS-B-ANNQ002 查詢公告
 * @author
 */
@RequiredArgsConstructor
@Service
public class ANNQ002SvcImpl implements ANNQ002Svc {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    private final AnnouncementRepo announcementRepo;

    @Override
    public ANNQ002Tranrs queryAnnouncement(ANNQ002Tranrq tranrq) throws DataNotFoundException {

        AnnouncementEntity entity = announcementRepo.findById(tranrq.getId())
                .orElseThrow(() -> new DataNotFoundException("查無該筆公告事項"));

        return new ANNQ002Tranrs(
                entity.getId(),
                entity.getContent(),
                entity.getCreatedBy(),
                entity.getCreatedAt().format(FORMATTER),
                entity.getUpdatedBy(),
                entity.getUpdatedAt().format(FORMATTER));
    }

}