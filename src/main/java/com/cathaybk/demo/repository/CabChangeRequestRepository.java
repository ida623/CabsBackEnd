package com.cathaybk.demo.repository;

import com.cathaybk.demo.entity.CabChangeRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * CAB_CHANGE_REQUEST Repository
 *
 * @author 00550396
 */
@Repository
public interface CabChangeRequestRepository extends JpaRepository<CabChangeRequestEntity, String> {

}