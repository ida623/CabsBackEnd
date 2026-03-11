package com.cathaybk.demo.repository;
import java.util.List;

import com.cathaybk.demo.entity.CodeLookupEntity;
import com.cathaybk.demo.entity.CodeLookupEntityPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CodeLookupRepo extends JpaRepository<CodeLookupEntity, CodeLookupEntityPK> {

    /**
     * 依代碼類別查詢，依 CODE_VALUE ASC 排序
     *
     * @param codeType 代碼類別
     * @return 代碼清單
     */
    List<CodeLookupEntity> findByCodeTypeOrderByCodeValueAsc(String codeType);

}