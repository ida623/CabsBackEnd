package com.cathaybk.demo.repository;

import com.cathaybk.demo.entity.AnnouncementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface AnnouncementRepo extends JpaRepository<AnnouncementEntity, Long> {

}