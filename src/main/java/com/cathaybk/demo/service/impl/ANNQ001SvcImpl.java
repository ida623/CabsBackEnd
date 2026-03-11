package com.cathaybk.demo.service.impl;

import com.cathaybk.demo.dto.ANNQ001TranrsEntity;
import com.cathaybk.demo.dto.ANNQ001Tranrq;
import com.cathaybk.demo.dto.ANNQ001Tranrs;
import com.cathaybk.demo.dto.ANNQ001TranrsItem;
import com.cathaybk.demo.service.ANNQ001Svc;
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
 * CABS-B-ANNQ001 查詢公告清單
 * @author
 */
@RequiredArgsConstructor
@Service
public class ANNQ001SvcImpl implements ANNQ001Svc {

    /** ANNQ001_QUERY_001 */
    public static final String ANNQ001_QUERY_001 = "ANNQ001_QUERY_001.sql";

    /** SqlUtils */
    private final SqlUtils sqlUtils;

    /** SqlAction */
    private final SqlAction sqlAction;

    /**
     * CABS-B-ANNQ001 查詢公告清單
     */
    @Override
    public ANNQ001Tranrs queryAnnouncementList(ANNQ001Tranrq tranrq) throws IOException {

        int offset = (tranrq.getPageNumber() - 1) * tranrq.getPageSize();

        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("pageSize", tranrq.getPageSize());
        queryMap.put("offset", offset);
        queryMap.put("sortDirection", tranrq.getSortDirection());

        List<ANNQ001TranrsEntity> resultList = sqlAction.queryForListVO(
                sqlUtils.getDynamicQuerySql(ANNQ001_QUERY_001, queryMap),
                queryMap, ANNQ001TranrsEntity.class, false);

        if (resultList == null || resultList.isEmpty()) {
            return new ANNQ001Tranrs(
                    tranrq.getPageNumber(),
                    tranrq.getPageSize(),
                    0L,
                    0,
                    Collections.emptyList());
        }

        long totalElements = resultList.get(0).getTotalElements();
        int totalPages = (int) ((totalElements + tranrq.getPageSize() - 1) / tranrq.getPageSize());

        List<ANNQ001TranrsItem> items = resultList.stream()
                .map(r -> new ANNQ001TranrsItem(
                        r.getId(),
                        r.getContent(),
                        r.getCreatedBy(),
                        r.getCreatedAt(),
                        r.getUpdatedBy(),
                        r.getUpdatedAt()))
                .collect(Collectors.toList());

        return new ANNQ001Tranrs(
                tranrq.getPageNumber(),
                tranrq.getPageSize(),
                totalElements,
                totalPages,
                items);
    }

}