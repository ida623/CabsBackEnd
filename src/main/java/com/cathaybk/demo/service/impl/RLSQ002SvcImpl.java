package com.cathaybk.demo.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.cathaybk.demo.dto.RLSQ001TranrsItem;
import com.cathaybk.demo.dto.RLSQ002Tranrs;
import com.cathaybk.demo.dto.ResponseTemplate;
import com.cathaybk.demo.exception.RestException;
import com.cathaybk.demo.factory.NormalResponseFactory;
import com.cathaybk.demo.service.RLSQ002Svc;
import com.cathaybk.demo.sql.SqlAction;
import com.cathaybk.demo.sql.SqlUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

/**
 * CABS-B-RLSQ002 匯入EXCEL產生放行清單
 *
 * @author System
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RLSQ002SvcImpl implements RLSQ002Svc {

    /** RLSQ001_QUERY_001 */
    public static final String RLSQ001_QUERY_001 = "RLSQ001_QUERY_001.sql";

    /** SqlUtils */
    private final SqlUtils sqlUtils;

    /** SqlAction */
    private final SqlAction sqlAction;

    /** NormalResponseFactory */
    private final NormalResponseFactory normalResponseFactory;

    @Override
    public ResponseTemplate<RLSQ002Tranrs> importExcel(MultipartFile file)
            throws IOException, RestException {

        // 檢查檔案
        if (file == null || file.isEmpty()) {
            throw new RestException("E102", "檔案 不得為空");
        }

        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".xlsx")) {
            throw new RestException("E102", "檔案格式必須為 .xlsx");
        }

        // 解析 Excel 取得 eformIds
        List<String> eformIds = parseExcelEformIds(file);

        // 檢查資料
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

        return normalResponseFactory.genNormalResponse(new RLSQ002Tranrs(items), null);
    }

    /**
     * 解析 Excel 取得 eformIds
     *
     * @param file
     * @return
     * @throws IOException
     * @throws RestException
     */
    private List<String> parseExcelEformIds(MultipartFile file) throws IOException, RestException {
        List<String> eformIds = new ArrayList<>();

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            // 讀取第一個工作表
            Sheet sheet = workbook.getSheetAt(0);

            // 從第2列開始讀取（索引1）
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                // 讀取 A 欄
                Cell cell = row.getCell(0);
                if (cell == null) {
                    continue;
                }

                String eformId = getCellValueAsString(cell);
                if (StringUtils.isNotBlank(eformId)) {
                    eformIds.add(eformId.trim());
                }
            }
        } catch (Exception e) {
            throw new RestException("E999", "Excel檔案解析失敗：" + e.getMessage());
        }

        return eformIds;
    }

    /**
     * 取得儲存格值為字串
     *
     * @param cell
     * @return
     */
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    /**
     * 檢查電文資料完整性
     *
     * @param eformIds
     * @throws RestException
     */
    private void validateEformIds(List<String> eformIds) throws RestException {
        if (eformIds == null || eformIds.isEmpty()) {
            throw new RestException("E102", "Excel檔案中未找到任何聯繫單號");
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
