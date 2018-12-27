package com.patent.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;


/////////////////////////////////////////////////////////////////////////////
//
//(c)2003-2018 ITS-I Inc. All Rights Reserved.
//
//THIS SOURCE FILE IS THE PROPERTY OF ITS-I Inc. AND IS NOT TO BE
//RE-DISTRIBUTED BY ANY MEANS WHATSOEVER WITHOUT THE EXPRESSED
//WRITTEN CONSENT OF ITS-I Inc.
//
//CONTACT INFORMATION:
//support@its-i.co.kr
//http://www.its-i.co.kr
//
/////////////////////////////////////////////////////////////////////////////

@Configuration
@MapperScan(basePackages = {"com.patent.dao.mapper"})
public class DatasourceConfigure {
	
	@Bean
	public SqlSessionFactory sqlSessionFactory(DataSource ds) throws Exception {
		SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
	    factory.setDataSource(ds);
	    factory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("/mappers/*Mapper.xml"));
	    return factory.getObject();
	}

	@Bean
	public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory factory) {
		return new SqlSessionTemplate(factory);
	}

}

