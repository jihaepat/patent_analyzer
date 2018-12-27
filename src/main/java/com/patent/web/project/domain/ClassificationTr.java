package com.patent.web.project.domain;

import java.util.List;

import com.patent.web.domain.PatentFile;

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
public class ClassificationTr {

	@Getter @Setter
	ClassificationInfo info;
	
	@Getter @Setter
	ClassificationInfo dept1;
	@Getter Boolean isCountDept1 = Boolean.FALSE;
	
	@Getter @Setter
	ClassificationInfo dept2;
	@Getter Boolean isCountDept2 = Boolean.FALSE;
	
	@Getter @Setter
	ClassificationInfo dept3;
	
	
	public void setDept1Value(Integer v1, String v2, String v3) {
		if (dept1 == null) {
			dept1 = new ClassificationInfo();
		}
		
		dept1.setId(v1);
		dept1.setName(v2);
		dept1.setCode(v3);
	}
	public void setDept1Count() {
		if (!isCountDept1) {
			isCountDept1 = Boolean.TRUE;
			dept1.setId(1);
		} else {
			dept1.setId(dept1.getId()+1);
		}
	}
	
	public void setDept2Value(Integer v1, String v2, String v3) {
		if (dept2 == null) {
			dept2 = new ClassificationInfo();
		}
		
		dept2.setId(v1);
		dept2.setName(v2);
		dept2.setCode(v3);
	}
	public void setDept2Count() {
		if (!isCountDept2) {
			isCountDept2 = Boolean.TRUE;
			dept2.setId(1);
		} else {
			dept2.setId(dept2.getId()+1);
		}
	}
	
	public void setDept3Value(Integer v1, String v2, String v3) {
		if (dept3 == null) {
			dept3 = new ClassificationInfo();
		}
		
		dept3.setId(v1);
		dept3.setName(v2);
		dept3.setCode(v3);
	}
	
	public void setInfo(String search, String remarks, List<PatentFile> files) {
		if (info == null) {
			info = new ClassificationInfo();
		}
		info.setSearch(search);
		info.setRemarks(remarks);
		info.setFiles(files);
	}
}

