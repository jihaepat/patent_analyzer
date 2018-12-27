package com.patent.web.project;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.patent.web.chart.PtntChartFactory;
import com.patent.web.domain.PageFactoryService;
import com.patent.web.domain.PageParam;
import com.patent.web.domain.SimpleResult;
import com.patent.web.member.LoginController;
import com.patent.web.project.domain.CLMN;
import com.patent.web.project.domain.Patent;
import com.patent.web.project.domain.ProjectStep1;
import com.patent.web.project.service.ProjectService;
import com.patent.web.utils.ExcelWriteUtiles;


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
public class ProjectController {
	
	@Autowired PageFactoryService pageFactory;
	@Autowired ProjectService service;
	
	@GetMapping("/project")
	public String main(ModelMap map) {
		map.addAttribute("menuProject", Boolean.TRUE);
		return "project/main";
	}
	
	@PostMapping("/project/list")
	public String list(ModelMap map, PageParam info, HttpSession session) {
		try {
			info.setMemberId(LoginController.getMemberInfo(session).getId());
			
			Integer totalSize = service.getProjectListSize(LoginController.getMemberInfo(session).getId());
			pageFactory.setTotalsize(totalSize==0?1:totalSize);
			pageFactory.calc(info);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		List<ProjectStep1> list = service.getProjectList(pageFactory);
		map.addAttribute("list", list);
		map.addAttribute("pageable", pageFactory);
		
		return "fragments/project :: mainList";
	}
	
	@PostMapping("/project/step")
	public String step1(ModelMap map, @RequestParam("id") Integer id, @RequestParam(value="sts", required=false, defaultValue="-1") Integer sts) {
		map.addAttribute("menuProject", Boolean.TRUE);
		map.addAttribute("projectId", id);
		
		if (sts == -1) {
			sts = service.getProjectStatus(id);
		}
		
		if (sts.intValue() <= 2) {
			map.addAttribute("state", 1);
			ProjectStep1 ps1 = service.getProjectInfo(id);
			ps1.setStatus(sts);
			map.addAttribute("projectinfo", ps1);
			
			return "project/step1";
		} else if (sts.intValue() <= 4) {
			map.addAttribute("state", 2);
			map.addAttribute("projectinfo", service.getProjectInfo2(id));
			
			return "project/step2";
		} else if (sts.intValue() <= 6) {
			map.addAttribute("state", 3);
			map.addAttribute("projectinfo", service.getProjectInfo3(id));
			
			return "project/step3";
		} else if (sts.intValue() <= 8) {
			map.addAttribute("state", 4);
			map.addAttribute("projectinfo", service.getProjectInfo4(id));
			
			return "project/step4";
		} else {
			ProjectStep1 ps1 = service.getProjectInfo(id);
			
			map.addAttribute("state", 5);
			map.addAttribute("projectinfo", service.getProjectInfo5(id));
			map.addAttribute("dtinfo", service.getPatentDateInfo(ps1.getTableName()));
			
			return "project/step5";
		}
	}
	
	@ResponseBody
	@PostMapping("/project/insertPorject")
	public SimpleResult insertProject(ProjectStep1 ps1, HttpSession session) {
		
		try {
			ps1.setTableName(String.format("PTNT_%s", UUID.randomUUID().toString().replaceAll("-", "_")));
			ps1.setMemberId(LoginController.getMemberInfo(session).getId());
			
			service.insertProjectStep1(ps1);
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleResult().failure();
		}
		
		return new SimpleResult().message(String.format("{\"id\":%d}", ps1.getId()));
	}
	
	@Value("${tmpfilepath}")
	String tmpfilepath;
	
	@ResponseBody
	@PostMapping("/project/downloadExcel/{id}/{status}")
    public void downloadEquipMeasurement(HttpServletRequest req, HttpServletResponse res, @PathVariable Integer id, @PathVariable Integer status) {
		ProjectStep1 ps1 = service.getProjectInfo(id);
		
		String serarch = req.getParameter("search");
		try {
			List<Patent> list = service.getExcelPatentList(ps1.getTableName(), status, serarch);
			List<CLMN> clmns = service.getClmnProject(id);

			ExcelWriteUtiles write = new ExcelWriteUtiles(ps1.getClassification(), list, clmns);
			String filefullpath = write.makeFile(tmpfilepath);
			
			if (filefullpath != null) {
				PtntChartFactory.downloadExcel(req, res, filefullpath, String.format("EXCEL_%d.xlsx", status));
			}
		} catch (Exception e) {
			try {
	    		Thread.sleep(1000);
	    	} catch (Exception e2) {
	    	}
			
			PtntChartFactory.downErrorAlert(res, e.getMessage() );
		}
	}
}

