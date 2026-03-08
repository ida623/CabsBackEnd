package com.cathaybk.demo.service.impl;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.cathaybk.demo.dto.CCRE001Tranrq;
import com.cathaybk.demo.dto.CCRQ001TranrsItem;
import com.cathaybk.demo.dto.RequestTemplate;
import com.cathaybk.demo.exception.RestException;
import com.cathaybk.demo.service.CCRE001Svc;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

/**
 * CABS-B-CCRE001 匯出聯繫單清單
 *
 * @author 00550396
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CCRE001SvcImpl implements CCRE001Svc {

    /** ObjectMapper */
    private final ObjectMapper objectMapper;

    /** DateTimeFormatter for Excel */
    private static final DateTimeFormatter EXCEL_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");

    @Override
    public byte[] exportExcel(RequestTemplate<CCRE001Tranrq> req) throws RestException {
        try {
            List<CCRQ001TranrsItem> items = req.getTranrq().getItems();

            // STEP1. 產生 Excel
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Export");

            // STEP2. 第一列輸出表頭
            createHeaderRow(sheet);

            // STEP3. 逐筆輸出資料列
            int rowNum = 1;
            for (CCRQ001TranrsItem item : items) {
                createDataRow(sheet, rowNum++, item);
            }

            // 輸出為 byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();

            return outputStream.toByteArray();

        } catch (Exception e) {
            throw new RestException("E999", "Failed to export Excel: " + e.getMessage());
        }
    }

    /**
     * 建立表頭列
     *
     * @param sheet
     */
    private void createHeaderRow(Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        String[] headers = {
            "承辦單位", "承辦人", "聯繫單號", "系統名稱", "APID",
            "變更執行開始時間", "變更執行結束時間", "變更內容", "BIA等級", "工作項目性質",
            "變更種類", "需要中斷或重啟服務", "需要公告", "風險評估表", "受影響範圍",
            "承製日期", "修改時間",
            "有關帳務之變更", "影響客戶基本資料", "影響銀行對外提供之資料",
            "安全認證機制", "資料庫異動", "影響本行對外網頁/ATM/語音功能",
            "伺服器及網路等設備重要參數設定調整或增加硬體資源", "關聯式資料庫是否有使用大型物件型別",
            "資料異動欄位是否進倉儲", "涉及上下游系統間電文異動", "屬於高風險資訊系統或基礎設施",
            "完成UAT並獲得user signoff", "變更系統涉及AI", "AI CAB聯繫單號", "是否啟用"
        };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }
    }

    /**
     * 建立資料列
     *
     * @param sheet
     * @param rowNum
     * @param item
     */
    private void createDataRow(Sheet sheet, int rowNum, CCRQ001TranrsItem item) {
        Row row = sheet.createRow(rowNum);
        int colNum = 0;

        // A. 承辦單位
        setCellValue(row, colNum++, item.getCreatedDept());
        // B. 承辦人
        setCellValue(row, colNum++, item.getCreatedEmpName());
        // C. 聯繫單號
        setCellValue(row, colNum++, item.getEformId());
        // D. 系統名稱
        setCellValue(row, colNum++, item.getSysName());
        // E. APID
        setCellValue(row, colNum++, item.getApid());
        // F. 變更執行開始時間
        setCellValue(row, colNum++, formatDateTime(item.getExecuteDate()));
        // G. 變更執行結束時間
        setCellValue(row, colNum++, formatDateTime(item.getEndDate()));
        // H. 變更內容
        setCellValue(row, colNum++, item.getChangeDetails());
        // I. BIA等級
        setCellValue(row, colNum++, convertBiaLevel(item.getBiaLevelId()));
        // J. 工作項目性質
        setCellValue(row, colNum++, convertAttributes(item.getAttributesId()));
        // K. 變更種類
        setCellValue(row, colNum++, convertChangeType(item.getChangeTypeId()));
        // L. 需要中斷或重啟服務
        setCellValue(row, colNum++, convertYesNo(item.getIsDowntimeRequires()));
        // M. 需要公告
        setCellValue(row, colNum++, convertYesNo(item.getIsAnn()));
        // N. 風險評估表
        setCellValue(row, colNum++, convertRiskAssessment(item.getRiskAssessmentId()));
        // O. 受影響範圍
        setCellValue(row, colNum++, item.getAffectedNotes());
        // P. 承製日期
        setCellValue(row, colNum++, formatDateTime(item.getCreatedDate()));
        // Q. 修改時間
        setCellValue(row, colNum++, formatDateTime(item.getModifyDate()));

        // R-CC. HRI 風險項目 (RiskId 1-12)
        String[] hriValues = parseHri(item.getHri());
        for (int i = 0; i < 12; i++) {
            setCellValue(row, colNum++, hriValues[i]);
        }

        // DD. 變更系統涉及AI
        setCellValue(row, colNum++, convertAiCab(item.getIsAiCab()));
        // EE. AI CAB聯繫單號
        setCellValue(row, colNum++, item.getAiCabEformId());
        // FF. 是否啟用
        setCellValue(row, colNum++, convertIsActive(item.getIsActive()));
    }

    /**
     * 設定儲存格值
     *
     * @param row
     * @param colNum
     * @param value
     */
    private void setCellValue(Row row, int colNum, String value) {
        Cell cell = row.createCell(colNum);
        cell.setCellValue(StringUtils.defaultString(value, ""));
    }

    /**
     * 格式化日期時間（yyyy/MM/dd HH:mm:ss → yyyy/MM/dd HH:mm）
     *
     * @param dateTimeStr
     * @return
     */
    private String formatDateTime(String dateTimeStr) {
        if (StringUtils.isBlank(dateTimeStr)) {
            return "";
        }
        try {
            // 移除秒數部分
            if (dateTimeStr.length() >= 16) {
                return dateTimeStr.substring(0, 16);
            }
            return dateTimeStr;
        } catch (Exception e) {
            return dateTimeStr;
        }
    }

    /**
     * 轉換 BIA 等級代碼為中文
     *
     * @param biaLevelId
     * @return
     */
    private String convertBiaLevel(String biaLevelId) {
        if (StringUtils.isBlank(biaLevelId)) return "";
        switch (biaLevelId) {
            case "0": return "無";
            case "1": return "第一級";
            case "2": return "第二級";
            case "3": return "第三級";
            default: return biaLevelId;
        }
    }

    /**
     * 轉換變更種類代碼為中文
     *
     * @param changeTypeId
     * @return
     */
    private String convertChangeType(String changeTypeId) {
        if (StringUtils.isBlank(changeTypeId)) return "";
        switch (changeTypeId) {
            case "1": return "一般";
            case "2": return "例行";
            case "3": return "緊急";
            default: return changeTypeId;
        }
    }

    /**
     * 轉換工作項目性質代碼為中文
     *
     * @param attributesId
     * @return
     */
    private String convertAttributes(String attributesId) {
        if (StringUtils.isBlank(attributesId)) return "";
        switch (attributesId) {
            case "1": return "專案";
            case "2": return "變更";
            case "3": return "維護";
            default: return attributesId;
        }
    }

    /**
     * 轉換風險評估代碼為中文
     *
     * @param riskAssessmentId
     * @return
     */
    private String convertRiskAssessment(String riskAssessmentId) {
        if (StringUtils.isBlank(riskAssessmentId)) return "";
        switch (riskAssessmentId) {
            case "1": return "低 Low";
            case "2": return "中 Med";
            case "3": return "高 High";
            default: return "";
        }
    }

    /**
     * 轉換 0/1 為 Yes/No
     *
     * @param value
     * @return
     */
    private String convertYesNo(String value) {
        if (StringUtils.isBlank(value)) return "No";
        return "1".equals(value) ? "Yes" : "No";
    }

    /**
     * 轉換 AI CAB 0/1 為 是/否
     *
     * @param value
     * @return
     */
    private String convertAiCab(String value) {
        if (StringUtils.isBlank(value)) return "否";
        return "1".equals(value) ? "是" : "否";
    }

    /**
     * 轉換 IS_ACTIVE Y/N 為 啟用/不啟用
     *
     * @param value
     * @return
     */
    private String convertIsActive(String value) {
        if (StringUtils.isBlank(value)) return "";
        return "Y".equals(value) ? "啟用" : "不啟用";
    }

    /**
     * 解析 HRI JSON，回傳 12 個風險項目的值（Y/N）
     *
     * @param hri
     * @return
     */
    private String[] parseHri(String hri) {
        String[] result = new String[12];
        for (int i = 0; i < 12; i++) {
            result[i] = "";
        }

        if (StringUtils.isBlank(hri)) {
            return result;
        }

        try {
            JsonNode rootNode = objectMapper.readTree(hri);
            if (rootNode.isArray()) {
                for (JsonNode node : rootNode) {
                    int riskId = node.has("RiskId") ? node.get("RiskId").asInt() : 0;
                    String riskIsChecked = node.has("RiskIsChecked") ? node.get("RiskIsChecked").asText() : "0";

                    if (riskId >= 1 && riskId <= 12) {
                        result[riskId - 1] = "1".equals(riskIsChecked) ? "Y" : "N";
                    }
                }
            }
        } catch (Exception e) {
            // 解析失敗，保持空白
        }

        return result;
    }

}