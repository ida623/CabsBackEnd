package com.cathaybk.demo.sql;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 取得 SQL String (Implements)
 */
@Component
public class GetSqlStrSrvUtil {

    @CacheEvict(value = "${sqlUtils.sql.key}", allEntries = true)
    public void cleanSQLCache() {
        System.out.println("clear sql cache success.");
    }

    @Cacheable(value = "${sqlUtils.sql.key}")
    public String getSql(String sqlName) throws IOException {
        ClassPathResource resource = new ClassPathResource("sql/" + sqlName);
        StringBuilder sb = new StringBuilder();
        try (InputStream dbAsStream = resource.getInputStream();
             BufferedReader br = new BufferedReader(new InputStreamReader(dbAsStream))) {
            while (br.ready()) {
                sb.append(br.readLine());
                sb.append(System.lineSeparator());
            }
        }
        return sb.toString();
    }

}
