package com.cathaybk.demo.repository;

import java.util.Optional;

import com.cathaybk.demo.entity.DateSettingWindowEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * 日期設定視窗 Repository
 *
 * @author system
 */
@Repository
public interface DateSettingWindowRepository extends JpaRepository<DateSettingWindowEntity, Long> {

    /**
     * 查詢最新一筆日期設定
     * 依建立時間降冪，若同時間則依ID降冪
     *
     * @return 最新一筆日期設定
     */
    Optional<DateSettingWindowEntity> findTopByOrderByCreatedAtDescIdDesc();
}