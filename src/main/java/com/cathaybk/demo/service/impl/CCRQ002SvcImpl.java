// file: src/main/java/cfc/bff/platform/service/impl/CCRQ002SvcImpl.java
package com.cathaybk.demo.service.impl;

import java.time.format.DateTimeFormatter;

import com.cathaybk.demo.dto.CCRQ002Tranrq;
import com.cathaybk.demo.dto.CCRQ002Tranrs;
import com.cathaybk.demo.dto.RequestTemplate;
import com.cathaybk.demo.dto.ResponseTemplate;
import com.cathaybk.demo.entity.CabChangeRequestEntity;
import com.cathaybk.demo.exception.DataNotFoundException;
import com.cathaybk.demo.exception.RestException;
import com.cathaybk.demo.factory.NormalResponseFactory;
import com.cathaybk.demo.repository.CabChangeRequestRepository;
import com.cathaybk.demo.service.CCRQ002Svc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

/**
 * CABS-B-CCRQ002 查詢聯繫單
 *
 * @author 00550396
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CCRQ002SvcImpl implements CCRQ002Svc {

    /** CabChangeRequestRepository */
    private final CabChangeRequestRepository cabChangeRequestRepository;

    /** NormalResponseFactory */
    private final NormalResponseFactory normalResponseFactory;

    /** DateTimeFormatter */
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    @Override
    public ResponseTemplate<CCRQ002Tranrs> query(RequestTemplate<CCRQ002Tranrq> req)
            throws RestException, DataNotFoundException {

        String eformId = req.getTranrq().getEformId();

        // STEP1. 讀取資料（依 eformId）
        CabChangeRequestEntity entity = cabChangeRequestRepository.findById(eformId)
                .orElseThrow(() -> new DataNotFoundException("E200", "Change request not found: " + eformId));

        // STEP2. 轉換成下行電文
        CCRQ002Tranrs tranrs = new CCRQ002Tranrs();
        tranrs.setEformId(entity.getEformId());
        tranrs.setApid(entity.getApid());
        tranrs.setSysName(entity.getSysName());
        tranrs.setBiaLevelId(entity.getBiaLevelId());
        tranrs.setExecuteDate(formatDateTime(entity.getExecuteDate()));
        tranrs.setEndDate(formatDateTime(entity.getEndDate()));
        tranrs.setChangeDetails(entity.getChangeDetails());
        tranrs.setChangeTypeId(entity.getChangeTypeId());
        tranrs.setRiskAssessmentId(entity.getRiskAssessmentId());
        tranrs.setAttributesId(entity.getAttributesId());
        tranrs.setIsDowntimeRequires(entity.getIsDowntimeRequires());
        tranrs.setIsChangedb(entity.getIsChangedb());
        tranrs.setIsAnn(entity.getIsAnn());
        tranrs.setProgramPhone(entity.getProgramPhone());
        tranrs.setAffectedNotes(entity.getAffectedNotes());
        tranrs.setHri(entity.getHri());
        tranrs.setCreatedEmpid(entity.getCreatedEmpid());
        tranrs.setCreatedEmpName(entity.getCreatedEmpname());
        tranrs.setCreatedDept(entity.getCreatedDept());
        tranrs.setCreatedPhoneExt(entity.getCreatedPhoneExt());
        tranrs.setCreatedDate(formatDateTime(entity.getCreatedDate()));
        tranrs.setModifyDate(formatDateTime(entity.getModifyDate()));
        tranrs.setIsActive(entity.getIsActive());
        tranrs.setReviewStatus(entity.getReviewStatus());
        tranrs.setReviewDate(formatDateTime(entity.getReviewDate()));
        tranrs.setReviewModifyEmpid(entity.getReviewModifyEmpid());
        tranrs.setReviewModifyTime(formatDateTime(entity.getReviewModifyTime()));
        tranrs.setIsAiCab(entity.getIsAiCab());
        tranrs.setAiCabEformId(entity.getAiCabEformId());

        return normalResponseFactory.genNormalResponse(tranrs, req);
    }

    /**
     * 格式化日期時間
     *
     * @param dateTime
     * @return
     */
    private String formatDateTime(java.time.LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(FORMATTER);
    }

}