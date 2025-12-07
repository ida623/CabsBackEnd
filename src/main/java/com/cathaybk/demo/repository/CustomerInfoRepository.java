package com.cathaybk.demo.repository;

import com.cathaybk.demo.entity.CustomerInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerInfoRepository extends JpaRepository<CustomerInfo, Integer>, JpaSpecificationExecutor<CustomerInfo> {
    List<CustomerInfo> findByIdNum(String idNum);
}