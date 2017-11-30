package com.an.acrowfunding.manager.service;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.an.acrowfunding.common.bean.ResultAjax;

@FeignClient("atcrowdfunding-activiti-service")
public interface ProcessService {

	@RequestMapping("/act/queryDataList")
	public List<Map<String,Object>> queryDataList(@RequestBody Map<String, Object>paramMap);
	
	@RequestMapping("/act/queryPageCount")
	public Object queryPageCount();

	@RequestMapping("/act/delete/{id}")
	public ResultAjax delete(@PathVariable("id")String id);
	
}
