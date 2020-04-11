package com.practice.dianping;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchAutoConfiguration;
import org.springframework.boot.autoconfigure.elasticsearch.rest.RestClientAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.practice.dianping")
@MapperScan("com.practice.dianping.dal")
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableScheduling
// 重要
@EnableAutoConfiguration(exclude={ElasticsearchAutoConfiguration.class, RestClientAutoConfiguration.class})
public class DianpingApplication {

	public static void main(String[] args) {
		SpringApplication.run(DianpingApplication.class, args);
	}

}
