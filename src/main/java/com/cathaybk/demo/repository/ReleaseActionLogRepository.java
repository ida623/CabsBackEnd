package com.cathaybk.demo.repository;

import com.cathaybk.demo.entity.ReleaseActionLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


/**
 * RELEASE_ACTION_LOG Repository
 *
 * @author 00550396
 */
@Repository
public interface ReleaseActionLogRepository extends JpaRepository<ReleaseActionLogEntity, Long> {

    /**
     * 查詢指定 eformId 的最新一筆紀錄
     * （依 ACTION_AT DESC, ID DESC 排序取第一筆）
     *
     * @param eformId
     * @return
     */
    Optional<ReleaseActionLogEntity> findFirstByEformIdOrderByActionAtDescIdDesc(String eformId);

}