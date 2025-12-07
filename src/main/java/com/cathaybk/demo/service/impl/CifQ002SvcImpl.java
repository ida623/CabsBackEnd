package com.cathaybk.demo.service.impl;

import com.cathaybk.demo.common.ReturnCodeAndDescEnum;
import com.cathaybk.demo.dto.*;
import com.cathaybk.demo.entity.CustomerInfo;
import com.cathaybk.demo.exception.DataNotFoundException;
import com.cathaybk.demo.repository.CustomerInfoRepository;
import com.cathaybk.demo.service.CifQ002Svc;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * XXA-C-CIFQ002 條件過濾查詢客戶（含動態排序）
 */
@Service
@RequiredArgsConstructor
public class CifQ002SvcImpl implements CifQ002Svc {

    private final CustomerInfoRepository customerInfoRepository;
    private final EntityManager entityManager;

    // 定義欄位名稱對應
    private static final Map<String, String> COLUMN_MAPPING = new HashMap<>();

    static {
        COLUMN_MAPPING.put("ORDER_ID", "orderId");
        COLUMN_MAPPING.put("ID_NUM", "idNum");
        COLUMN_MAPPING.put("GENDER", "gender");
        COLUMN_MAPPING.put("EDUCATION", "education");
        COLUMN_MAPPING.put("ADDRESS_1", "address1");
        COLUMN_MAPPING.put("ADDRESS_2", "address2");
        COLUMN_MAPPING.put("EMAIL", "email");
        COLUMN_MAPPING.put("YEAR", "year");
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public ResponseTemplate<CIFQ002Tranrs> filter(RequestTemplate<CIFQ002Tranrq> request) throws DataNotFoundException, IOException {

        CIFQ002Tranrq tranrq = request.getTranrq();
        CIFQ002TranrqData searchData = tranrq.getData();

        // 使用 Criteria API 建立動態查詢
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // 查詢總筆數
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<CustomerInfo> countRoot = countQuery.from(CustomerInfo.class);
        countQuery.select(cb.count(countRoot));
        countQuery.where(buildPredicates(cb, countRoot, searchData));

        Long totalCount = entityManager.createQuery(countQuery).getSingleResult();

        if (totalCount == 0) {
            throw new DataNotFoundException("查無資料");
        }

        // 查詢資料
        CriteriaQuery<CustomerInfo> dataQuery = cb.createQuery(CustomerInfo.class);
        Root<CustomerInfo> dataRoot = dataQuery.from(CustomerInfo.class);
        dataQuery.select(dataRoot);
        dataQuery.where(buildPredicates(cb, dataRoot, searchData));

        // 處理排序
        dataQuery.orderBy(buildOrder(cb, dataRoot, tranrq.getSortInfo()));

        // 執行分頁查詢
        int pageNumber = tranrq.getPage().getPageNumber();
        int pageSize = tranrq.getPage().getPageSize();

        TypedQuery<CustomerInfo> typedQuery = entityManager.createQuery(dataQuery);
        typedQuery.setFirstResult((pageNumber - 1) * pageSize);
        typedQuery.setMaxResults(pageSize);

        List<CustomerInfo> resultList = typedQuery.getResultList();

        // 轉換為 DTO
        List<CIFQ002TranrsItems> items = resultList.stream()
                .map(this::convertToTranrsItem)
                .collect(Collectors.toList());

        // 計算總頁數
        BigDecimal totalCountBD = new BigDecimal(totalCount);
        BigDecimal pageSizeBD = new BigDecimal(pageSize);
        int totalPage = totalCountBD.divide(pageSizeBD, 0, RoundingMode.CEILING).intValue();

        // 建立 TRANRS
        CIFQ002Tranrs tranrs = new CIFQ002Tranrs();
        tranrs.setPageNumber(pageNumber);
        tranrs.setPageSize(pageSize);
        tranrs.setTotalPage(totalPage);
        tranrs.setTotalCount(totalCount.intValue());
        tranrs.setItems(items);

        // 建立 ResponseTemplate
        ResponseTemplate<CIFQ002Tranrs> response = new ResponseTemplate<>();
        HEADER header = new HEADER();
        header.setMsgid(request.getMwheader().getMsgid());
        header.setReturncode(ReturnCodeAndDescEnum.SUCCESS.getCode());
        header.setReturndesc(ReturnCodeAndDescEnum.SUCCESS.getDesc());
        response.setMwheader(header);
        response.setTranrs(tranrs);

        return response;
    }

    /**
     * 建立查詢條件
     */
    private Predicate[] buildPredicates(CriteriaBuilder cb, Root<CustomerInfo> root, CIFQ002TranrqData searchData) {
        List<Predicate> predicates = new ArrayList<>();

        if (searchData != null) {
            if (StringUtils.isNotBlank(searchData.getIdNum())) {
                predicates.add(cb.like(root.get("idNum"), "%" + searchData.getIdNum() + "%"));
            }
            if (StringUtils.isNotBlank(searchData.getChineseName())) {
                predicates.add(cb.like(root.get("chineseName"), "%" + searchData.getChineseName() + "%"));
            }
            if (StringUtils.isNotBlank(searchData.getGender())) {
                predicates.add(cb.equal(root.get("gender"), searchData.getGender()));
            }
            if (StringUtils.isNotBlank(searchData.getEducation())) {
                predicates.add(cb.equal(root.get("education"), searchData.getEducation()));
            }
            if (StringUtils.isNotBlank(searchData.getMobile())) {
                predicates.add(cb.like(root.get("mobile"), "%" + searchData.getMobile() + "%"));
            }
            if (StringUtils.isNotBlank(searchData.getEmail())) {
                predicates.add(cb.like(root.get("email"), "%" + searchData.getEmail() + "%"));
            }
            if (searchData.getYear() != null) {
                predicates.add(cb.equal(root.get("year"), searchData.getYear()));
            }
        }

        return predicates.toArray(new Predicate[0]);
    }

    /**
     * 建立動態排序
     */
    private Order buildOrder(CriteriaBuilder cb, Root<CustomerInfo> root, SortInfo sortInfo) {
        // 轉換欄位名稱（sortInfo 必填，不會是 null）
        String fieldName = COLUMN_MAPPING.getOrDefault(sortInfo.getSortColumn().toUpperCase(), "orderId");

        // 判斷排序方向
        if ("ASC".equalsIgnoreCase(sortInfo.getSortBy())) {
            return cb.asc(root.get(fieldName));
        } else {
            return cb.desc(root.get(fieldName));
        }
    }

    /**
     * 轉換 Entity 為 DTO
     */
    private CIFQ002TranrsItems convertToTranrsItem(CustomerInfo customerInfo) {
        CIFQ002TranrsItems item = new CIFQ002TranrsItems();
        item.setOrderId(customerInfo.getOrderId());
        item.setIdNum(customerInfo.getIdNum());
        item.setChineseName(customerInfo.getChineseName());
        item.setGender(customerInfo.getGender());
        item.setEducation(customerInfo.getEducation());
        item.setZipCode1(customerInfo.getZipCode1());
        item.setAddress1(customerInfo.getAddress1());
        item.setTelephone1(customerInfo.getTelephone1());
        item.setZipCode2(customerInfo.getZipCode2());
        item.setAddress2(customerInfo.getAddress2());
        item.setTelephone2(customerInfo.getTelephone2());
        item.setMobile(customerInfo.getMobile());
        item.setEmail(customerInfo.getEmail());
        item.setYear(customerInfo.getYear());
        return item;
    }
}