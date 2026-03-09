package com.cathaybk.demo.service.impl;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.cathaybk.demo.dto.*;
import com.cathaybk.demo.factory.NormalResponseFactory;
import com.cathaybk.demo.service.ANNQ001Svc;
import com.cathaybk.demo.sql.SqlAction;
import com.cathaybk.demo.sql.SqlUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

/**
 * CABS-B-ANNQ001 查詢公告清單
 *
 * @author
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ANNQ001SvcImpl implements ANNQ001Svc {

    /** ANNQ001_QUERY_001 */
    public static final String ANNQ001_QUERY_001 = "ANNQ001_QUERY_001.sql";

    /** SqlUtils */
    private final SqlUtils sqlUtils;

    /** SqlAction */
    private final SqlAction sqlAction;

    /** NormalResponseFactory */
    private final NormalResponseFactory normalResponseFactory;

    @Override
    public ResponseTemplate<ANNQ001Tranrs> queryList(RequestTemplate<ANNQ001Tranrq> req)
            throws IOException {

        ANNQ001Tranrq tranrq = req.getTranrq();

        // STEP1: 計算 offset
        int offset = (tranrq.getPageNumber() - 1) * tranrq.getPageSize();

        // STEP2: 建立 SQL Action 參數
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("pageSize", tranrq.getPageSize());
        paramMap.put("offset", offset);
        paramMap.put("sortDirection", tranrq.getSortDirection() != null ? tranrq.getSortDirection() : "DESC");

        // STEP3: 執行 SQL 查詢
        List<Map<String, Object>> resultList = sqlAction.queryForList(
                sqlUtils.getDynamicQuerySql(ANNQ001_QUERY_001, paramMap), paramMap);

        // STEP4: 組裝下行電文
        ANNQ001Tranrs tranrs = new ANNQ001Tranrs();
        tranrs.setPageNumber(tranrq.getPageNumber());
        tranrs.setPageSize(tranrq.getPageSize());

        if (resultList == null || resultList.isEmpty()) {
            tranrs.setTotalElements(0L);
            tranrs.setTotalPages(0);
            tranrs.setItems(List.of());
        } else {
            // 取得總筆數
            Long totalElements = ((Number) resultList.get(0).get("TOTALELEMENTS")).longValue();
            tranrs.setTotalElements(totalElements);

            // 計算總頁數
            int totalPages = (int) ((totalElements + tranrq.getPageSize() - 1) / tranrq.getPageSize());
            tranrs.setTotalPages(totalPages);

            // 轉換資料項目
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            List<ANNQ001TranrsItem> items = resultList.stream()
                    .map(row -> {
                        ANNQ001TranrsItem item = new ANNQ001TranrsItem();
                        item.setId(((Number) row.get("ID")).longValue());
                        item.setContent((String) row.get("CONTENT"));
                        item.setCreatedBy((String) row.get("CREATED_BY"));
                        item.setCreatedAt(row.get("CREATED_AT") != null 
                                ? sdf.format(row.get("CREATED_AT")) : null);
                        item.setUpdatedBy((String) row.get("UPDATED_BY"));
                        item.setUpdatedAt(row.get("UPDATED_AT") != null 
                                ? sdf.format(row.get("UPDATED_AT")) : null);
                        return item;
                    })
                    .collect(Collectors.toList());

            tranrs.setItems(items);
        }

        return normalResponseFactory.genNormalResponse(tranrs, req);
    }

}