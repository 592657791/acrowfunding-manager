package com.an.acrowfunding.manager.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.an.acrowfunding.common.bean.ResultAjax;
import com.an.acrowfunding.common.controller.BaseController;
import com.an.acrowfunding.common.util.Page;
import com.an.acrowfunding.manager.service.ProcessService;


@RequestMapping("/process")
@Controller
public class ProcessController extends BaseController{
	
	@Autowired
	ProcessService processService;
	
	@Autowired
	RestTemplate restTemplate;
	
	@RequestMapping("index")
	public String index() {
		return "process";
	}
	
	@ResponseBody
	@RequestMapping("loadDataList")
	public ResultAjax loadDataList(Integer pageno,Integer pagesize) {
		
		System.out.println("pageNo:"+pageno);
		System.out.println("paegSize:"+pagesize);
		
		ResultAjax result = new ResultAjax();
		
		Page<Map<String, Object>> page = new Page<>(pageno, pagesize);
		Map<String, Object> paramMap = new HashMap<>();
		
		int startindex = page.getStartIndex();
		
		paramMap.put("startindex", startindex);
		paramMap.put("pagesize", pagesize);
		
		List<Map<String, Object>> queryDataList = processService.queryDataList(paramMap);
		Integer count = (Integer) processService.queryPageCount();
		
		page.setDatas(queryDataList);
		page.setTotalsize(count);
		
		result.add("page", page);
		result.setSuccess(true);
		return result;
	}
	
	@ResponseBody
	@RequestMapping("/upload")
	public ResultAjax upload(HttpServletRequest request) throws Exception{
		
		ResultAjax aJaxResult = new ResultAjax();
		
		try {
			MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest)request;
			
			MultipartFile file = multipartHttpServletRequest.getFile("procDefFile");
			//HttpHeaders headers = new HttpHeaders();
			//headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			String uuid = UUID.randomUUID().toString();
			
			String fileName = file.getOriginalFilename();
			
			final File tempFile = File.createTempFile(uuid, fileName.substring(fileName.lastIndexOf(".")));
			file.transferTo(tempFile);
			FileSystemResource resource = new FileSystemResource(tempFile);  
			MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();  
			param.add("pdfile", resource);
			
			ResultAjax result = restTemplate.postForObject("http://atcrowdfunding-activiti-service/act/upload",param,ResultAjax.class);
			
			if(result.isSuccess()) {
				tempFile.delete();
				aJaxResult.setSuccess(true);
			}else {
				aJaxResult.setSuccess(false);
			}
		} catch (Exception e) {
			aJaxResult.setSuccess(false);
			e.printStackTrace();
		}
		return aJaxResult;
	}
	
	@RequestMapping("/view")
	public String view(String id,Model model) {
		System.out.println("id:"+id);
		model.addAttribute(id);
		return "process/view";
		
	}
	
	@RequestMapping("/loadImg")
	public void loadImg( String id, HttpServletResponse resp ) throws Exception { //流程实例id是字符串类型.
		// 通过响应对象返回图形信息
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.IMAGE_PNG);
		String url = "http://atcrowdfunding-activiti-service/act/loadImgById/"+id;
		ResponseEntity<byte[]> response = restTemplate.exchange( url, HttpMethod.POST,  new HttpEntity<byte[]>(headers), byte[].class); 
	    byte[] result = response.getBody();

	        InputStream in = new ByteArrayInputStream(result);
		OutputStream out = resp.getOutputStream();
		
		int i = -1;
		while ( (i = in.read()) != -1 ) {
			out.write(i);
		}
	}
	
	@ResponseBody
	@RequestMapping("delete")
	public Object delete(@RequestParam("id")String id) {
		ResultAjax result = new ResultAjax();
		try {
			ResultAjax result1 = (ResultAjax) processService.delete(id);
			if(result1.isSuccess()) {
				result.setSuccess(true);
			}else {
				result.setSuccess(false);
			}
			result.setSuccess(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			result.setSuccess(false);
			e.printStackTrace();
		}
		return result;
	}

}
