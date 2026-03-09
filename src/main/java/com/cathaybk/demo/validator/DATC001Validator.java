package com.cathaybk.demo.validator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.cathaybk.demo.dto.DATC001Tranrq;
import com.cathaybk.demo.exception.RequestValidException;
import org.springframework.stereotype.Component;

/**
 * DATC001 自訂驗證器
 *
 * @author system
 */
@Component
public class DATC001Validator {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    /**
     * 驗證日期區間邏輯
     * windowStartDate <= windowEndDate
     *
     * @param tranrq
     * @throws RequestValidException
     */
    public void validateDateRange(DATC001Tranrq tranrq) throws RequestValidException {
        try {
            LocalDate startDate = LocalDate.parse(tranrq.getWindowStartDate(), DATE_FORMATTER);
            LocalDate endDate = LocalDate.parse(tranrq.getWindowEndDate(), DATE_FORMATTER);

            if (startDate.isAfter(endDate)) {
                throw new RequestValidException("預計執行變更區間－起日不得晚於迄日");
            }
        } catch (Exception e) {
            if (e instanceof RequestValidException) {
                throw e;
            }
            throw new RequestValidException("日期格式錯誤");
        }
    }
}