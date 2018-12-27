package com.patent.web.faq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.patent.web.domain.PageFactoryService;
import com.patent.web.domain.PageParam;
import com.patent.web.domain.SimpleResult;
import com.patent.web.faq.domain.Faq;
import com.patent.web.faq.service.FaqService;


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

@Controller
public class FaqController {

	@Autowired FaqService service;
	
	@GetMapping("/faq")
	public String faq(ModelMap map, PageParam info) {
		map.addAttribute("menuFaq", Boolean.TRUE);
		return "faq/list";
	}
	
	@Autowired PageFactoryService pageFactory;
	
	@PostMapping("/faq/list")
	public String faqList(ModelMap map, PageParam info) {
		try {
			pageFactory.setTotalsize(service.getTotalSize());
			pageFactory.calc(info);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		map.addAttribute("list", service.getList(pageFactory));
		map.addAttribute("pageable", pageFactory);
		
		return "fragments/faq :: faq";
	}
	
	
	
	@ResponseBody
	@PostMapping("/faq/question")
	public SimpleResult question(Faq faq) {
		try {
			this.service.question(faq);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new SimpleResult();
	}
	
	@ResponseBody
	@PostMapping("/faq/answer")
	public SimpleResult answer(Faq faq) {
		try {
			this.service.answer(faq);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new SimpleResult();
	}
	
	@ResponseBody
	@PostMapping("/faq/delete")
	public SimpleResult delete(Faq faq) {
		try {
			this.service.delete(faq);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new SimpleResult();
	}
}

