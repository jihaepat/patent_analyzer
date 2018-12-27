package com.patent.web.project.domain;

import java.util.ArrayList;
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

public class TreeMenuItem<T> {
	@Getter @Setter
	private T item;
	
	@SuppressWarnings("rawtypes")
	@Getter @Setter
	private List children;
	
	public TreeMenuItem( T item ) {
        this.item = item;
        this.children = new ArrayList<>();
    }
}
