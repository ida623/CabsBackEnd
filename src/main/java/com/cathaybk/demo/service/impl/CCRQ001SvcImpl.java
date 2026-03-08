package com.cathaybk.demo.service.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cathaybk.demo.dto.*;
import com.cathaybk.demo.exception.RestException;
import com.cathaybk.demo.factory.NormalResponseFactory;
import com.cathaybk.demo.service.CCRQ001Svc;
import com.cathaybk.demo.sql.SqlAction;
import com.cathaybk.demo.sql.SqlUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

/**
 * CABS-B-CCRQ001 查詢聯繫單列表
 *
 * @author 00550396
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CCRQ001SvcImpl implements CCRQ001Svc {

    /** CCRQ001_QUERY_001 */
    public static final String CCRQ001_QUERY_001 = "CCRQ001_QUERY_001.sql";

    /** SqlUtils */
    private final SqlUtils sqlUtils;

    /** SqlAction */
    private final SqlAction sqlAction;

    /** NormalResponseFactory */
    private final NormalResponseFactory normalResponseFactory;

    @Override
    public ResponseTemplate<CCRQ001Tranrs> queryList(RequestTemplate<CCRQ001Tranrq> req)
            throws IOException, RestException {

        CCRQ001Tranrq tranrq = req.getTranrq();

        // STEP1. 建立SQL Action參數
        Map<String, Object> queryMap = buildQueryMap(tranrq);

        // STEP2. 執行SQL查詢
        List<CCRQ001TranrsItem> items = sqlAction.queryForListVO(
                sqlUtils.getDynamicQuerySql(CCRQ001_QUERY_001, queryMap),
                queryMap,
                CCRQ001TranrsItem.class,
                false);

        // STEP3. 回傳下行
        return normalResponseFactory.genNormalResponse(new CCRQ001Tranrs(items), req);
    }

    /**
     * 建立查詢參數Map（僅放入非空值，對應SQL中的動態條件）
     *
     * @param tranrq
     * @return
     */
    private Map<String, Object> buildQueryMap(CCRQ001Tranrq tranrq) {
        Map<String, Object> queryMap = new HashMap<>();

        if (StringUtils.isNotBlank(tranrq.getEformId())) {
            queryMap.put("eformId", tranrq.getEformId());
        }
        if (StringUtils.isNotBlank(tranrq.getBiaLevelId())) {
            queryMap.put("biaLevelId", tranrq.getBiaLevelId());
        }
        if (StringUtils.isNotBlank(tranrq.getExecuteDate())) {
            queryMap.put("executeDate", tranrq.getExecuteDate());
        }
        if (StringUtils.isNotBlank(tranrq.getEndDate())) {
            queryMap.put("endDate", tranrq.getEndDate());
        }
        if (StringUtils.isNotBlank(tranrq.getChangeTypeId())) {
            queryMap.put("changeTypeId", tranrq.getChangeTypeId());
        }
        if (StringUtils.isNotBlank(tranrq.getAttributesId())) {
            queryMap.put("attributesId", tranrq.getAttributesId());
        }
        if (StringUtils.isNotBlank(tranrq.getCreatedEmpName())) {
            queryMap.put("createdEmpName", tranrq.getCreatedEmpName());
        }
        if (StringUtils.isNotBlank(tranrq.getCreatedDept())) {
            queryMap.put("createdDept", tranrq.getCreatedDept());
        }

        return queryMap;
    }

}