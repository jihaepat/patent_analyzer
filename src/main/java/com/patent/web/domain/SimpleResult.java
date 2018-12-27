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

public class SimpleResult {
	@Getter @Setter Boolean result;
	@Getter @Setter Integer state;
	@Getter @Setter String message;
	
	public SimpleResult() {
        this.result = Boolean.TRUE;
        this.state = -1;
        this.message = null;
    }
	
	public SimpleResult failure() {
		this.result =Boolean.FALSE;
		return this;
	}
	
	public SimpleResult state(Integer state) {
		this.state = state;
		return this;
	}
	
	public SimpleResult message(String message) {
		this.message = message;
		return this;
	}
	
	public SimpleResult nullMessage() {
		this.message = "{}";
		return this;
	}
	
	
	@Override
	public String toString() {
		return String.format("{result:%b, state:%d, message:%s}", result, state, message);
	}
}

