package com.cathaybk.demo.service.impl;

import com.cathaybk.demo.common.ReturnCodeAndDescEnum;
import com.cathaybk.demo.dto.*;
import com.cathaybk.demo.exception.DataNotFoundException;
import com.cathaybk.demo.service.CIFQ002Svc;
import com.cathaybk.demo.sql.SqlAction;
import com.cathaybk.demo.sql.SqlUtils;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;

/**
 * XXA-C-CIFQ002 查詢客戶列表
 */
@Service
@RequiredArgsConstructor // TODO 統一 RequiredArgsConstructor 取代 @Autowired
public class CIFQ002SvcImpl implements CIFQ002Svc {

    // Note 命名 CIFQ002_QUERY_001
    private static final String QUERY_SQL_FILE = "CIFQ002_QUERY.sql";

    private final SqlAction sqlAction;
    private final SqlUtils sqlUtils;
    private final EntityManager em;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public ResponseTemplate<CIFQ002Tranrs> filter(RequestTemplate<CIFQ002Tranrq> request) throws DataNotFoundException, IOException {

        // 取得請求
        CIFQ002Tranrq tranrq = request.getTranrq();
        CIFQ002TranrqData searchData = tranrq.getData();

        // 建立動態 Map，欄位有值才放入
        HashMap<String, Object> params = new HashMap<>();

        // Note > ObjectUtils.isNotEmpty(searchData)
        if (searchData != null) {
            // TODO 建議於impl拼接字串
            // params.put("chineseName", String.format("%%%s%%", searchData.getChineseName()));
            // params.put("chineseName", String.join("", "%", searchData.getChineseName(), "%"));
            if (StringUtils.isNotBlank(searchData.getIdNum())) {
                params.put("idNum", String.join("", "%", searchData.getIdNum(), "%"));
            }
            if (StringUtils.isNotBlank(searchData.getChineseName())) {
                params.put("chineseName", String.join("", "%", searchData.getChineseName(), "%"));
            }
            if (StringUtils.isNotBlank(searchData.getGender())) {
                params.put("gender", String.join("", "%", searchData.getGender(), "%"));
            }
            if (StringUtils.isNotBlank(searchData.getEducation())) {
                params.put("education", String.join("", "%", searchData.getEducation(), "%"));
            }
            if (StringUtils.isNotBlank(searchData.getMobile())) {
                params.put("mobile", String.join("", "%", searchData.getMobile(), "%"));
            }
            if (StringUtils.isNotBlank(searchData.getEmail())) {
                params.put("email", String.join("", "%", searchData.getEmail(), "%"));
            }
            if (searchData.getYear() != null) {
                params.put("year", searchData.getYear());
            }
        }

        int pageNumber = tranrq.getPage().getPageNumber();  // 目前頁碼
        int pageSize = tranrq.getPage().getPageSize();      // 每頁筆數

        // 計算 offset：從哪一筆資料開始取，要跳過前面的資料筆數）
        int offset = (pageNumber - 1) * pageSize;

        // 加入分頁參數
        params.put("pageSize", pageSize);
        params.put("offset", offset);

        // 建立動態排序字串
        String[] orderBy = buildOrderBy(tranrq.getSortInfo());

        // TODO 只需要 CIFQ002_QUERY.sql 即可
        // 執行查詢，對應到 CIFQ002TranrsItems
        String querySql = sqlUtils.getDynamicQuerySQL(QUERY_SQL_FILE, params, orderBy);
        List<CIFQ002TranrsItems> items = sqlAction.queryForListVO(em, querySql, params, CIFQ002TranrsItems.class, true);

        // TODO 善用 CollectionUtils.isNotEmpty(items) 判斷查無資料 throw new DataNotFoundException("查無資料");
        if (CollectionUtils.isEmpty(items)) {
            throw new DataNotFoundException("查無資料");
        }

        // 取得資料總筆數（從查詢結果計算）
        int totalCount = items.size();

        // 使用 BigDecimal 計算總頁數，無條件進位
        BigDecimal totalCountBD = new BigDecimal(totalCount);
        BigDecimal pageSizeBD = new BigDecimal(pageSize);
        int totalPage = totalCountBD.divide(pageSizeBD, 0, RoundingMode.CEILING).intValue();

        // 建立 TRANRS
        CIFQ002Tranrs tranrs = new CIFQ002Tranrs();
        tranrs.setPageNumber(pageNumber);
        tranrs.setPageSize(pageSize);
        tranrs.setTotalPage(totalPage);
        tranrs.setTotalCount(totalCount);
        tranrs.setItems(items);

        // TODO 善用 @AllArgsConstructor , @NoArgsConstructor
        // 建立回應，設定 HEADER
        ResponseTemplate<CIFQ002Tranrs> response = new ResponseTemplate<>();
        HEADER header = new HEADER(
                request.getMwheader().getMsgid(),
                ReturnCodeAndDescEnum.SUCCESS.getCode(),
                ReturnCodeAndDescEnum.SUCCESS.getDesc()
        );
        response.setMwheader(header);
        response.setTranrs(tranrs);

        return response;
    }

    // 建立動態排序字串
    private String[] buildOrderBy(SortInfo sortInfo) {

        String sortColumn = sortInfo.getSortColumn();  // 排序欄位
        String sortBy = sortInfo.getSortBy();          // 排序方式

        // 預設排序規則：ORDER_ID 倒序排序
        if (StringUtils.isBlank(sortColumn)) {
            sortColumn = "ORDER_ID";
        }

        if (StringUtils.isBlank(sortBy)) {
            sortBy = "DESC";
        }

        // 回傳格式：欄位名稱 + 排序方向
        return new String[]{sortColumn + " " + sortBy};
    }
}