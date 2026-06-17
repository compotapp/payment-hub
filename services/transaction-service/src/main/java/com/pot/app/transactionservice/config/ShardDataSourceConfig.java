//package com.pot.app.transactionservice.config;
//
//import com.pot.app.transactionservice.config.properties.DatasourceProperties;
//import com.zaxxer.hikari.HikariDataSource;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import org.springframework.jdbc.core.JdbcTemplate;
//
//import javax.sql.DataSource;
//import java.util.HashMap;
//import java.util.Map;
//
//@Configuration
//@RequiredArgsConstructor
//public class ShardDataSourceConfig {
//
//    private final DatasourceProperties properties;
//
//    @Bean
//    public DataSource shard0DataSource() {
//        HikariDataSource ds = new HikariDataSource();
//        ds.setJdbcUrl(properties.getShard0().getUrl());
//        ds.setUsername(properties.getShard0().getUsername());
//        ds.setPassword(properties.getShard0().getPassword());
//        return ds;
//    }
//
//    @Bean
//    public DataSource shard1DataSource() {
//        HikariDataSource ds = new HikariDataSource();
//        ds.setJdbcUrl(properties.getShard1().url());
//        ds.setUsername(properties.getShard1().username());
//        ds.setPassword(properties.getShard1().password());
//        return ds;
//    }
//
//    @Bean
//    @Primary
//    public DataSource routingDataSource(
//            @Qualifier("shard0DataSource") DataSource shard0,
//            @Qualifier("shard1DataSource") DataSource shard1) {
//
//        ShardDataSourceRouter router = new ShardDataSourceRouter();
//
//        Map<Object, Object> targetDataSources = new HashMap<>();
//        targetDataSources.put("shard0", shard0);
//        targetDataSources.put("shard1", shard1);
//
//        router.setTargetDataSources(targetDataSources);
//        router.setDefaultTargetDataSource(shard0); // fallback
//
//        return router;
//    }
//
//    @Bean
//    public JdbcTemplate jdbcTemplate(DataSource routingDataSource) {
//        return new JdbcTemplate(routingDataSource);
//    }
//}