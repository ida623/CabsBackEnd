package com.cathaybk.demo.service.impl;

import com.cathaybk.demo.dto.RLSC001BeforeStatusEntity;
import com.cathaybk.demo.dto.RLSC001Tranrq;
import com.cathaybk.demo.dto.RLSC001Tranrs;
import com.cathaybk.demo.entity.ReleaseActionLogEntity;
import com.cathaybk.demo.exception.RequestValidException;
import com.cathaybk.demo.repository.ReleaseActionLogRepo;
import com.cathaybk.demo.service.RLSC001Svc;
import com.cathaybk.demo.sql.SqlAction;
import com.cathaybk.demo.sql.SqlUtils;
import com.cathaybk.demo.user.UserObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * CABS-B-RLSC001 放行處理
 * @author System
 */
@RequiredArgsConstructor
@Service
public class RLSC001SvcImpl implements RLSC001Svc {

    public static final String RLSC001_QUERY_001 = "RLSC001_QUERY_001.sql";

    private final ReleaseActionLogRepo releaseActionLogRepo;
    private final UserObject userObject;
    private final SqlUtils sqlUtils;
    private final SqlAction sqlAction;
    private final IContactClient iContactClient;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RLSC001Tranrs processRelease(RLSC001Tranrq tranrq) throws IOException, RequestValidException {

        // 去重並驗證每筆 eformId
        List<String> eformIds = tranrq.getEformIds().stream()
                .map(String::trim)
                .distinct()
                .collect(Collectors.toList());

        // STEP1. 取得操作者與時間
        String actionBy = userObject.getEmpName();
        LocalDateTime actionAt = LocalDateTime.now();

        // STEP2. 查詢各 eformId 前次最新狀態
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("eformIds", eformIds);

        List<RLSC001BeforeStatusEntity> statusResultList = sqlAction.queryForListVO(
                sqlUtils.getDynamicQuerySql(RLSC001_QUERY_001, queryMap),
                queryMap, RLSC001BeforeStatusEntity.class, false);

        Map<String, String> lastStatusMap = statusResultList.stream()
                .collect(Collectors.toMap(
                        RLSC001BeforeStatusEntity::getEformId,
                        r -> r.getStatus() != null ? r.getStatus() : "N"));

        // STEP3. 計算 afterStatus
        String afterStatus = "PASS".equals(tranrq.getActionType()) ? "Y" : "N";

        // STEP4. 批次新增 RELEASE_ACTION_LOG
        LocalDateTime approvedAt = LocalDateTime.parse(
                tranrq.getApprovedAt() + " 00:00:00",
                DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));

        List<ReleaseActionLogEntity> entities = new ArrayList<>();
        for (String eformId : eformIds) {
            ReleaseActionLogEntity entity = new ReleaseActionLogEntity();
            entity.setEformId(eformId);
            entity.setActionType(tranrq.getActionType());
            entity.setRequestSource(tranrq.getRequestSource());
            entity.setBeforeStatus(lastStatusMap.getOrDefault(eformId, "N"));
            entity.setAfterStatus(afterStatus);
            entity.setActionBy(actionBy);
            entity.setActionAt(actionAt);
            entity.setApprovedAt(approvedAt);
            entity.setIContactRefStatus(null);
            entity.setIContactRefMessage(null);
            entities.add(entity);
        }

        List<ReleaseActionLogEntity> savedEntities = releaseActionLogRepo.saveAll(entities);

        // STEP5. 呼叫 iContact（DB commit 後執行，不放 transaction 內）
        String reviewStatus = "PASS".equals(tranrq.getActionType()) ? "Y" : "N";
        String reviewDate = tranrq.getApprovedAt().replace("/", "");

        CabsListO001Tranrq iContactRq = new CabsListO001Tranrq(reviewStatus, reviewDate, eformIds);
        CabsListO001Tranrs iContactRs;

        try {
            iContactRs = iContactClient.callCabsListO001(iContactRq);
        } catch (Exception e) {
            // iContact 呼叫失敗，仍回 0000，記錄失敗原因
            String errorMsg = e.getMessage();
            savedEntities.forEach(entity -> {
                entity.setIContactRefStatus("E999");
                entity.setIContactRefMessage(errorMsg);
            });
            releaseActionLogRepo.saveAll(savedEntities);

            return new RLSC001Tranrs("N", "E999", errorMsg);
        }

        // STEP6. 寫回 iContact 回傳結果
        boolean isSuccess = "0000".equals(iContactRs.getReturnCode());
        String refStatus = iContactRs.getReturnCode();
        String refMessage = isSuccess ? "交易成功" : iContactRs.getReturnDesc();

        savedEntities.forEach(entity -> {
            entity.setIContactRefStatus(refStatus);
            entity.setIContactRefMessage(refMessage);
        });
        releaseActionLogRepo.saveAll(savedEntities);

        return new RLSC001Tranrs(
                isSuccess ? "Y" : "N",
                iContactRs.getReturnCode(),
                refMessage);
    }

}