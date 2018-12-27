package com.patent.web.member.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.patent.dao.mapper.MemberMapper;
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

@Service
public class MemberService {
	
	@Autowired MemberMapper mapper;
	
	public Integer getMemberListSize() throws DataAccessException {
		return mapper.getMemberListSize();
	}
	
	public List<Member> getMemberList(PageFactoryService pageFactory) throws DataAccessException {
		return mapper.getMemberList(pageFactory);
	}
	
	public Member getMember(Member member) throws DataAccessException {
		return mapper.getMember(member);
	}
	
	public Member getMemberById(Integer id) throws DataAccessException {
		return mapper.getMemberById(id);
	}
	
	public Integer insertCheckIdMember(String data) throws DataAccessException {
		return mapper.insertCheckIdMember(data);
	}
	
	@Transactional
	public void insertMember(Member member) throws DataAccessException {
		mapper.insertMember(member);
	}
	
	@Transactional
	public void updateMember(Member member) throws DataAccessException {
		mapper.updateMember(member);
	}
}

