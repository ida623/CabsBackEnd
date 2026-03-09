package com.cathaybk.demo.sql;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

public abstract class SqlUtils {
    protected static final String ORDER_BY_FIELD = ":ORDER_BY";
    protected static final String QUERY_FIELD = ":QUERY_FIELD";
    protected GetSqlStrSrvUtilInterface theGetSqlStrSrvUtil;

    public SqlUtils(GetSqlStrSrvUtilInterface theGetSqlStrSrvUtil) {
        this.theGetSqlStrSrvUtil = theGetSqlStrSrvUtil;
    }

    public String getDynamicQuerySql(String sqlName, Map<String, Object> dataMap) throws IOException {
        return this.getQuerySql(this.getSqlStr(sqlName, (String[])null, (String[])null), dataMap);
    }

    public String getDynamicQuerySql(String sqlName, Map<String, Object> dataMap, String[] orderBy) throws IOException {
        return this.getQuerySql(this.getSqlStr(sqlName, (String[])null, orderBy), dataMap);
    }

    public String getDynamicQuerySql(String sqlName, Map<String, Object> dataMap, String[] queryFields, String[] orderBy) throws IOException {
        return this.getQuerySql(this.getSqlStr(sqlName, queryFields, orderBy), dataMap);
    }

    public String getQuerySql(String sqlName) throws IOException {
        return this.getSqlStr(sqlName, (String[])null, (String[])null);
    }

    protected String getSqlStr(String sqlName, String[] queryFields, String[] orderBy) throws IOException {
        String sql = this.theGetSqlStrSrvUtil.getSql(sqlName);
        if (StringUtils.contains(sql, ":QUERY_FIELD") && queryFields != null && queryFields.length > 0) {
            sql = this.replaceField(sql, ":QUERY_FIELD", queryFields);
        }

        if ((StringUtils.contains(sql, "order by") || StringUtils.contains(sql, "ORDER BY")) && orderBy != null && orderBy.length > 0) {
            sql = this.replaceField(sql, ":ORDER_BY", orderBy);
        }

        return sql;
    }

    protected String getQuerySql(String sql, Map<String, Object> sqlMap) {
        String[] str = StringUtils.substringsBetween(sql, "[", "]");
        if (str == null) {
            return sql;
        } else {
            Pattern pattern = Pattern.compile("\\:(\\S*)");
            List<String> strList = Arrays.asList(str);
            List<String> getStrList = new ArrayList();
            Set<String> keySet = sqlMap.keySet();

            for(String s : strList) {
                Matcher matcher = pattern.matcher(s);
                List<String> subStrList = new ArrayList();

                while(matcher.find()) {
                    subStrList.add(matcher.group(0).replace(":", ""));
                }

                int subStrListSize = subStrList.size();
                if (subStrListSize != 0 && keySet.containsAll(subStrList)) {
                    getStrList.add(s);
                }
            }

            strList = new ArrayList(strList);
            strList.removeAll(getStrList);

            for(String s : strList) {
                sql = StringUtils.replace(sql, s, "");
            }

            sql = sql.replace("[", "").replace("]", "");
            return sql;
        }
    }

    protected String replaceField(String sql, String replaceField, String[] orderBy) {
        StringBuilder sb = new StringBuilder();

        for(String field : orderBy) {
            sb.append(field).append(',');
        }

        String orderByStr = StringUtils.removeEnd(sb.toString(), ",");
        return sql.replace(replaceField, orderByStr);
    }
}
