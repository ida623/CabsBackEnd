package com.cathaybk.demo.service.impl;

import java.util.List;

import com.cathaybk.demo.dto.CODQ001Tranrq;
import com.cathaybk.demo.dto.CODQ001Tranrs;
import com.cathaybk.demo.dto.CODQ001TranrsItem;
import com.cathaybk.demo.entity.CodeLookupEntity;
import com.cathaybk.demo.repository.CodeLookupRepo;
import com.cathaybk.demo.service.CODQ001Svc;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

/**
 * CABS-B-CODQ001 代碼查詢
 * @author 00550381
 */
@RequiredArgsConstructor
@Service
public class CODQ001SvcImpl implements CODQ001Svc {

    /** CodeLookupRepo */
    private final CodeLookupRepo codeLookupRepo;

    /**
     * CABS-B-CODQ001 代碼查詢
     * 依 codeType 查詢 CODE_LOOKUP，回傳對應代碼清單
     */
    @Override
    public CODQ001Tranrs queryCodeList(CODQ001Tranrq tranrq) {

        List<CodeLookupEntity> entityList =
                codeLookupRepo.findByCodeTypeOrderByCodeValueAsc(tranrq.getCodeType());

        List<CODQ001TranrsItem> items = entityList.stream()
                .map(e -> new CODQ001TranrsItem(e.getCodeValue(), e.getCodeName()))
                .toList();

        return new CODQ001Tranrs(items);
    }

}