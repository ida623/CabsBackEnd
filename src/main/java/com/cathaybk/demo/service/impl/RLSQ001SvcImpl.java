package com.cathaybk.demo.service.impl;

import com.cathaybk.demo.dto.RLSQ001Tranrq;
import com.cathaybk.demo.dto.RLSQ001Tranrs;
import com.cathaybk.demo.dto.RLSQueryEntity;
import com.cathaybk.demo.service.RLSQ001Svc;
import com.cathaybk.demo.sql.SqlAction;
import com.cathaybk.demo.sql.SqlUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * CABS-B-RLSQ001 產生放行清單
 * @author System
 */
@RequiredArgsConstructor
@Service
public class RLSQ001SvcImpl implements RLSQ001Svc {

    public static final String RLSQ001_QUERY_001 = "RLSQ001_QUERY_001.sql";

    private final SqlUtils sqlUtils;
    private final SqlAction sqlAction;

    @Override
    public RLSQ001Tranrs previewReleaseList(RLSQ001Tranrq tranrq) throws IOException {

        List<String> eformIds = tranrq.getEformIds().stream()
                .map(String::trim)
                .distinct()
                .collect(Collectors.toList());

        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("eformIds", eformIds);

        List<RLSQueryEntity> resultList = sqlAction.queryForListVO(
                sqlUtils.getDynamicQuerySql(RLSQ001_QUERY_001, queryMap),
                queryMap, RLSQueryEntity.class, false);

        if (resultList == null || resultList.isEmpty()) {
            return new RLSQ001Tranrs(Collections.emptyList());
        }

        List<RLSQTranrsItem> items = resultList.stream()
                .map(r -> new RLSQTranrsItem(
                        r.getEformId(),
                        r.getExecuteDate(),
                        r.getEndDate(),
                        r.getCreatedEmpid(),
                        r.getCreatedEmpName(),
                        r.getCreatedDept(),
                        r.getStatus(),
                        r.getChangeDetails(),
                        r.getApid(),
                        r.getSysName()))
                .collect(Collectors.toList());

        return new RLSQ001Tranrs(items);
    }

}