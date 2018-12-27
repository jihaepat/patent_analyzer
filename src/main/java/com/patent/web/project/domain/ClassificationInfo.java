package com.patent.web.project.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.patent.web.domain.PatentFile;
import com.patent.web.utils.PatentUtiles;

import lombok.Getter;
import lombok.Setter;


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

public class ClassificationInfo {
	
	@Getter @Setter
	Integer parentId;
	
	@Getter @Setter
	Integer id;
	
	@Getter @Setter
	String name;
	
	@Getter @Setter
	String search;
	
	@Getter @Setter
	String remarks;
	
	@Getter @Setter
	String code;
	
	@Getter
	List<PatentFile> files;
	
	public void setFiles(List<PatentFile> files) {
		if (files == null) {
			files = new ArrayList<>();
		}
		
		this.files = files;
	}
	
	public Map toJSON(Boolean result) {
		Map map = new HashMap();
		Map mes = new HashMap();

		map.put("result", result);
		map.put("message", mes);
		
		mes.put("id", id);
		mes.put("name", PatentUtiles.jsonString(name));
		mes.put("code", PatentUtiles.jsonString(code));
		mes.put("search", PatentUtiles.jsonString(search));
		mes.put("remarks", PatentUtiles.jsonString(remarks));
		mes.put("files", files);
		return map;
	}

	@Override
	public String toString() {
		return String.format("{\"id\":%d, \"name\":\"%s\", \"code\":\"%s\", \"search\":\"%s\", \"remarks\":\"%s\", \"files\":%s}", id, PatentUtiles.jsonString(name), PatentUtiles.jsonString(code), PatentUtiles.jsonString(search), PatentUtiles.jsonString(remarks), files); 
	}
}
