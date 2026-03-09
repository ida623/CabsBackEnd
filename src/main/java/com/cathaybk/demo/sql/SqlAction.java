package com.cathaybk.demo.sql;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.query.sql.internal.NativeQueryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public abstract class SqlAction {
    private static Logger log = LoggerFactory.getLogger(SqlAction.class);
    @PersistenceContext
    protected EntityManager em;
    protected ObjectMapper mapper;

    public SqlAction(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Transactional(
            readOnly = true
    )
    public <T> List<T> queryForList(String sql, Map<String, ?> parameters, Class<T> clazz) {
        this.showlogForSql(sql, parameters);
        Query query = this.em.createNativeQuery(sql, clazz);

        for(Map.Entry<String, ?> entry : parameters.entrySet()) {
            query.setParameter((String)entry.getKey(), entry.getValue());
        }

        return query.getResultList();
    }

    @Transactional(
            readOnly = true
    )
    public List<Map<String, Object>> queryForList(String sql, Map<String, ?> parameters) {
        this.showlogForSql(sql, parameters);
        Query query = this.em.createNativeQuery(sql);
        ((NativeQueryImpl)query.unwrap(NativeQueryImpl.class)).setTupleTransformer((tuple, aliases) -> {
            Map<String, Object> result = new HashMap();

            for(int i = 0; i < aliases.length; ++i) {
                result.put(aliases[i], tuple[i]);
            }

            return result;
        });

        for(Map.Entry<String, ?> entry : parameters.entrySet()) {
            query.setParameter((String)entry.getKey(), entry.getValue());
        }

        return query.getResultList();
    }

    @Transactional(
            readOnly = true
    )
    public <T> List<T> queryForList(EntityManager em, String sql, Map<String, ?> parameters, Class<T> clazz) {
        this.showlogForSql(sql, parameters);
        Query query = em.createNativeQuery(sql, clazz);

        for(Map.Entry<String, ?> entry : parameters.entrySet()) {
            query.setParameter((String)entry.getKey(), entry.getValue());
        }

        return query.getResultList();
    }

    @Transactional(
            readOnly = true
    )
    public List<Map<String, Object>> queryForList(EntityManager em, String sql, Map<String, ?> parameters) {
        this.showlogForSql(sql, parameters);
        Query query = em.createNativeQuery(sql);
        ((NativeQueryImpl)query.unwrap(NativeQueryImpl.class)).setTupleTransformer((tuple, aliases) -> {
            Map<String, Object> result = new HashMap();

            for(int i = 0; i < aliases.length; ++i) {
                result.put(aliases[i], tuple[i]);
            }

            return result;
        });

        for(Map.Entry<String, ?> entry : parameters.entrySet()) {
            query.setParameter((String)entry.getKey(), entry.getValue());
        }

        return query.getResultList();
    }

    @Transactional(
            readOnly = true
    )
    public <T> List<T> queryForListVO(EntityManager em, String sql, Map<String, ?> parameters, Class<T> claz, boolean isTrim) {
        this.showlogForSql(sql, parameters);
        long startTime = System.currentTimeMillis();
        Query query = em.createNativeQuery(sql);
        ((NativeQueryImpl)query.unwrap(NativeQueryImpl.class)).setTupleTransformer((tuple, aliases) -> {
            Map<String, Object> result = new HashMap();

            for(int i = 0; i < aliases.length; ++i) {
                result.put(aliases[i], tuple[i]);
            }

            return result;
        });

        for(Map.Entry<String, ?> entry : parameters.entrySet()) {
            query.setParameter((String)entry.getKey(), entry.getValue());
        }

        List<T> resultList = new ArrayList();
        List<Map<String, Object>> list = query.getResultList();

        for(Map<String, Object> map : list) {
            map.forEach((k, v) -> {
                if (v instanceof String && isTrim && StringUtils.isNotBlank((String)v)) {
                    map.put(k, StringUtils.trim((String)v));
                }

            });
            resultList.add(this.mapper.convertValue(map, claz));
        }

        long endTime = System.currentTimeMillis();
        long sTime = endTime - startTime;
        this.showlogTimeDetail(list.size(), sTime);
        return resultList;
    }

    @Transactional(
            propagation = Propagation.REQUIRED
    )
    public int update(EntityManager em, String sql, Map<String, ?> parameters) {
        return this.executeQuery(em, sql, parameters);
    }

    @Transactional(
            propagation = Propagation.REQUIRED
    )
    public int insert(EntityManager em, String sql, Map<String, ?> parameters) {
        return this.executeQuery(em, sql, parameters);
    }

    @Transactional(
            propagation = Propagation.REQUIRED
    )
    public int delete(EntityManager em, String sql, Map<String, ?> parameters) {
        return this.executeQuery(em, sql, parameters);
    }

    @Transactional(
            readOnly = true
    )
    public <T> List<T> queryForListVO(String sql, Map<String, ?> parameters, Class<T> claz, boolean isTrim) {
        this.showlogForSql(sql, parameters);
        long startTime = System.currentTimeMillis();
        Query query = this.em.createNativeQuery(sql);
        ((NativeQueryImpl)query.unwrap(NativeQueryImpl.class)).setTupleTransformer((tuple, aliases) -> {
            Map<String, Object> result = new HashMap();

            for(int i = 0; i < aliases.length; ++i) {
                result.put(aliases[i], tuple[i]);
            }

            return result;
        });

        for(String key : parameters.keySet()) {
            query.setParameter(key, parameters.get(key));
        }

        List<T> resultList = new ArrayList();
        List<Map<String, Object>> list = query.getResultList();

        for(Map<String, Object> map : list) {
            map.forEach((k, v) -> {
                if (v instanceof String && isTrim && StringUtils.isNotBlank((String)v)) {
                    map.put(k, StringUtils.trim((String)v));
                }

            });
            resultList.add(this.mapper.convertValue(map, claz));
        }

        long endTime = System.currentTimeMillis();
        long sTime = endTime - startTime;
        this.showlogTimeDetail(list.size(), sTime);
        return resultList;
    }

    @Transactional(
            propagation = Propagation.REQUIRED
    )
    protected int executeQuery(EntityManager em, String sql, Map<String, ?> parameters) {
        this.showlogForSql(sql, parameters);
        Query query = em.createNativeQuery(sql);
        ((NativeQueryImpl)query.unwrap(NativeQueryImpl.class)).setTupleTransformer((tuple, aliases) -> {
            Map<String, Object> result = new HashMap();

            for(int i = 0; i < aliases.length; ++i) {
                result.put(aliases[i], tuple[i]);
            }

            return result;
        });

        for(Map.Entry<String, ?> entry : parameters.entrySet()) {
            query.setParameter((String)entry.getKey(), entry.getValue());
        }

        return query.executeUpdate();
    }

    protected void showlogForSql(String sql, Map<String, ?> parameter) {
        String newSql = sql;
        if (parameter != null && !parameter.isEmpty()) {
            for(Map.Entry<String, ?> entry : parameter.entrySet()) {
                newSql = newSql.replace(":" + (String)entry.getKey(), String.valueOf(entry.getValue()));
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("[SQL log]- ").append(" : Query DB->").append(this.replaceAllBlank(newSql));
        String msg = sb.toString();
        log.info(msg);
    }

    protected void showlogTimeDetail(int count, long calTime) {
        StringBuilder sb = new StringBuilder();
        sb.append("[SQL log]- ").append(",[資料筆數]:").append(count).append(",[花費時間]:").append(calTime).append(" ms");
        String msg = sb.toString();
        log.debug(msg);
    }

    protected String replaceAllBlank(String str) {
        String s = "";
        if (str != null) {
            s = str.replaceAll("\\n|\\r|\\t", " ").replaceAll("\\s+", " ");
        }

        return s;
    }
}
