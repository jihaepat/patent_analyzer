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
@Getter
@Setter
public class Index {
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
	        return false;
	    }
	    if (!Index.class.isAssignableFrom(obj.getClass())) {
	        return false;
	    }
	    try {
			final Index other = (Index) obj;
			if (this.key.intValue() == other.key.intValue()) {
				return true;
			}	
	    } catch (Exception e) {
	    }
		
		return false;
	}
	@Override
	public int hashCode() {
		return 159 + key;
	}
	
	Integer key;
	Integer iv;
	Float fv;
	String tableName;
	
	public Index key(Integer key) { this.key = key; return this; }
	public Index tableName(String tableName) { this.tableName = tableName; return this; }
}
