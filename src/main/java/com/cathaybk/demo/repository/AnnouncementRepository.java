package com.cathaybk.demo.repository;

import com.cathaybk.demo.entity.AnnouncementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * ANNOUNCEMENT Repository
 *
 * @author 張育誠
 */
@Repository
public interface AnnouncementRepository extends JpaRepository<AnnouncementEntity, Long> {

}