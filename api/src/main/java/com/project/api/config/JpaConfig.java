package com.project.api.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.util.Map;

/**
 * JPA 설정 클래스
 * 엔티티 매니저와 트랜잭션 매니저를 설정합니다.
 */
@Configuration
@EnableJpaRepositories(
    basePackages = "com.project.api.adapter.out.persistence",
    entityManagerFactoryRef = "entityManagerFactory",
    transactionManagerRef = "transactionManager"
)
@EnableJpaAuditing
@EnableTransactionManagement
public class JpaConfig {

    /**
     * 엔티티 매니저 팩토리 빈 생성 (운영 환경용: local, dev, stage, prod)
     */
    @Bean("entityManagerFactory")
    @Primary
    @Profile("!test")  // test 프로파일이 아닌 모든 환경
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            DataSource routingDataSource,
            JpaProperties jpaProperties,
            HibernateProperties hibernateProperties) {
        
        EntityManagerFactoryBuilder builder = createEntityManagerFactoryBuilder(jpaProperties, hibernateProperties);
        
        return builder
                .dataSource(routingDataSource)
                .packages("com.project.api.domain")
                .persistenceUnit("default")
                .build();
    }

    /**
     * 엔티티 매니저 팩토리 빈 생성 (테스트 환경용)
     */
    @Bean("entityManagerFactory")
    @Primary
    @Profile("test")  // test 프로파일에서만
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryTest(
            DataSource dataSource,
            JpaProperties jpaProperties,
            HibernateProperties hibernateProperties) {
        
        EntityManagerFactoryBuilder builder = createEntityManagerFactoryBuilder(jpaProperties, hibernateProperties);
        
        return builder
                .dataSource(dataSource)
                .packages("com.project.api.domain")
                .persistenceUnit("default")
                .build();
    }

    /**
     * 트랜잭션 매니저 빈 생성
     */
    @Bean("transactionManager")
    @Primary
    public PlatformTransactionManager transactionManager(LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory.getObject());
    }

    /**
     * EntityManagerFactoryBuilder 생성
     */
    private EntityManagerFactoryBuilder createEntityManagerFactoryBuilder(
            JpaProperties jpaProperties, HibernateProperties hibernateProperties) {
        
        Map<String, Object> properties = hibernateProperties.determineHibernateProperties(
                jpaProperties.getProperties(), new HibernateSettings()
        );
        
        return new EntityManagerFactoryBuilder(
                new org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter(),
                properties,
                null
        );
    }
} 