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
@Getter @Setter
public class CLMN {
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
	        return false;
	    }
	    if (!CLMN.class.isAssignableFrom(obj.getClass())) {
	        return false;
	    }
	    final CLMN other = (CLMN) obj;
	    if (this.id == other.getId() && this.name.equals(other.getName()) && this.mthd.equals(other.getName())) {
	    	return true;
	    }
	    return false;
	}
	@Override
	public int hashCode() {
		int hash = 3;
		hash += 53 * hash + Integer.toString(id).hashCode();
	    hash += 53 * hash + (this.name != null ? this.name.hashCode() : 0);
	    hash += 53 * hash + (this.mthd != null ? this.mthd.hashCode() : 0);
	    return hash;
	}
	
	
	int id;
	String name;
	String mthd;
	String type;
	int order;
}
