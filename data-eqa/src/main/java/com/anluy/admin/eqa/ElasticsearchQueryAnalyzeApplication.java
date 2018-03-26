package com.anluy.admin.eqa;

import com.anluy.admin.eqa.core.EqaMetaMap;
import com.anluy.admin.eqa.entity.EqaMeta;
import com.anluy.admin.eqa.service.EqaMetaService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@SpringBootApplication
public class ElasticsearchQueryAnalyzeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ElasticsearchQueryAnalyzeApplication.class, args);
	}

	@Bean
	public EqaMetaMap getEqaMeatMap(EqaMetaService eqaMetaService){
		EqaMetaMap eqaMetaMap = new EqaMetaMap();
		List<EqaMeta> list = eqaMetaService.getAll();
		list.forEach(eqaMeta -> {
			eqaMetaMap.addEqaMeta(eqaMeta);
		});
		return eqaMetaMap;
	}

}
