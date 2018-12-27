package com.patent.web.chart;

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

@Getter @Setter
public class Dataset {
	Integer cnt;
	Integer cnt2;
	Integer cnt3;
	Integer cnt4;
	String rwKey;
	String clmnKey;
	
	public Dataset clmnkey(String clmnKey) {
		this.clmnKey = clmnKey;
		return this;
	}
	public Dataset cnt(Integer cnt) {
		this.cnt = cnt;
		return this;
	}
}
