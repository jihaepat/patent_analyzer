package com.patent.web.member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
public class SignUpController {
	
	@Autowired MemberService service;
	
	@GetMapping("/concent")
	public String concent() {
		return "member/concent";
	}
	
	@PostMapping("/signup")
	public String signup(@RequestParam("adv") Integer adv, ModelMap map) {
		map.addAttribute("adv", adv);
		return "member/signup";
	}
	
	@ResponseBody
	@PostMapping("/insertMember")
	public SimpleResult insert(Member member) {
		try {
			service.insertMember(member);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new SimpleResult();
	}
	
	@ResponseBody
	@PostMapping("/check_id")
	public SimpleResult check_id(@RequestParam(value="id", required=false) String data) {
		try {
			Integer count = service.insertCheckIdMember(data);
			if (count == 0) {
				return new SimpleResult();		
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new SimpleResult().failure();
	}
}

