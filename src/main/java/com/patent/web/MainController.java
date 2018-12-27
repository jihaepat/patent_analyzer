package com.patent.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


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
public class MainController {

	@GetMapping(value = {"/", "", "/main"})
	public String index() {
		return "index";
	}
	
	@GetMapping("/sub")
	public String main() {
		return "main";
	}
	
	@GetMapping("/topmenu")
	public String topmenu() {
		return "fragments/menu :: topmenu";
	}
	
}
