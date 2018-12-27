package com.patent.web.project.domain;

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

public class Classification {
	
	@Getter @Setter
	Integer projectId;
	
	@Getter @Setter
	Integer id;
	
	@Setter
	Integer parentId;
	
	@Getter @Setter
	String name;

	public Integer getParentId() {
		if (parentId == null)	return -1;
		
		return parentId;
	}
	
	@Getter @Setter
	Integer dept;
	
	@Getter @Setter
	String code;
	
	@Override
	public String toString() {
		return String.format("{\"id\":%d, \"parentId\":%d}", id, parentId);
	}
}

