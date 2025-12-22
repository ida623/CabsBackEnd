package com.cathaybk.demo.service.impl;

import com.cathaybk.demo.common.ReturnCodeAndDescEnum;
import com.cathaybk.demo.dto.*;
import com.cathaybk.demo.entity.CustomerInfoEntity;
import com.cathaybk.demo.exception.DataNotFoundException;
import com.cathaybk.demo.exception.UpdateFailException;
import com.cathaybk.demo.repository.CustomerInfoRepository;
import com.cathaybk.demo.service.CIFT002SvcSpringBeanUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CIFT002SvcImplSpringBeanUtils implements CIFT002SvcSpringBeanUtils {

    private final CustomerInfoRepository customerInfoRepository;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public ResponseTemplate<EmptyTranrs> editInfoSpringBeanUtils(RequestTemplate<CIFT002Tranrq> request) throws DataNotFoundException {

        CIFT002Tranrq tranrq = request.getTranrq();
        CIFT002TranrqData data = tranrq.getData();

        // 使用 ORDER_ID 查詢客戶資料是否存在
        CustomerInfoEntity customerInfoEntity = customerInfoRepository.findById(data.getOrderId())
                .orElseThrow(() -> new DataNotFoundException("查無資料"));

        // 若要更新身份證號，檢查身份證號是否重複
        if (!customerInfoEntity.getIdNum().equals(data.getIdNum())) {
            List<CustomerInfoEntity> existingCustomers = customerInfoRepository.findByIdNum(data.getIdNum());
            if (!existingCustomers.isEmpty()) {
                throw new UpdateFailException("更新失敗：身份證號已被其他客戶使用");
            }
        }

        // 使用 Spring BeanUtils 進行映射更新
        // Spring BeanUtils 的 copyProperties 會複製 null 值，需要額外處理
        // 使用 getNullPropertyNames 來獲取 null 屬性並忽略它們
        String[] nullPropertyNames = getNullPropertyNames(data);
        BeanUtils.copyProperties(data, customerInfoEntity, nullPropertyNames);

        // 執行更新
        try {
            customerInfoRepository.save(customerInfoEntity);
        } catch (Exception e) {
            throw new UpdateFailException("更新失敗");
        }

        ResponseTemplate<EmptyTranrs> response = new ResponseTemplate<>();
        HEADER header = new HEADER(
                request.getMwheader().getMsgid(),
                ReturnCodeAndDescEnum.SUCCESS.getCode(),
                ReturnCodeAndDescEnum.SUCCESS.getDesc()
        );
        response.setMwheader(header);
        response.setTranrs(new EmptyTranrs());

        return response;
    }

    // 輔助方法：獲取 null 屬性名稱
    private String[] getNullPropertyNames(Object source) {
        final org.springframework.beans.BeanWrapper src = new org.springframework.beans.BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        java.util.Set<String> emptyNames = new java.util.HashSet<>();
        for (java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) emptyNames.add(pd.getName());
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }
}