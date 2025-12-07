package com.cathaybk.demo.sql;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SqlUtils
 */
@Component
@RequiredArgsConstructor
public class SqlUtils {

    /** ORDER_BY 替換字 */
    private static final String ORDER_BY_FIELD = ":ORDER_BY";

    /** 查詢欄位 替換字 */
    private static final String QUERY_FIELD = ":QUERY_FIELD";

    private final GetSqlStrSrvUtil theGetSqlStrSrvUtil;

    /**
     * 取得並組成動態查詢SQL
     *
     * @param sqlName
     * @param dataMap
     * @return
     * @throws IOException
     */
    public String getDynamicQuerySQL(String sqlName, Map<String, Object> dataMap) throws IOException {
        return this.getQuerySql(getSQLStr(sqlName, null, null), dataMap);
    }

    /**
     * 取得並組成動態查詢SQL + order by
     *
     * @param sqlName
     * @param dataMap
     * @param orderBy
     * @return
     * @throws IOException
     */
    public String getDynamicQuerySQL(String sqlName, Map<String, Object> dataMap, String[] orderBy) throws IOException {
        return this.getQuerySql(getSQLStr(sqlName, null, orderBy), dataMap);
    }

    /**
     * 取得並組成動態查詢SQL + order by
     *
     * @param sqlName
     * @param dataMap
     * @param orderBy
     * @return
     * @throws IOException
     */
    public String getDynamicQuerySQL(String sqlName, Map<String, Object> dataMap, String[] queryFields, String[] orderBy)
            throws IOException {
        return this.getQuerySql(getSQLStr(sqlName, queryFields, orderBy), dataMap);
    }

    /**
     * 取得查詢SQL
     *
     * @param sqlName
     * @throws IOException
     */
    public String getQuerySql(String sqlName) throws IOException {
        return this.getSQLStr(sqlName, null, null);
    }

    private String getSQLStr(String sqlName, String[] queryFields, String[] orderBy) throws IOException {
        String sql = theGetSqlStrSrvUtil.getSql(sqlName);

        if (sql.contains(QUERY_FIELD) && queryFields != null && queryFields.length > 0) {
            sql = replaceField(sql, QUERY_FIELD, queryFields);
        }

        if (sql.toLowerCase().contains("order by") && orderBy != null && orderBy.length > 0) {
            sql = replaceField(sql, ORDER_BY_FIELD, orderBy);
        }

        return sql;
    }

    /**
     * 組動態查詢條件SQL
     *
     * @param sql
     * @param sqlMap
     */
    private String getQuerySql(String sql, Map<String, Object> sqlMap) {

        String[] str = StringUtils.substringsBetween(sql, "[", "]");
        if (str == null) {
            return sql;
        }

        Pattern pattern = Pattern.compile("\\:(\\S*)");

        List<String> strList = Arrays.asList(str);
        List<String> getStrList = new ArrayList<>();
        Set<String> keySet = sqlMap.keySet();
        for (String s : strList) {
            Matcher matcher = pattern.matcher(s);
            List<String> subStrList = new ArrayList<>();
            while (matcher.find()) {
                subStrList.add(matcher.group(0).replace(":", ""));
            }
            int subStrListSize = subStrList.size();
            if (subStrListSize != 0 && keySet.containsAll(subStrList)) {
                getStrList.add(s);
            }

        }
        strList = new ArrayList<>(strList);
        strList.removeAll(getStrList);

        for (String s : strList) {
            sql = sql.replace(s, "");
        }

        sql = sql.replace("[", "").replace("]", "");
        return sql;
    }

    /**
     * 替換欄位
     *
     * @param sql
     * @return
     */
    private String replaceField(String sql, String replaceField, String[] orderBy) {
        String orderByStr = String.join(",", orderBy);
        return sql.replace(replaceField, orderByStr);
    }

}