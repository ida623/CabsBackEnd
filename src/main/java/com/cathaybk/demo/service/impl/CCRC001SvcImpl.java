package com.cathaybk.demo.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import com.cathaybk.demo.dto.CCRC001Tranrq;
import com.cathaybk.demo.dto.EmptyTranrs;
import com.cathaybk.demo.dto.RequestTemplate;
import com.cathaybk.demo.dto.ResponseTemplate;
import com.cathaybk.demo.entity.CabChangeRequestEntity;
import com.cathaybk.demo.entity.ReleaseActionLogEntity;
import com.cathaybk.demo.exception.RestException;
import com.cathaybk.demo.factory.NormalResponseFactory;
import com.cathaybk.demo.repository.CabChangeRequestRepository;
import com.cathaybk.demo.repository.ReleaseActionLogRepository;
import com.cathaybk.demo.service.CCRC001Svc;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

/**
 * CABS-B-CCRC001 iContact聯繫單資料匯入
 *
 * @author 00550396
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CCRC001SvcImpl implements CCRC001Svc {

    /** com.cathaybk.demo.entity.CabChangeRequestRepository */
    private final CabChangeRequestRepository cabChangeRequestRepository;

    /** com.cathaybk.demo.entity.ReleaseActionLogRepository */
    private final ReleaseActionLogRepository releaseActionLogRepository;

    /** NormalResponseFactory */
    private final NormalResponseFactory normalResponseFactory;

    /** DateTimeFormatter */
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    @Override
    @Transactional
    public ResponseTemplate<EmptyTranrs> sync(RequestTemplate<CCRC001Tranrq> req)
            throws RestException {

        CCRC001Tranrq tranrq = req.getTranrq();

        // STEP1. 查詢是否已存在
        Optional<CabChangeRequestEntity> existingOpt = cabChangeRequestRepository.findById(tranrq.getEformId());
        boolean isUpdate = existingOpt.isPresent();

        // STEP2. UPSERT 寫入
        CabChangeRequestEntity entity = existingOpt.orElse(new CabChangeRequestEntity());
        
        // 設定欄位
        entity.setEformId(tranrq.getEformId());
        entity.setSysName(tranrq.getSysName());
        entity.setApid(tranrq.getApid());
        entity.setBiaLevelId(tranrq.getBiaLevelId());
        entity.setExecuteDate(parseDateTime(tranrq.getExecuteDate()));
        entity.setEndDate(parseDateTime(tranrq.getEndDate()));
        entity.setChangeDetails(tranrq.getChangeDetails());
        entity.setChangeTypeId(tranrq.getChangeTypeId());
        entity.setRiskAssessmentId(tranrq.getRiskAssessmentId());
        entity.setAttributesId(tranrq.getAttributesId());
        entity.setIsDowntimeRequires(tranrq.getIsDowntimeRequires());
        entity.setIsChangedb(tranrq.getIsChangedb());
        entity.setIsAnn(tranrq.getIsAnn());
        entity.setProgramPhone(tranrq.getProgramPhone());
        entity.setAffectedNotes(tranrq.getAffectedNotes());
        entity.setCreatedEmpname(tranrq.getCreatedEmpName());
        entity.setCreatedEmpid(tranrq.getCreatedEmpid());
        entity.setCreatedDate(parseDateTime(tranrq.getCreatedDate()));
        entity.setCreatedDept(tranrq.getCreatedDept());
        entity.setCreatedPhoneExt(tranrq.getCreatedPhoneExt());
        entity.setModifyDate(parseDateTime(tranrq.getModifyDate()));
        entity.setIsActive(StringUtils.defaultIfBlank(tranrq.getIsActive(), "Y"));
        entity.setHri(tranrq.getHri());
        entity.setReviewStatus(tranrq.getReviewStatus());
        entity.setReviewDate(parseDateTime(tranrq.getReviewDate()));
        entity.setReviewModifyEmpid(tranrq.getReviewModifyEmpid());
        entity.setReviewModifyTime(parseDateTime(tranrq.getReviewModifyTime()));
        entity.setIsAiCab(tranrq.getIsAiCab());
        entity.setAiCabEformId(tranrq.getAiCabEformId());

        cabChangeRequestRepository.save(entity);

        // STEP3. 若為 UPDATE，寫入 RELEASE_ACTION_LOG 並回待審核
        if (isUpdate) {
            // 取得前一次狀態
            String lastStatus = releaseActionLogRepository
                    .findFirstByEformIdOrderByActionAtDescIdDesc(tranrq.getEformId())
                    .map(ReleaseActionLogEntity::getAfterStatus)
                    .orElse("N");

            // 新增一筆 RELEASE_ACTION_LOG (RESEND)
            ReleaseActionLogEntity log = new ReleaseActionLogEntity();
            log.setEformId(tranrq.getEformId());
            log.setActionType("RESEND");
            log.setBeforeStatus(lastStatus);
            log.setAfterStatus("N"); // 固定清為待審核
            log.setActionBy("ICONTACT");
            log.setRequestSource("RESEND");
            log.setActionAt(LocalDateTime.now());
            log.setApprovedAt(null);
            log.setIcontactRefStatus(null);
            log.setIcontactRefMessage(null);

            releaseActionLogRepository.save(log);
        }

        // STEP4. 回傳下行
        return normalResponseFactory.genNormalResponse(new EmptyTranrs(), req);
    }

    /**
     * 解析日期時間字串
     *
     * @param dateTimeStr
     * @return
     */
    private LocalDateTime parseDateTime(String dateTimeStr) {
        if (StringUtils.isBlank(dateTimeStr)) {
            return null;
        }
        return LocalDateTime.parse(dateTimeStr, FORMATTER);
    }

}