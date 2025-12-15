package com.cathaybk.demo.repository;

import com.cathaybk.demo.entity.CustomerInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerInfoRepository extends JpaRepository<CustomerInfoEntity, Long>, JpaSpecificationExecutor<CustomerInfoEntity> {
    List<CustomerInfoEntity> findByIdNum(String idNum);
}