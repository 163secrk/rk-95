package com.community.elderlycare.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import javax.sql.DataSource;

@Configuration
public class SqliteConfig {
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("org.sqlite.JDBC");
        String dbPath = System.getProperty("user.dir") + "/data/elderly_care.db";
        ds.setUrl("jdbc:sqlite:" + dbPath);
        return ds;
    }
}
