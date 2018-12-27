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

public class Pair<T, T1> {
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
	        return false;
	    }
	    if (!Pair.class.isAssignableFrom(obj.getClass())) {
	        return false;
	    }
		final Pair<T, T1> other = (Pair<T, T1>) obj;
		if (v1 instanceof Integer && v2 instanceof String) {
			Integer tmpI = (Integer) v1, tmpI2 = (Integer) other.v1;
			String tmpS = (String) v2, tmpS2 = (String) other.v2;
			if (tmpI == null || tmpI2 == null || tmpS == null || tmpS2 == null) {
				return false;
			}
			
			if (tmpI.intValue() == tmpI2.intValue() && tmpS.equals(tmpS2)) {
				return true;
			}
		} else if (v1 instanceof String && v2 instanceof String) {
			String tmpI = (String) v1, tmpI2 = (String) other.v1;
			String tmpS = (String) v2, tmpS2 = (String) other.v2;
			if (tmpI == null || tmpI2 == null || tmpS == null || tmpS2 == null) {
				return false;
			}
			
			if (tmpI.equals(tmpI2) && tmpS.equals(tmpS2)) {
				return true;
			}
		}
		
		return false;
	}
	@Override
	public int hashCode() {
	    int hash = 3;
	    if (v1 instanceof Integer) {
	    	hash = hash * 3 + ((Integer) v1).intValue();
	    } else if (v1 instanceof String) {
	    	hash = hash * 3 + ((String) v1).hashCode();
	    }
	    if (v2 instanceof String) {
	    	hash = hash * 3 + ((String) v2).hashCode();
	    }
	    
	    return hash;
	}
	
	public boolean isEmpty() {
		return v1 == null || v2 == null;
	}
	
	
	@Getter @Setter
	T v1;
	
	@Getter @Setter
	T1 v2;
	
	@Override
	public String toString() {
		if (v1 instanceof Integer && v2 instanceof String)
			return String.format("{\"v1\":%d,\"v2\":\"%s\"}", v1, v2);
		else if (v1 instanceof String && v2 instanceof String) 
			return String.format("{\"v1\":\"%s\",\"v2\":\"%s\"}", v1, v2);
		
		return "";
	}
}
