package com.cathaybk.demo.repository;

import com.cathaybk.demo.entity.MsgCodeEntity;
import com.cathaybk.demo.entity.MsgCodeIdEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MsgCodeRepository extends JpaRepository<MsgCodeEntity, MsgCodeIdEntity> {
    List<MsgCodeEntity> findByMsgCode (String msgCode);
}
