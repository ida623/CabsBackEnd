package com.cathaybk.demo.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import com.cathaybk.demo.dto.*;
import com.cathaybk.demo.factory.NormalResponseFactory;
import com.cathaybk.demo.service.CODQ001Svc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

/**
 * CABS-B-CODQ001 代碼查詢
 *
 * @author 張育誠
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CODQ001SvcImpl implements CODQ001Svc {

    /** NormalResponseFactory */
    private final NormalResponseFactory normalResponseFactory;

    /** CodeLookupRepository */
    private final CodeLookupRepository codeLookupRepository;

    @Override
    public ResponseTemplate<CODQ001Tranrs> queryList(RequestTemplate<CODQ001Tranrq> req)
            throws IOException {

        String codeType = req.getTranrq().getCodeType();

        // 查詢 CODE_LOOKUP，依 CODE_VALUE 排序
        List<CodeLookupEntity> entityList = codeLookupRepository.findByCodeTypeOrderByCodeValueAsc(codeType);

        // 轉換為下行格式
        List<CODQ001TranrsItem> items = entityList.stream()
                .map(entity -> new CODQ001TranrsItem(entity.getCodeValue(), entity.getCodeName()))
                .collect(Collectors.toList());

        return normalResponseFactory.genNormalResponse(new CODQ001Tranrs(items), req);
    }

}