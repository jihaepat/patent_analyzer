package com.patent.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.dao.DataAccessException;

import com.patent.web.domain.PageFactoryService;
import com.patent.web.member.domain.Member;


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

public interface MemberMapper {
	Integer getMemberListSize() throws DataAccessException;
	List<Member> getMemberList(PageFactoryService pageFactory) throws DataAccessException;
	Member getMember(Member member) throws DataAccessException;
	Member getMemberById(@Param("id") Integer id) throws DataAccessException;
	
	Integer insertCheckIdMember(String data) throws DataAccessException;
	void insertMember(Member member) throws DataAccessException;
	void updateMember(Member member) throws DataAccessException;
}
