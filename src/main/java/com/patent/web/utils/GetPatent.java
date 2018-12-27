package com.patent.web.utils;

import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.patent.web.project.domain.CLMN;
import com.patent.web.project.domain.NexitPatent;
import com.patent.web.project.domain.Patent;

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

public class GetPatent {

	private Class<Patent> clsPatent = Patent.class;
	private RestTemplate restTemplate = null;
	private List<CLMN> clmns = null;
	
	public GetPatent() {
		initClmns();
	}
	public GetPatent(List<CLMN> clmns) {
		this.clmns = clmns;
		
		initClmns();
	}
	
	private void initClmns() {
		restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
		restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
		
		for(HttpMessageConverter<?> messageConverter : restTemplate.getMessageConverters()) {
			if(messageConverter instanceof AllEncompassingFormHttpMessageConverter) {
				((AllEncompassingFormHttpMessageConverter) messageConverter).setCharset(Charset.forName("UTF-8"));
				((AllEncompassingFormHttpMessageConverter) messageConverter).setMultipartCharset(Charset.forName("UTF-8"));
			}
		}
	}
	
	public List<Integer> getPatent(String uri, String param) throws Exception {
		List<Integer> noiseIds = new ArrayList<>();
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(uri);
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		
		HttpEntity<String> entity = new HttpEntity<String>(param, headers);
		
		try {
			ResponseEntity<String> response = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.POST, entity, String.class);
			if (response.getStatusCode() == HttpStatus.OK) {
				JsonParser parser = new JsonParser();
				JsonElement rootElem = parser.parse( response.getBody().toString() );
				if (rootElem.isJsonObject()) {
					JsonObject root = rootElem.getAsJsonObject();
					JsonElement resultsElem = root.get("results");
					if (resultsElem.isJsonArray()) {
						JsonArray results = resultsElem.getAsJsonArray();
						for (JsonElement rtElem : results) {
							JsonObject result = rtElem.getAsJsonObject();
							if (result.get("is_noise").getAsBoolean()) {
								noiseIds.add(result.get("id").getAsInt());
							}
						}
					}
				}
			}
		} catch (ResourceAccessException e) {
			e.printStackTrace();
			throw new Exception("검색API에 접속할 수 없습니다.");
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("검색API에 알수 없는 오류가 발생했습니다.");
		}
		
		return noiseIds;
	}
	
	/******************************************************************************************************************/
	public List<NexitPatent> getPatent(String uri) throws Exception {
		List<NexitPatent> list = new ArrayList<>();
		String responseString = "";
		
		try {
			responseString = restTemplate.getForObject(uri, String.class);
		} catch (ResourceAccessException e) {
			e.printStackTrace();
			throw new Exception("검색API에 접속할 수 없습니다.");
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("검색API에 알수 없는 오류가 발생했습니다.");
		}
		
		JsonParser parser = new JsonParser();
		JsonElement rootElem = parser.parse(responseString);
		if (rootElem.isJsonObject()) {
			JsonObject root = rootElem.getAsJsonObject();
			JsonElement resultsElem = root.get("results");
			if (resultsElem.isJsonArray()) {
				JsonArray results = resultsElem.getAsJsonArray();
				for (JsonElement rtElem : results) {
					JsonObject result = rtElem.getAsJsonObject();
					NexitPatent patent = new NexitPatent();
					
					for (CLMN clmn : clmns) {
						Field field = clsPatent.getDeclaredField(clmn.getMthd());
						JsonElement value = result.get(clmn.getName());

						if (field != null && value != null && !value.isJsonNull()) {
							field.setAccessible(true);
							
							if ("S".equals(clmn.getType())) {
								field.set(patent, value.getAsString());
							} else if ("I".equals(clmn.getType())) {
								field.set(patent, getInt(value.getAsString()));
							}
						}
					}
					
					list.add(patent);
				}
			}
		}
		
		return list;
	}
	
	public Integer getInt(String value) {
		try {
			return Integer.parseInt(value);	
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
