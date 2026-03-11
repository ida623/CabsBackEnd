package com.cathaybk.demo.service.impl;

import com.cathaybk.demo.common.ReturnCodeAndDescEnum;
import com.cathaybk.demo.dto.DATC001Tranrq;
import com.cathaybk.demo.dto.EmptyTranrs;
import com.cathaybk.demo.exception.DataNotFoundException;
import com.cathaybk.demo.exception.RestException;
import com.cathaybk.demo.service.DATC001Svc;
import com.cathaybk.demo.user.UserObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * CABS-B-DATC001 新增日期設定
 * @author system
 */
@RequiredArgsConstructor
@Service
@Transactional(rollbackFor = Exception.class)
public class DATC001SvcImpl implements DATC001Svc {

    /** DataSettingWindowRepo */
    private final DataSettingWindowRepo dataSettingWindowRepo;

    /** UserObject */
    private final UserObject userObject;

    /**
     * CABS-B-DATC001 新增日期設定
     * @author 00550354
     */
    @Override
    public EmptyTranrs createDateSettings(DATC001Tranrq tranrq) throws RestException, DataNotFoundException {

        LocalDate startDate = DateFormatUtil.parseToLocalDate(tranrq.getWindowStartDate());

        LocalDate endDate = DateFormatUtil.parseToLocalDate(tranrq.getWindowEndDate());

        if (startDate.isAfter(endDate)) {
            throw new RestException(ReturnCodeAndDescEnum.SE903.getCode(), ReturnCodeAndDescEnum.SE903.getDesc(),
                    "預計執行變更區間－起日不可大於預計執行變更區間－迄日");
        }

        DataSettingWindowEntity largestIdEntity = dataSettingWindowRepo.findTopByOrderByIdDesc()
                .orElseThrow(() -> new DataNotFoundException("查無目前最新日期設定"));

        DataSettingWindowEntity entity = new DataSettingWindowEntity();
        entity.setId(largestIdEntity.getId() + 1);
        entity.setWindowStartDate(startDate);
        entity.setWindowEndDate(endDate);
        entity.setCutoffDate(DateFormatUtil.parseToLocalDateTime(tranrq.getCutoffDate()));
        entity.setCreatedBy(userObject.getEmpName());
        entity.setCreatedAt(LocalDateTime.now());
        dataSettingWindowRepo.save(entity);
        return new EmptyTranrs();
    }

}
