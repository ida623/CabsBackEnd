package com.cathaybk.demo.service.impl;

import com.cathaybk.demo.dto.RLSQ002Tranrs;
import com.cathaybk.demo.dto.RLSQueryEntity;
import com.cathaybk.demo.exception.RequestValidException;
import com.cathaybk.demo.service.RLSQ002Svc;
import com.cathaybk.demo.sql.SqlAction;
import com.cathaybk.demo.sql.SqlUtils;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * CABS-B-RLSQ002 匯入EXCEL產生放行清單
 * @author System
 */
@RequiredArgsConstructor
@Service
public class RLSQ002SvcImpl implements RLSQ002Svc {

    public static final String RLSQ002_QUERY_001 = "RLSQ001_QUERY_001.sql";

    private final SqlUtils sqlUtils;
    private final SqlAction sqlAction;

    @Override
    public RLSQ002Tranrs importExcelAndPreview(MultipartFile file)
            throws IOException, RequestValidException {

        // STEP1. 解析 Excel 取得 eformIds
        List<String> eformIds = parseEformIdsFromExcel(file);

        if (eformIds.isEmpty()) {
            return new RLSQ002Tranrs(Collections.emptyList());
        }

        // STEP2. 執行 SQL 查詢
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("eformIds", eformIds);

        List<RLSQueryEntity> resultList = sqlAction.queryForListVO(
                sqlUtils.getDynamicQuerySql(RLSQ002_QUERY_001, queryMap),
                queryMap, RLSQueryEntity.class, false);

        if (resultList == null || resultList.isEmpty()) {
            return new RLSQ002Tranrs(Collections.emptyList());
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

        return new RLSQ002Tranrs(items);
    }

    /**
     * 讀取 Excel A欄第2列起的聯繫單號
     */
    private List<String> parseEformIdsFromExcel(MultipartFile file) throws IOException {

        List<String> eformIds = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            // 從第2列（index=1）開始讀取 A 欄
            for (int rowIdx = 1; rowIdx <= sheet.getLastRowNum(); rowIdx++) {
                Row row = sheet.getRow(rowIdx);
                if (row == null) {
                    continue;
                }
                Cell cell = row.getCell(0);
                if (cell == null) {
                    continue;
                }
                String eformId = cell.toString().trim();
                if (!eformId.isBlank()) {
                    eformIds.add(eformId);
                }
            }
        }

        // 去除重複值
        return eformIds.stream().distinct().collect(Collectors.toList());
    }

}