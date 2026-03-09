package com.cathaybk.demo.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cathaybk.demo.dto.*;
import com.cathaybk.demo.exception.RestException;
import com.cathaybk.demo.factory.NormalResponseFactory;
import com.cathaybk.demo.service.RLSQ001Svc;
import com.cathaybk.demo.sql.SqlAction;
import com.cathaybk.demo.sql.SqlUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

/**
 * CABS-B-RLSQ001 產生放行清單
 *
 * @author System
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RLSQ001SvcImpl implements RLSQ001Svc {

    /** RLSQ001_QUERY_001 */
    public static final String RLSQ001_QUERY_001 = "RLSQ001_QUERY_001.sql";

    /** SqlUtils */
    private final SqlUtils sqlUtils;

    /** SqlAction */
    private final SqlAction sqlAction;

    /** NormalResponseFactory */
    private final NormalResponseFactory normalResponseFactory;

    @Override
    public ResponseTemplate<RLSQ001Tranrs> preview(RequestTemplate<RLSQ001Tranrq> req)
            throws IOException, RestException {

        List<String> eformIds = req.getTranrq().getEformIds();

        // 檢查電文資料完整性
        validateEformIds(eformIds);

        // 去除重複值
        List<String> distinctEformIds = new ArrayList<>(new HashSet<>(eformIds));

        // 執行查詢
        Map<String, Object> queryMap = Map.of("eformIds", distinctEformIds);

        List<RLSQ001TranrsItem> items = sqlAction.queryForListVO(
                sqlUtils.getDynamicQuerySql(RLSQ001_QUERY_001, queryMap),
                queryMap,
                RLSQ001TranrsItem.class,
                false);

        return normalResponseFactory.genNormalResponse(new RLSQ001Tranrs(items), req);
    }

    /**
     * 檢查電文資料完整性
     *
     * @param eformIds
     * @throws RestException
     */
    private void validateEformIds(List<String> eformIds) throws RestException {
        if (eformIds == null || eformIds.isEmpty()) {
            throw new RestException("E102", "聯繫單號清單 不得為空");
        }

        if (eformIds.size() > 200) {
            throw new RestException("E102", "聯繫單號清單 數量不得超過200筆");
        }

        for (String eformId : eformIds) {
            String trimmedId = StringUtils.trim(eformId);
            if (StringUtils.isBlank(trimmedId)) {
                throw new RestException("E102", "聯繫單號 不得為空白");
            }
            if (trimmedId.length() != 17) {
                throw new RestException("E102", "聯繫單號長度必須為17：" + trimmedId);
            }
        }
    }
}
