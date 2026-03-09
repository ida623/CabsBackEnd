package com.cathaybk.demo.service.impl;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cathaybk.demo.dto.RLSC001Tranrq;
import com.cathaybk.demo.dto.RLSC001Tranrs;
import com.cathaybk.demo.dto.RequestTemplate;
import com.cathaybk.demo.dto.ResponseTemplate;
import com.cathaybk.demo.entity.ReleaseActionLogEntity;
import com.cathaybk.demo.exception.RestException;
import com.cathaybk.demo.factory.NormalResponseFactory;
import com.cathaybk.demo.repository.ReleaseActionLogRepository;
import com.cathaybk.demo.service.RLSC001Svc;
import com.cathaybk.demo.sql.SqlAction;
import com.cathaybk.demo.sql.SqlUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

/**
 * CABS-B-RLSC001 放行處理
 *
 * @author System
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RLSC001SvcImpl implements RLSC001Svc {

    /** RLSC001_QUERY_001 */
    public static final String RLSC001_QUERY_001 = "RLSC001_QUERY_001.sql";

    /** SqlUtils */
    private final SqlUtils sqlUtils;

    /** SqlAction */
    private final SqlAction sqlAction;

    /** NormalResponseFactory */
    private final NormalResponseFactory normalResponseFactory;

    /** ReleaseActionLogRepository */
    private final ReleaseActionLogRepository releaseActionLogRepository;

    @Override
    @Transactional
    public ResponseTemplate<RLSC001Tranrs> actions(RequestTemplate<RLSC001Tranrq> req)
            throws IOException, RestException {

        RLSC001Tranrq tranrq = req.getTranrq();

        // 檢查電文資料完整性
        validateRequest(tranrq);

        // 去除重複值
        List<String> distinctEformIds = new ArrayList<>(new HashSet<>(tranrq.getEformIds()));

        // STEP1: 取得操作者與時間
        String actionBy = getActionBy(); // 從登入資訊取得
        Timestamp actionAt = new Timestamp(System.currentTimeMillis());

        // STEP2: 查詢所有 eformId 的前次最新狀態
        Map<String, String> lastStatusMap = getLastStatusMap(distinctEformIds);

        // STEP3: 計算 afterStatus
        String afterStatus = calculateAfterStatus(tranrq.getActionType());

        // STEP4: 批次新增 RELEASE_ACTION_LOG
        List<ReleaseActionLogEntity> entities = new ArrayList<>();
        for (String eformId : distinctEformIds) {
            ReleaseActionLogEntity entity = new ReleaseActionLogEntity();
            entity.setEformId(eformId);
            entity.setActionType(tranrq.getActionType());
            entity.setRequestSource(tranrq.getRequestSource());
            entity.setBeforeStatus(lastStatusMap.getOrDefault(eformId, "N"));
            entity.setAfterStatus(afterStatus);
            entity.setActionBy(actionBy);
            entity.setActionAt(actionAt);
            entity.setApprovedAt(tranrq.getApprovedAt());
            entity.setIContactRefStatus(null);
            entity.setIContactRefMessage(null);

            entities.add(entity);
        }

        // 儲存到資料庫
        List<ReleaseActionLogEntity> savedEntities = releaseActionLogRepository.saveAll(entities);

        // STEP5: 呼叫 iContact 回寫
        RLSC001Tranrs tranrs = callIContact(tranrq, savedEntities);

        // STEP6: 更新 iContact 回寫結果
        updateIContactRefStatus(savedEntities, tranrs);

        return normalResponseFactory.genNormalResponse(tranrs, req);
    }

    /**
     * 檢查電文資料完整性
     *
     * @param tranrq
     * @throws RestException
     */
    private void validateRequest(RLSC001Tranrq tranrq) throws RestException {
        List<String> eformIds = tranrq.getEformIds();

        if (eformIds == null || eformIds.isEmpty()) {
            throw new RestException("E102", "聯繫單號清單 不得為空");
        }

        // 檢查重複
        Set<String> uniqueIds = new HashSet<>();
        for (String eformId : eformIds) {
            String trimmedId = StringUtils.trim(eformId);

            if (StringUtils.isBlank(trimmedId)) {
                throw new RestException("E102", "聯繫單號 不得為空白");
            }

            if (trimmedId.length() != 17) {
                throw new RestException("E102", "聯繫單號長度必須為17：" + trimmedId);
            }

            if (!uniqueIds.add(trimmedId)) {
                throw new RestException("E102", "聯繫單號重複：" + trimmedId);
            }
        }
    }

    /**
     * 取得操作者
     *
     * @return
     */
    private String getActionBy() {
        // TODO: 從登入資訊/Session/Security Context 取得操作者姓名
        // 範例：SecurityContextHolder.getContext().getAuthentication().getName()
        return "SYSTEM"; // 預設值
    }

    /**
     * 查詢所有 eformId 的前次最新狀態
     *
     * @param eformIds
     * @return
     * @throws IOException
     */
    private Map<String, String> getLastStatusMap(List<String> eformIds) throws IOException {
        Map<String, Object> queryMap = Map.of("eformIds", eformIds);

        List<Map<String, Object>> resultList = sqlAction.queryForListMap(
                sqlUtils.getDynamicQuerySql(RLSC001_QUERY_001, queryMap),
                queryMap,
                false);

        Map<String, String> lastStatusMap = new HashMap<>();
        for (Map<String, Object> row : resultList) {
            String eformId = (String) row.get("EFORM_ID");
            String status = (String) row.get("STATUS");
            lastStatusMap.put(eformId, status != null ? status : "N");
        }

        return lastStatusMap;
    }

    /**
     * 計算 afterStatus
     *
     * @param actionType
     * @return
     */
    private String calculateAfterStatus(String actionType) {
        if ("PASS".equals(actionType)) {
            return "Y";
        } else if ("REJECT".equals(actionType)) {
            return "N";
        }
        return "N";
    }

    /**
     * 呼叫 iContact 回寫
     *
     * @param tranrq
     * @param savedEntities
     * @return
     */
    private RLSC001Tranrs callIContact(RLSC001Tranrq tranrq, List<ReleaseActionLogEntity> savedEntities) {
        RLSC001Tranrs tranrs = new RLSC001Tranrs();

        try {
            // 轉換參數
            String reviewStatus = "PASS".equals(tranrq.getActionType()) ? "Y" : "N";
            String reviewDate = convertToYyyyMMdd(tranrq.getApprovedAt());
            List<String> eformList = tranrq.getEformIds();

            // TODO: 實際呼叫 iContact API (CFS-C-CABSLISTO001)
            // 範例：
            // IContactRequest iContactReq = new IContactRequest();
            // iContactReq.setReviewStatus(reviewStatus);
            // iContactReq.setReviewDate(reviewDate);
            // iContactReq.setEformList(eformList);
            // IContactResponse iContactResp = iContactClient.call(iContactReq);

            // 模擬成功回應
            tranrs.setIContactCallSuccess("Y");
            tranrs.setIContactReturnCode("0000");
            tranrs.setIContactReturnDesc("交易成功");

        } catch (Exception e) {
            // iContact 失敗
            tranrs.setIContactCallSuccess("N");
            tranrs.setIContactReturnCode("E999");
            tranrs.setIContactReturnDesc(e.getMessage());
        }

        return tranrs;
    }

    /**
     * 更新 iContact 回寫結果到資料庫
     *
     * @param entities
     * @param tranrs
     */
    private void updateIContactRefStatus(List<ReleaseActionLogEntity> entities, RLSC001Tranrs tranrs) {
        for (ReleaseActionLogEntity entity : entities) {
            if ("Y".equals(tranrs.getIContactCallSuccess())) {
                entity.setIContactRefStatus("0000");
                entity.setIContactRefMessage("交易成功");
            } else {
                entity.setIContactRefStatus(tranrs.getIContactReturnCode());
                entity.setIContactRefMessage(tranrs.getIContactReturnDesc());
            }
        }

        releaseActionLogRepository.saveAll(entities);
    }

    /**
     * 轉換日期格式為 yyyyMMdd
     *
     * @param approvedAt
     * @return
     */
    private String convertToYyyyMMdd(String approvedAt) {
        if (approvedAt == null) {
            return "";
        }

        // 假設 approvedAt 格式為 yyyy/MM/dd
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy/MM/dd");
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyyMMdd");
            return outputFormat.format(inputFormat.parse(approvedAt));
        } catch (Exception e) {
            // 如果已經是 yyyyMMdd 格式，直接返回
            return approvedAt.replace("/", "").replace("-", "");
        }
    }
}
