package com.cathaybk.demo.repository;

import java.util.List;

import com.cathaybk.demo.entity.CodeLookupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * CODE_LOOKUP Repository
 *
 * @author
 */
@Repository
public interface CodeLookupRepository extends JpaRepository<CodeLookupEntity, CodeLookupEntity.CodeLookupId> {

    /**
     * 依代碼類別查詢，並依代碼值升序排序
     *
     * @param codeType 代碼類別
     * @return 代碼清單
     */
    List<CodeLookupEntity> findByCodeTypeOrderByCodeValueAsc(String codeType);

}