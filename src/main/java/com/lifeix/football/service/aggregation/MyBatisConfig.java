package com.lifeix.football.service.aggregation;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

/**
 * MyBatis基础配置
 */
@Configuration
public class MyBatisConfig {

    @Autowired
    DataSource dataSource;

    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactoryBean() {
	SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
	// 设置数据源
	bean.setDataSource(dataSource);
	// 添加sqlXML目录
	ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
	try {
	    bean.setMapperLocations(resolver.getResources("classpath:mapper/**/*Mapper.xml"));
	    return bean.getObject();
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new RuntimeException(e);
	}
    }

    @Bean(name = "sqlSession")
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
	return new SqlSessionTemplate(sqlSessionFactory);
    }
}
