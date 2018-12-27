package com.patent.web.project.domain;

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
public class ProjectStep1 {

	@Getter @Setter
	Integer id;
	
	@Getter @Setter
	Integer userId;
	
	@Getter @Setter
	String name;
	
	@Getter @Setter
	String regDate;
	
	@Setter
	Integer regBulletin;
	
	@Getter @Setter
	Integer bulletin;
	
	public Integer getRegBulletin() {
		if (regBulletin == null)	return 0;
		
		return regBulletin;
	}
	
	@Setter
	Integer publicBulletin;
	
	public Integer getPublicBulletin() {
		if (publicBulletin == null)	 return 0;
		
		return publicBulletin;
	}
	
	@Getter @Setter
	Integer memberId;
	
	@Getter @Setter
	Integer classification;
	
	@Getter @Setter
	String tableName;
	
	@Getter @Setter
	Integer status;
	
	@Getter @Setter
	Integer rownum;
	@Getter @Setter
	Integer isNs;
	@Getter @Setter
	Integer isQtd;
}

