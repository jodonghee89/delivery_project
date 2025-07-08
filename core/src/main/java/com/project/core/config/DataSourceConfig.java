package com.project.core.config;

import com.project.core.enums.DadaSourceType;
import com.zaxxer.hikari.HikariDataSource;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

@Configuration
@Profile("!test")  // test 프로파일이 아닌 모든 환경에서 활성화
public class DataSourceConfig {

  @Bean
  @ConfigurationProperties("spring.datasource.primary")
  DataSourceProperties primaryProperties() {
    return new DataSourceProperties();
  }

  @DependsOn("primaryProperties")
  @Bean
  DataSource primaryDataSource() {
    return primaryProperties()
        .initializeDataSourceBuilder()
        .type(HikariDataSource.class)
        .build();
  }

  @Bean
  @ConfigurationProperties("spring.datasource.secondary")
  DataSourceProperties secondaryProperties() {
    return new DataSourceProperties();
  }

  @DependsOn("secondaryProperties")
  @Bean
  DataSource secondaryDataSource() {
    return secondaryProperties()
        .initializeDataSourceBuilder()
        .type(HikariDataSource.class)
        .build();
  }

  @Primary
  @Bean
  DataSource routingDataSource(
      @Qualifier("primaryDataSource") DataSource primaryDataSource,
      @Qualifier("secondaryDataSource") DataSource secondaryDataSource
  ) {
    final AbstractRoutingDataSource routingDataSource = new RoutingDataSource();
    final Map<Object, Object> targetDataSources = new HashMap<>();
    targetDataSources.put(DadaSourceType.PRIMARY.name().toLowerCase(), primaryDataSource);
    targetDataSources.put(DadaSourceType.SECONDARY.name().toLowerCase(), secondaryDataSource);
    routingDataSource.setTargetDataSources(targetDataSources);
    routingDataSource.setDefaultTargetDataSource(primaryDataSource);


    return routingDataSource;
  }
}
