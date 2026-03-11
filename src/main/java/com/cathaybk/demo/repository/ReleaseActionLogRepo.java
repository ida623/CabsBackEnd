package com.cathaybk.demo.repository;

import com.cathaybk.demo.entity.ReleaseActionLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReleaseActionLogRepo extends JpaRepository<ReleaseActionLogEntity, Long> {

}