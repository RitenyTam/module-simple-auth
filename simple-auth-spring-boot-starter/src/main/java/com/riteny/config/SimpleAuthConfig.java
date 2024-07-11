package com.riteny.config;

import com.riteny.config.anno.SimpleAuthArgumentTypeResolver;
import com.riteny.simpleauth.SimpleAuthService;
import com.riteny.simpleauth.datasource.impl.PropertiesUserDataSource;
import com.riteny.simpleauth.datasource.impl.SqlLiteUserDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SimpleAuthConfig {

    @Bean
    @ConditionalOnExpression("${simple.auth.datasource.type} == 1")
    public SimpleAuthService simpleAuthServiceForProperties() {
        PropertiesUserDataSource userDataSource = new PropertiesUserDataSource();
        return new SimpleAuthService(userDataSource);
    }

    @Bean
    @ConditionalOnExpression("${simple.auth.datasource.type} == 2")
    public SimpleAuthService simpleAuthServiceForSqlLiteDataSource(@Value("${simple.auth.datasource.url}") String dataSourcePath) {
        SqlLiteUserDataSource userDataSource = new SqlLiteUserDataSource(dataSourcePath);
        return new SimpleAuthService(userDataSource);
    }

    @Bean
    public SimpleAuthArgumentTypeResolver simpleAuthArgumentTypeResolver(SimpleAuthService simpleAuthService) {
        return new SimpleAuthArgumentTypeResolver(simpleAuthService);
    }
}
