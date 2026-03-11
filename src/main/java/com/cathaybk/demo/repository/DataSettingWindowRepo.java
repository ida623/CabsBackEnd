package com.cathaybk.demo.repository;

import com.cathaybk.demo.entity.DataSettingWindowEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DataSettingWindowRepo extends JpaRepository<DataSettingWindowEntity, Long> {

/**
* 依「建立時間」判斷最新一筆：`CREATED_AT` 最大者
* @return
*/
DataSettingWindowEntity findTopByOrderByCreatedAtDescIdDesc();

/**
* 查詢 ID 最大的一筆資料
*/
Optional<DataSettingWindowEntity> findTopByOrderByIdDesc();

}