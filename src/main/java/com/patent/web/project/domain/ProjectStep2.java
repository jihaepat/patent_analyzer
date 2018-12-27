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
public class ProjectStep2 {
	@Getter @Setter
	Integer id;
	
	@Getter @Setter
	Integer noise;
	
	@Getter @Setter
	Integer status;
	
	@Getter @Setter
	Integer isNs;
}
