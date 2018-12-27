package com.patent.dao.mapper;

import java.util.List;

import org.springframework.dao.DataAccessException;

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

public interface FaqMapper {
	
	Integer getTotalSize() throws DataAccessException;
	List<Faq> getList(PageFactoryService info) throws DataAccessException;
	
	void question(Faq faq) throws DataAccessException;
	void updateQuestion(Faq faq) throws DataAccessException;
	void answer(Faq faq) throws DataAccessException;
	void delete(Faq faq) throws DataAccessException;
}
