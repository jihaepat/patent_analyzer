package com.patent.web.domain;

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

public class PatentFile {
	@Getter @Setter
	Integer projectId;
	
	@Getter @Setter
	Integer parentId;
	
	@Getter @Setter
	Integer id;
	
	@Getter @Setter
	String orgname;
	
	@Getter @Setter
	String savename;
	
	
	@Override
	public String toString() {
		return String.format("{\"id\":%d, \"orgname\":\"%s\", \"savename\": \"%s\"}", id, orgname, savename);
	}
}
