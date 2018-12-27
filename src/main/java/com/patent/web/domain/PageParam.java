package com.patent.web.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


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

@ToString
public class PageParam {
	@Setter @Getter protected Integer limit;
	@Setter @Getter protected String search;
	
	@Getter protected Integer page;
	@Setter @Getter protected Integer memberId;
	@Setter @Getter protected String tableName;
	
	public PageParam() {
		page = 1;
	}
	
	public void setPage(int page) {
		if(page>0) --page;
		this.page = page;
	}
}
