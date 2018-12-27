package com.patent.web.faq.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.patent.dao.mapper.FaqMapper;
import com.patent.web.domain.PageFactoryService;
import com.patent.web.faq.domain.Faq;


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
public class FaqService {
	
	@Autowired FaqMapper mapper;
	
	
	public Integer getTotalSize() throws DataAccessException {
		return mapper.getTotalSize();
	}
	
	public List<Faq> getList(PageFactoryService info) throws DataAccessException {
		return mapper.getList(info);
	}
	
	@Transactional
	public void question(Faq faq) throws DataAccessException {
		mapper.question(faq);
	}
	
	@Transactional
	public void updateQuestion(Faq faq) throws DataAccessException {
		mapper.updateQuestion(faq);
	}
	
	@Transactional
	public void answer(Faq faq) throws DataAccessException {
		mapper.answer(faq);
	}
	
	@Transactional
	public void delete(Faq faq) throws DataAccessException {
		mapper.delete(faq);
	}
}

