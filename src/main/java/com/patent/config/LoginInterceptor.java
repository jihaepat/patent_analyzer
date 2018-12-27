package com.patent.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.WebUtils;

import com.patent.web.member.domain.LoginSession;


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

public class LoginInterceptor extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)  throws Exception {
		String url = request.getServletPath();

		if ("ok".equals(request.getParameter("mok"))) {
			return true;
		}
		
		if (url.contains("/error")) {
            return true;
        }
		
		if (
				url.equals("/")
				|| url.contains("/test")
				|| url.contains("/main") || url.contains("/sub") || url.contains("/topmenu") 
				|| url.contains("/project/updateStep5/download/") || url.contains("/project/downloadExcel/")
				|| url.contains("/faq")|| url.contains("/guide")
				|| url.contains("/js/") || url.contains("/css/") || url.contains("/font-awesome/") || url.contains("/images")
				|| url.contains("/concent") 
				|| url.contains("/signup") || url.contains("/signup_action") || url.contains("/check_id") || url.contains("/insertMember")
			) {
			return true;
		}
		
		if (url.contains("step2/put"))	return true;
		
		LoginSession session = (LoginSession) WebUtils.getSessionAttribute(request, LoginSession.login_session_name);
		if (session == null) {
			if (!url.contains("/login")) {
				response.setStatus(999);
				return false;
			}
		} else {
			if (url.contains("/login")) {
				response.setStatus(998);
				return false;
			}
		}
		
		return true;
	}

}

