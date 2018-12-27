package com.patent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


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

@SpringBootApplication
@ComponentScan("com.patent.*")
public class PatentApplication {

	public static void main(String[] args) {
		SpringApplication.run(PatentApplication.class, args);
	}

}
