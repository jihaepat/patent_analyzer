package com.patent.web.domain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.patent.web.utils.PatentUtiles;

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

@Service
public class PageFactoryService extends PageParam {
	@Getter @Setter Integer totalsize;
	
	@Getter @Setter Integer frstpage;
	@Getter @Setter Integer lastpage;
	@Getter @Setter Integer totalpage;
	
	@Value("${pageable.pagegroup}")
	@Getter @Setter Integer pagegroup = 10;
	@Value("${pageable.onepagelstcnt}")
	Integer defaultonepagelstcnt = 10;
	
	@Getter @Setter Integer onepagelstcnt = 10;
	
	@Getter @Setter String search;
	
	public PageFactoryService info(PageParam param) throws Exception {
		if (param.getLimit() != null) {
			onepagelstcnt = param.getLimit();
		} else {
			onepagelstcnt = defaultonepagelstcnt;
		}
		if (!PatentUtiles.isEmpty(param.getSearch())) {
			search = param.getSearch();
		} else {
			search = null;
		}
		
		memberId = param.getMemberId();
		page = param.page+1;
		
		return this;
	}
	
	public PageFactoryService calc(PageParam param) throws Exception {
		info(param);
		return calc();
	}
	
	public PageFactoryService calc() throws Exception {
		frstpage = ((page - 1) / pagegroup) * pagegroup + 1;
		lastpage  = frstpage + pagegroup - 1;
		totalpage = totalsize / onepagelstcnt;
		if (totalsize % onepagelstcnt > 0) {
			totalpage++;
		}
		
		if (lastpage > totalpage) {
			lastpage = totalpage;
		}
		
		return this;
	}
	
	public Integer getOffset() {
		return (page-1) * onepagelstcnt;
	}
	
	@Override
	public String toString() {
		return String.format("[page=%d, totalsize=%d, offset=%d], [frstpage=%d, lastpage=%d, totalpage=%d], [pagegroup=%d, onepagelstcnt=%d]"
				, page, totalsize, getOffset(), frstpage, lastpage, totalpage, pagegroup, onepagelstcnt);
	}
}

