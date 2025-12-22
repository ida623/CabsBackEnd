package com.cathaybk.demo.mapper;

import com.cathaybk.demo.dto.CIFT001TranrqData;
import com.cathaybk.demo.dto.CIFT002TranrqData;
import com.cathaybk.demo.dto.CIFQ001TranrsData;
import com.cathaybk.demo.entity.CustomerInfoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CustomerInfoMapper {

    CIFQ001TranrsData toTranrsData(CustomerInfoEntity entity);

    CustomerInfoEntity toEntity(CIFT001TranrqData data);

    // 更新方法：自動忽略 null 值
    void updateEntityFromData(CIFT002TranrqData data, @MappingTarget CustomerInfoEntity entity);
}