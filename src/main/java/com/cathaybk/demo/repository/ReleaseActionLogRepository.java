package com.cathaybk.demo.repository;

import com.cathaybk.demo.entity.ReleaseActionLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * RELEASE_ACTION_LOG Repository
 *
 * @author System
 */
@Repository
public interface ReleaseActionLogRepository extends JpaRepository<ReleaseActionLogEntity, Long> {
}
