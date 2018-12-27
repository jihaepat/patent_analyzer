package com.patent.web.member.domain;

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

@Getter
@Setter
@ToString
public class Member {
	Integer rownum;
	Integer id;
	String usrid;
	String usrpass;
	String name;
	String position;
	String mailaddr;
	String phone;
	String regdt;
	Integer grade;
	Integer adv;
}

