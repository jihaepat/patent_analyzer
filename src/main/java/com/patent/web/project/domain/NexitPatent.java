package com.patent.web.project.domain;

import java.util.List;

import com.patent.web.domain.Pair;

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

@Getter
@Setter
public class NexitPatent extends Patent {

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
	        return false;
	    }
	    if (!NexitPatent.class.isAssignableFrom(obj.getClass())) {
	        return false;
	    }
	    final NexitPatent other = (NexitPatent) obj;
	    if (this.APPNM.equals(other.getAPPNM()) && this.clss1 == other.clss1 && this.clss2 == other.clss2 && this.clss3 == other.clss3) {
	    	return true;
	    }
	    return false;
	}
	@Override
	public int hashCode() {
	    int hash = 159 + (this.APPNM != null ? this.APPNM.hashCode() : 0);
	    hash = hash * 53 + clss1;
	    if (clss2 != null) hash = hash * 53 + clss2;
	    if (clss3 != null) hash = hash * 53 + clss3;
	    return hash;
	}
	
	protected Integer prvId;
	protected Integer nxtId;
	
	protected Integer clss1;
	protected Integer clss2;
	protected Integer clss3;
	
	protected Integer fmly;
	protected Integer qtd;
	
	protected List<Pair<Integer, String>> clss2List;
	protected List<Pair<Integer, String>> clss3List;
}
