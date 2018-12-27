package com.patent.web.member;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.patent.web.domain.SimpleResult;
import com.patent.web.member.domain.LoginSession;
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
public class LoginController {
	
	@Autowired MemberService memberService;
	
	public static Member getMemberInfo(HttpSession session) throws Exception {
		LoginSession userSession = (LoginSession) session.getAttribute(LoginSession.login_session_name);
		if (userSession != null) {
			return userSession.getMember();
		}
		
		return null; 
	}
	
	
	@GetMapping("/login")
	public String login() {
		return "member/login";
	}
	
	@ResponseBody
	@PostMapping("/login")
	public SimpleResult login_action(HttpServletRequest request, HttpServletResponse response, Member member) throws Exception {
		try {
			Member tmp = memberService.getMember(member);
			
			if (tmp == null) {
				return new SimpleResult().state(1);
			} else {
				if ("0".equals(tmp.getUsrpass())) {
					return new SimpleResult().state(2);
				} else if (tmp.getGrade() == 0) {
					return new SimpleResult().state(3);
				} else {
					LoginSession session = new LoginSession();
					session.setMember(tmp);
					request.getSession().setAttribute(LoginSession.login_session_name, session);
				}
			}	
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleResult().failure().message(e.getMessage());
		}
		
		return new SimpleResult();
	}
	
	@GetMapping("/logout")
	public String logout(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.getSession().removeAttribute(LoginSession.login_session_name);
	    request.getSession().invalidate();
	    
	    return "member/login";
	}
}

