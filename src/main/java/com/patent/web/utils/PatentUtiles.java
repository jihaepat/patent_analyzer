package com.patent.web.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


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

public class PatentUtiles {

	public static String jsonString(String value) {
		if (value == null)	return "";
		
		return value;
	}
	
	public static boolean isEmpty(String value) {
		return value == null || "".equals(value);
	}
	
	public static Integer getNumberParameter(HttpServletRequest request, String name) {
		String rt = request.getParameter(name);
		if (rt == null || "".equals(rt)) {
			return null;
		}
		
		Pattern p = Pattern.compile("(^[0-9]*$)");
		if (p.matcher(rt).find()) {
			return Integer.parseInt(rt);
		}
		
		return null;
	}
	
	public static List<Integer> getNumberParameters(HttpServletRequest request, String name) {
		List<Integer> rts = new ArrayList<>();
		
		String[] rt = request.getParameterValues(name);
		if (rt == null) {
			return rts;
		}
		
		
		Pattern p = Pattern.compile("(^[0-9]*$)");
		for (String tmp : rt) {
			if (p.matcher(tmp).find()) {
				rts.add(Integer.parseInt(tmp));
			}	
		}
		
		return rts;
	}

	static Pattern  pttnDate = Pattern.compile("[- /.]*");
	static DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyyMMdd");
	public static DateTime parseDateTime(String date) {
		Matcher mat = pttnDate.matcher(date);
		mat.reset(date);
		date = mat.replaceAll("");
		return formatter.parseDateTime(date);
	}
	
}

