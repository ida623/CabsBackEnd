package com.cathaybk.demo.repository;

import com.cathaybk.demo.entity.MsgCode;
import com.cathaybk.demo.entity.MsgCodeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MsgCodeRepository extends JpaRepository<MsgCode, MsgCodeId> {
    List<MsgCode> findByMsgCode (String msgCode);
}
