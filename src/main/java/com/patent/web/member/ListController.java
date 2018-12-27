package com.patent.web.member;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.patent.web.domain.PageFactoryService;
import com.patent.web.domain.PageParam;
import com.patent.web.domain.SimpleResult;
import com.patent.web.member.domain.Member;
import com.patent.web.member.service.MemberService;


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
public class ListController {
	
	@Autowired PageFactoryService pageFactory;
	@Autowired MemberService service;
	
	@GetMapping("/manager")
	public String manager(ModelMap map) {
		map.addAttribute("menuManager", Boolean.TRUE);
		return "member/list";
	}
	
	@PostMapping("/manager/list")
	public String list(ModelMap map, PageParam info, HttpSession session) {
		try {
			if (LoginController.getMemberInfo(session).getGrade() != 10) {
				return "fragments/error :: error";
			}
			
			Integer totalSize = service.getMemberListSize();
			pageFactory.setTotalsize(totalSize==0?1:totalSize);
			pageFactory.calc(info);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		List<Member> list = service.getMemberList(pageFactory);
		map.addAttribute("list", list);
		map.addAttribute("pageable", pageFactory);
		
		return "fragments/member :: list";
	}
	
	@PostMapping("/manager/info")
	public String info(ModelMap map, @RequestParam(value="id", required=false) Integer id, HttpSession session) {
		try {
			if (id == null || id == -1) {
				id = LoginController.getMemberInfo(session).getId();
			} else {
				if (LoginController.getMemberInfo(session).getGrade() != 10) {
					return "fragments/error :: error";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		map.addAttribute("info", service.getMemberById(id));
		return "fragments/member :: memberForm";
	}
	
	@ResponseBody
	@PostMapping("/manager/update")
	public SimpleResult update(Member member) {
		
		try {
			service.updateMember(member);
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleResult().failure();
		}
		
		return new SimpleResult();
	}
}
