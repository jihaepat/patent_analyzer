package com.patent.web.project.domain;

import java.util.List;

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


@SuppressWarnings("rawtypes")
public class SimpleChartResult {
	@Getter @Setter Boolean result;
	
	@Getter @Setter ChartInfo info;
	@Getter @Setter List infos;
	
	public SimpleChartResult() {
        this.result = Boolean.TRUE;
    }
	
	public SimpleChartResult info(ChartInfo info) {
		this.info = info;
		return this;
	}
	
	public SimpleChartResult infos(List infos) {
		this.infos = infos;
		return this;
	}
	
	public SimpleChartResult failure() {
		this.result =Boolean.FALSE;
		return this;
	}
	
}

