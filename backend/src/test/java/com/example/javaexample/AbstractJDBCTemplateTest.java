package com.example.javaexample;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

public class AbstractJDBCTemplateTest extends AbstractTestContainerTest {

    private static DataSource getDataSource() {
        return DataSourceBuilder.create()
                .driverClassName(postgreSQLContainer.getDriverClassName())
                .url(postgreSQLContainer.getJdbcUrl())
                .username(postgreSQLContainer.getUsername())
                .password(postgreSQLContainer.getPassword())
                .build();
    }

    protected JdbcTemplate getJDBCTemplate() {
        return new JdbcTemplate(getDataSource());
    }
}
