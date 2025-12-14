package com.cathaybk.demo.service.impl;

import com.cathaybk.demo.common.ReturnCodeAndDescEnum;
import com.cathaybk.demo.dto.*;
import com.cathaybk.demo.exception.DataNotFoundException;
import com.cathaybk.demo.service.CifQ002Svc;
import com.cathaybk.demo.sql.SqlAction;
import com.cathaybk.demo.sql.SqlUtils;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * XXA-C-CIFQ002 條件過濾查詢客戶（含動態排序）
 * 提供多條件查詢、分頁及動態排序功能
 *
 * 功能特點：
 * - 支援多欄位條件組合查詢（身份證、姓名、性別、教育程度、手機、Email、年度）
 * - 支援分頁查詢
 * - 支援動態排序（可指定排序欄位及排序方向）
 * - 使用動態 SQL 技術，只將有值的參數加入查詢條件
 */
@Service
@RequiredArgsConstructor
public class CifQ002SvcImpl implements CifQ002Svc {

    /**
     * 查詢客戶筆數的 SQL 檔案名稱
     * 用於計算符合條件的總筆數
     */
    private static final String COUNT_SQL_FILE = "CIFQ002_COUNT.sql";

    /**
     * 查詢客戶列表的 SQL 檔案名稱
     * 用於取得分頁後的客戶資料清單
     */
    private static final String QUERY_SQL_FILE = "CIFQ002_QUERY.sql";

    @Autowired
    private final SqlAction sqlAction;  // SQL 執行工具，負責執行動態 SQL

    @Autowired
    private final SqlUtils sqlUtils;    // SQL 工具類，負責產生動態 SQL 語句

    @Autowired
    private final EntityManager em;     // JPA EntityManager，用於資料庫操作

    /**
     * 條件過濾查詢客戶資料（含分頁及動態排序）
     *
     * 執行流程：
     * 1. 解析查詢條件，建立動態 SQL 參數
     * 2. 查詢符合條件的總筆數
     * 3. 計算分頁資訊（總頁數、offset）
     * 4. 執行分頁查詢，取得客戶資料清單
     * 5. 封裝回應資料
     *
     * @param request 包含查詢條件、分頁資訊、排序資訊的請求物件
     * @return 包含客戶清單及分頁資訊的回應物件
     * @throws DataNotFoundException 當查無符合條件的資料時拋出
     * @throws IOException IO例外
     */
    @Override
    @Transactional(rollbackOn = Exception.class)
    public ResponseTemplate<CIFQ002Tranrs> filter(RequestTemplate<CIFQ002Tranrq> request) throws DataNotFoundException, IOException {

        // 從 request 物件中取得查詢條件
        CIFQ002Tranrq tranrq = request.getTranrq();
        CIFQ002TranrqData searchData = tranrq.getData();

        // 建立動態 SQL 參數 Map - 只放有值的參數
        // 這樣可以避免空值參數影響查詢效能，實現真正的動態查詢
        HashMap<String, Object> params = new HashMap<>();

        if (searchData != null) {
            // 只有當欄位有值時才放入 map，實現條件的動態組合
            if (StringUtils.isNotBlank(searchData.getIdNum())) {
                params.put("idNum", searchData.getIdNum());  // 身份證字號（模糊查詢）
            }
            if (StringUtils.isNotBlank(searchData.getChineseName())) {
                params.put("chineseName", searchData.getChineseName());  // 中文姓名（模糊查詢）
            }
            if (StringUtils.isNotBlank(searchData.getGender())) {
                params.put("gender", searchData.getGender());  // 性別（模糊查詢）
            }
            if (StringUtils.isNotBlank(searchData.getEducation())) {
                params.put("education", searchData.getEducation());  // 教育程度（模糊查詢）
            }
            if (StringUtils.isNotBlank(searchData.getMobile())) {
                params.put("mobile", searchData.getMobile());  // 手機號碼（模糊查詢）
            }
            if (StringUtils.isNotBlank(searchData.getEmail())) {
                params.put("email", searchData.getEmail());  // 電子郵件（模糊查詢）
            }
            if (searchData.getYear() != null) {
                params.put("year", searchData.getYear());  // 年度（精確查詢）
            }
        }

        // 第一步：取得符合條件的總筆數
        // 使用 SqlUtils 根據參數動態產生 COUNT SQL
        String countSql = sqlUtils.getDynamicQuerySQL(COUNT_SQL_FILE, params);
        List<Map<String, Object>> countResult = sqlAction.queryForList(em, countSql, params);
        int totalCount = ((Number) countResult.get(0).get("TOTAL_COUNT")).intValue();

        // 若查無資料，拋出 DataNotFoundException
        if (totalCount == 0) {
            throw new DataNotFoundException("查無資料");
        }

        // 第二步：計算分頁資訊
        int pageNumber = tranrq.getPage().getPageNumber();  // 目前頁碼（從 1 開始）
        int pageSize = tranrq.getPage().getPageSize();      // 每頁筆數

        // 使用 BigDecimal 計算總頁數，避免整數除法精度問題
        // 使用 CEILING 無條件進位，確保最後一頁的資料也能顯示
        BigDecimal totalCountBD = new BigDecimal(totalCount);
        BigDecimal pageSizeBD = new BigDecimal(pageSize);
        int totalPage = totalCountBD.divide(pageSizeBD, 0, RoundingMode.CEILING).intValue();

        // 計算 offset：從哪一筆資料開始取（跳過前面頁數的資料筆數）
        // 例如：第 3 頁，每頁 10 筆 → offset = (3-1) * 10 = 20（跳過前 20 筆）
        int offset = (pageNumber - 1) * pageSize;

        // 將分頁參數加入 params
        params.put("pageSize", pageSize);
        params.put("offset", offset);

        // 第三步：建立動態排序字串
        String[] orderBy = buildOrderBy(tranrq.getSortInfo());

        // 第四步：執行分頁查詢，取得客戶資料清單
        // 使用 SqlUtils 根據參數和排序條件動態產生 QUERY SQL
        String querySql = sqlUtils.getDynamicQuerySQL(QUERY_SQL_FILE, params, orderBy);
        // 執行查詢並將結果自動對應到 CIFQ002TranrsItems 物件
        List<CIFQ002TranrsItems> items = sqlAction.queryForListVO(em, querySql, params, CIFQ002TranrsItems.class, true);

        // 第五步：建立 TRANRS 回應資料物件
        CIFQ002Tranrs tranrs = new CIFQ002Tranrs();
        tranrs.setPageNumber(pageNumber);    // 目前頁碼
        tranrs.setPageSize(pageSize);        // 每頁筆數
        tranrs.setTotalPage(totalPage);      // 總頁數
        tranrs.setTotalCount(totalCount);    // 總筆數
        tranrs.setItems(items);              // 客戶資料清單

        // 第六步：建立 ResponseTemplate 並設定表頭資訊
        ResponseTemplate<CIFQ002Tranrs> response = new ResponseTemplate<>();
        HEADER header = new HEADER();
        header.setMsgid(request.getMwheader().getMsgid());                        // 訊息編號
        header.setReturncode(ReturnCodeAndDescEnum.SUCCESS.getCode());            // 回應代碼：0000(成功)
        header.setReturndesc(ReturnCodeAndDescEnum.SUCCESS.getDesc());            // 回應描述：交易成功
        response.setMwheader(header);
        response.setTranrs(tranrs);

        return response;
    }

    /**
     * 建立動態排序字串
     *
     * 功能說明：
     * - 根據前端傳入的排序資訊，產生 SQL ORDER BY 子句
     * - 若未指定排序欄位，預設以 ORDER_ID 倒序排序
     * - 若未指定排序方向，預設為 DESC（倒序）
     *
     * @param sortInfo 排序資訊物件（包含 sortColumn 和 sortBy）
     * @return 排序字串陣列，格式：["欄位名稱 排序方向"]
     */
    private String[] buildOrderBy(SortInfo sortInfo) {
        // sortInfo 必填，不會是 null
        String sortColumn = sortInfo.getSortColumn();  // 排序欄位名稱
        String sortBy = sortInfo.getSortBy();          // 排序方向（ASC/DESC）

        // 預設排序規則：若未指定排序欄位，以 ORDER_ID 倒序排序
        // 這樣可以確保新增的資料排在前面
        if (StringUtils.isBlank(sortColumn)) {
            sortColumn = "ORDER_ID";
        }

        // 預設排序方向：若未指定，使用 DESC（倒序）
        if (StringUtils.isBlank(sortBy)) {
            sortBy = "DESC";
        }

        // 回傳格式：欄位名稱 + 空格 + 排序方向（轉大寫）
        // 例如：["ORDER_ID DESC"] 或 ["CHINESE_NAME ASC"]
        return new String[]{sortColumn + " " + sortBy.toUpperCase()};
    }
}