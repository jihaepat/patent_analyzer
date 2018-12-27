package com.patent.web.utils;

//import java.lang.reflect.Field;
//import java.nio.charset.Charset;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
//import org.springframework.http.converter.HttpMessageConverter;
//import org.springframework.http.converter.StringHttpMessageConverter;
//import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.web.util.UriComponentsBuilder;
//
//import com.patent.web.domain.Pair;
//import com.patent.web.project.domain.CLMN;
//import com.patent.web.project.domain.NexitPatent;
//import com.patent.web.project.domain.Patent;

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

public class GetPatent_TMP {

//	private Class<Patent> clsPatent = Patent.class;
//	private RestTemplate restTemplate = null;
//	private List<CLMN> clmns = null;
//	
//	public GetPatent_TMP() {
////		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
////		factory.setConnectTimeout(10*1000);
////		factory.setReadTimeout(10*1000);
//
//		restTemplate = new RestTemplate();
//		restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
//		
//		for(HttpMessageConverter<?> messageConverter : restTemplate.getMessageConverters()) {
//			if(messageConverter instanceof AllEncompassingFormHttpMessageConverter) {
//				((AllEncompassingFormHttpMessageConverter) messageConverter).setCharset(Charset.forName("UTF-8"));
//				((AllEncompassingFormHttpMessageConverter) messageConverter).setMultipartCharset(Charset.forName("UTF-8"));
//			}
//		}
//	}
//	
//	public GetPatent_TMP(List<CLMN> clmns) {
////		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
////		factory.setConnectTimeout(10*1000);
////		factory.setReadTimeout(10*1000);
//		
//		restTemplate = new RestTemplate();
//		restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
//		
//		for(HttpMessageConverter<?> messageConverter : restTemplate.getMessageConverters()) {
//			if(messageConverter instanceof AllEncompassingFormHttpMessageConverter) {
//				((AllEncompassingFormHttpMessageConverter) messageConverter).setCharset(Charset.forName("UTF-8"));
//				((AllEncompassingFormHttpMessageConverter) messageConverter).setMultipartCharset(Charset.forName("UTF-8"));
//			}
//		}
//		
//		this.clmns = clmns;
//	}
//	
//	@SuppressWarnings({ "unchecked", "static-access" })
//	public Pair<Integer, String> putNoise(String uri, String tableName, Integer userId, Integer id, Integer qna, String lang) throws Exception {
//		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(uri);
//		
//		JSONObject jsonObj = new JSONObject();
//		jsonObj.put("user_id", userId.toString());
//		jsonObj.put("db_table", String.format("TB_%s", tableName.toUpperCase()));
//		jsonObj.put("pt_id", id.toString());
//		jsonObj.put("language", lang);
//		jsonObj.put("number_of_qa", qna.toString());
//		
//		HttpHeaders headers = new HttpHeaders();
//		headers.setContentType(MediaType.APPLICATION_JSON);
//		HttpEntity<String> entity = new HttpEntity<String>(jsonObj.toJSONString(), headers);
//		
//		Pair<Integer, String> result = new Pair<>();
//		result.setV1(0);
//		ResponseEntity<String> response = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.PUT, entity, String.class);
//		if (response.getStatusCode() == HttpStatus.ACCEPTED) {
//			result.setV2(response.getHeaders().getFirst(headers.SET_COOKIE));
//
//			JSONParser parser = new JSONParser();
//			Object obj = parser.parse( response.getBody().toString() );
//			if (obj != null) {
//				jsonObj = (JSONObject) obj;
//				if (jsonObj.get("status") != null) {
//					if ("success".equals(jsonObj.get("status").toString())) {
//						result.setV1(1);
//					} else if (jsonObj.get("message") != null && "Your order is still being processed".equals(jsonObj.get("status").toString())) {
//						result.setV1(2);
//					}
//				}
//			}
//		}
//		
//		
//		return result;
//	}
//	
//	@SuppressWarnings({ "rawtypes", "unchecked" })
//	public List<Integer> getNoise(String uri, String session) throws Exception {
//		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(uri);
//		HttpHeaders headers = new HttpHeaders();
//		headers.setContentType(MediaType.APPLICATION_JSON);
//		headers.set("Cookie", String.format("%s", session));
//		HttpEntity entity = new HttpEntity(headers);
//
//		ResponseEntity<String> response = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET, entity, String.class);
//		if (response.getStatusCode() == HttpStatus.OK) {
//			JSONParser parser = new JSONParser();
//			Object obj = parser.parse( response.getBody().toString() );
//	
//			if (obj != null) {
//				JSONObject jsonObj = (JSONObject) obj;
//				if (jsonObj.get("status") != null) {
//					if ("success".equals(jsonObj.get("status").toString())) {
//						List<Integer> ids = new ArrayList<>();
//						
//						JSONArray results = (JSONArray) jsonObj.get("data");
//						for(Object ptObj : results) {
//							JSONObject jsonPt = (JSONObject) ptObj;
//							ids.add(Integer.parseInt(jsonPt.get("id").toString()));
//						}
//						
//						return ids;
//					}
//				}
//			}
//		}
//		return null;
//	}
//	
//	@SuppressWarnings("unchecked")
//	public List<Integer> setNoise(String uri, Integer qna, String param, String session) throws Exception {
//		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(uri);
//		
//		JSONObject jsonObj = new JSONObject();
//		jsonObj.put("answers", param);
//		jsonObj.put("number_of_qa", qna.toString());
//		
//		HttpHeaders headers = new HttpHeaders();
//		headers.setContentType(MediaType.APPLICATION_JSON);
//		headers.set("Cookie", String.format("%s", session));
//		HttpEntity<String> entity = new HttpEntity<String>(jsonObj.toJSONString(), headers);
//		
//		ResponseEntity<String> response = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.POST, entity, String.class);
//		if (response.getStatusCode() == HttpStatus.CREATED) {
//			JSONParser parser = new JSONParser();
//			Object obj = parser.parse( response.getBody().toString() );
//			
//			if (obj != null) {
//				jsonObj = (JSONObject) obj;
//				if (jsonObj.get("status") != null) {
//					if ("success".equals(jsonObj.get("status").toString())) {
//						List<Integer> ids = new ArrayList<>();
//						
//						JSONArray results = (JSONArray) jsonObj.get("data");
//						for(Object ptObj : results) {
//							JSONObject jsonPt = (JSONObject) ptObj;
//							ids.add(Integer.parseInt(jsonPt.get("id").toString()));
//						}
//						
//						return ids;
//					}
//				}
//			}
//		}
//		
//		return null;
//	}
//	
//	@SuppressWarnings({ "unchecked", "rawtypes" })
//	public List<Integer> deleteNoise(String uri, String session) throws Exception {
//		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(uri);
//		HttpHeaders headers = new HttpHeaders();
//		headers.setContentType(MediaType.APPLICATION_JSON);
//		headers.set("Cookie", String.format("%s", session));
//		HttpEntity entity = new HttpEntity(headers);
//		
//		ResponseEntity<String> response = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.DELETE, entity, String.class);
//		if (response.getStatusCode() == HttpStatus.CREATED) {
//			JSONParser parser = new JSONParser();
//			Object obj = parser.parse( response.getBody().toString() );
//			
//			if (obj != null) {
//				JSONObject jsonObj = (JSONObject) obj;
//				if (jsonObj.get("status") != null) {
//					if ("success".equals(jsonObj.get("status").toString())) {
//						List<Integer> ids = new ArrayList<>();
//						
//						JSONArray results = (JSONArray) jsonObj.get("data");
//						for(Object ptObj : results) {
//							JSONObject jsonPt = (JSONObject) ptObj;
//							ids.add(Integer.parseInt(jsonPt.get("id").toString()));
//						}
//						
//						return ids;
//					}
//				}
//			}
//		}
//		
//		return null;
//	}
//	
//	
//	
//	/******************************************************************************************************************/
//	
//	
//	public List<NexitPatent> getPatent(String uri) throws Exception {
//		List<NexitPatent> list = new ArrayList<>();
//		String responseString = "";
//		try {
//			responseString = restTemplate.getForObject(uri, String.class);	
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new Exception("검색API에 접속할 수 없습니다.");
//		}
//		
//
//		JSONParser parser = new JSONParser();
//		Object obj = parser.parse( responseString );
//		if (obj != null) {
//			JSONObject jsonObj = (JSONObject) obj;
//			JSONArray results = (JSONArray) jsonObj.get("results");
//			for(Object ptObj : results) {
//				JSONObject jsonPt = (JSONObject) ptObj;
//				NexitPatent patent = new NexitPatent();
//				
//				for (CLMN clmn : clmns) {
//					Field field = clsPatent.getDeclaredField(clmn.getMthd());
//					Object value = (Object) jsonPt.get(clmn.getName());
//					
//					if (field != null && value != null) {
//						field.setAccessible(true);
//						if ("S".equals(clmn.getType())) {
//							field.set(patent, getStr(value));
//						} else if ("I".equals(clmn.getType())) {
//							field.set(patent, getInt(value));
//						}
//					}
//				}
//				
//				list.add(patent);
//			}
//		}
//		
//		return list;
//	}
//	
//	public String getStr(Object value) {
//		if (value instanceof String) {
//			return (String) value;
//		} else if (value instanceof Integer) {
//			return ((Integer)value).toString();
//		} else if (value instanceof Long) {
//			return ((Long)value).toString();
//		}
//		
//		return null;
//	}
//	
//	public Integer getInt(Object value) {
//		if (value instanceof String) {
//			return Integer.parseInt((String)value);
//		} else if (value instanceof Integer) {
//			return ((Integer)value);
//		} else if (value instanceof Long) {
//			return ((Long)value).intValue();
//		}
//		
//		return null;
//	}
}
