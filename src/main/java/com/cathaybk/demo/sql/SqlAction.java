package com.cathaybk.demo.sql;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.query.sql.internal.NativeQueryImpl;
import org.hibernate.transform.Transformers;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * EntityManager Native SQL方法
 */
@Lazy
@Component
@SuppressWarnings("unchecked")
@RequiredArgsConstructor
public class SqlAction {

    private final EntityManager em;

    private final ObjectMapper mapper;

    /**
     * 查詢
     *
     * @param sql
     * @param parameters
     * @param clazz
     * @return
     */
    @Transactional(readOnly = true)
    public <T> List<T> queryForList(String sql, Map<String, ?> parameters, Class<T> clazz) {
        showLogForSql(sql, parameters);
        Query query = this.em.createNativeQuery(sql, clazz);
        for (Map.Entry<String, ?> entry : parameters.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
        return query.getResultList();
    }

    /**
     * 查詢
     *
     * @param sql
     * @param parameters
     * @param clazz
     * @return
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> queryForList(String sql, Map<String, ?> parameters) {
        showLogForSql(sql, parameters);

        Query query = this.em.createNativeQuery(sql);
        query.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        for (Map.Entry<String, ?> entry : parameters.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
        return query.getResultList();
    }

    /**
     * 查詢並轉成VO
     *
     * @param <T>
     * @param sql
     * @param parameters
     * @param claz
     * @param isTrim
     * @return
     */
    @Transactional(readOnly = true)
    public <T> List<T> queryForListVO(String sql, Map<String, ?> parameters, Class<T> claz, boolean isTrim) {
        showLogForSql(sql, parameters);
        long startTime = System.currentTimeMillis();
        Query query = this.em.createNativeQuery(sql);
        query.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        for (String key : parameters.keySet()) {
            query.setParameter(key, parameters.get(key));
        }

        List<T> resultList = new ArrayList<>();
        List<Map<String, Object>> list = query.getResultList();

        for (Map<String, Object> map : list) {
            map.forEach((k, v) -> {
                if (v instanceof String && isTrim && StringUtils.isNotBlank((String) v)) {
                    map.put(k, StringUtils.trim((String) v));
                }
            });
            resultList.add((T) mapper.convertValue(map, claz));
        }
        long endTime = System.currentTimeMillis();
        long sTime = endTime - startTime;
        showLogTimeDetail(list.size(), sTime);

        return resultList;
    }

    /**
     * 查詢
     *
     * @param <T>
     * @param em
     * @param sql
     * @param parameters
     * @param clazz
     * @return
     */
    @Transactional(readOnly = true)
    public <T> List<T> queryForList(EntityManager em, String sql, Map<String, ?> parameters, Class<T> clazz) {
        showLogForSql(sql, parameters);
        Query query = em.createNativeQuery(sql, clazz);
        for (Map.Entry<String, ?> entry : parameters.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
        return query.getResultList();
    }

    /**
     * 查詢
     *
     * @param em
     * @param sql
     * @param parameters
     * @return
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> queryForList(EntityManager em, String sql, Map<String, ?> parameters) {
        showLogForSql(sql, parameters);

        Query query = em.createNativeQuery(sql);
        query.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        for (Map.Entry<String, ?> entry : parameters.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
        return query.getResultList();
    }

    /**
     * 查詢並轉成VO
     *
     * @param <T>
     * @param em
     * @param sql
     * @param parameters
     * @param claz
     * @param isTrim
     * @return
     */
    @Transactional(readOnly = true)
    public <T> List<T> queryForListVO(EntityManager em, String sql, Map<String, ?> parameters, Class<T> claz, boolean isTrim) {
        showLogForSql(sql, parameters);
        long startTime = System.currentTimeMillis();
        Query query = em.createNativeQuery(sql);
        query.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        for (Map.Entry<String, ?> entry : parameters.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }

        List<T> resultList = new ArrayList<>();
        List<Map<String, Object>> list = query.getResultList();

        for (Map<String, Object> map : list) {
            map.forEach((k, v) -> {
                if (v instanceof String && isTrim && StringUtils.isNotBlank((String) v)) {
                    map.put(k, StringUtils.trim((String) v));
                }
            });
            resultList.add((T) mapper.convertValue(map, claz));
        }
        long endTime = System.currentTimeMillis();
        long sTime = endTime - startTime;
        showLogTimeDetail(list.size(), sTime);

        return resultList;
    }

    /**
     * 修改
     *
     * @param em
     * @param sql
     * @param parameters
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public int update(EntityManager em, String sql, Map<String, ?> parameters) {
        return executeQuery(em, sql, parameters);
    }

    /**
     * 新增
     *
     * @param em
     * @param sql
     * @param parameters
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public int insert(EntityManager em, String sql, Map<String, ?> parameters) {
        return executeQuery(em, sql, parameters);
    }

    /**
     * 刪除
     *
     * @param em
     * @param sql
     * @param parameters
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public int delete(EntityManager em, String sql, Map<String, ?> parameters) {
        return executeQuery(em, sql, parameters);
    }

    /**
     *
     * @param em
     * @param sql
     * @param parameters
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    protected int executeQuery(EntityManager em, String sql, Map<String, ?> parameters) {
        showLogForSql(sql, parameters);

        Query query = em.createNativeQuery(sql);
        query.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        for (Map.Entry<String, ?> entry : parameters.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
        return query.executeUpdate();
    }

    /**
     *
     * @param sql
     * @param parameter
     */
    private void showLogForSql(String sql, Map<String, ?> parameter) {
        String newSql = sql;
        if (parameter != null && !parameter.isEmpty()) {
            for (Map.Entry<String, ?> entry : parameter.entrySet()) {
                newSql = newSql.replace(":" + entry.getKey(), String.valueOf(entry.getValue()));
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("[SQL log]- ").append(" : Query DB->").append(replaceAllBlank(newSql));
        String msg = sb.toString();
        System.out.println(msg);
    }

    /**
     *
     * @param count
     * @param calTime
     */
    private void showLogTimeDetail(int count, long calTime) {

        StringBuilder sb = new StringBuilder();
        sb.append("[SQL log]- ").append(",[資料筆數]:").append(count).append(",[花費時間]:").append(calTime).append(" ms");
        String msg = sb.toString();
        System.out.println(msg);
    }

    private String replaceAllBlank(String str) {
        String s = "";
        if (str != null) {
            s = str.replaceAll("[\\n\\r\\t]", " ").replaceAll("\\s+", " ");
        }
        return s;
    }
}
