package com.anluy.admin.eqa;

import com.anluy.admin.eqa.core.EqaMetaMap;
import com.anluy.admin.eqa.entity.EqaIndex;
import com.anluy.admin.eqa.entity.EqaMeta;
import com.anluy.admin.eqa.service.EqaIndexService;
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
	public EqaMetaMap getEqaMeatMap(EqaIndexService eqaIndexService, EqaMetaService eqaMetaService){
		EqaMetaMap eqaMetaMap = new EqaMetaMap();
		List<EqaIndex> indexs =  eqaIndexService.getAll();
		indexs.forEach(index -> {
			eqaMetaMap.addEqaIndex(index);
		});
		List<EqaMeta> metas = eqaMetaService.getAll();
		metas.forEach(eqaMeta -> {
			eqaMetaMap.addEqaMeta(eqaMeta);
		});
		return eqaMetaMap;
	}

}
