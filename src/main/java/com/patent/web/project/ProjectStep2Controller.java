package com.patent.web.project;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.patent.web.domain.PageFactoryService;
import com.patent.web.domain.PageParam;
import com.patent.web.domain.Pair;
import com.patent.web.domain.SimpleResult;
import com.patent.web.project.domain.ClassificationInfo;
import com.patent.web.project.domain.Patent;
import com.patent.web.project.domain.ProjectStep1;
import com.patent.web.project.domain.ProjectStep2;
import com.patent.web.project.service.ProjectService;
import com.patent.web.utils.GetPatent;


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
public class ProjectStep2Controller {
	
	@Autowired PageFactoryService pageFactory;
	@Autowired ProjectService service;
	@Autowired MessageSource message;
	@Value("${filepath}") String filepath;
	
	@Value("${patent.noise.uri}") String patentNoiseUri;

	
	@PostMapping("/project/step2")
	public String step1(ModelMap map, @RequestParam("id") Integer id) {
		map.addAttribute("menuProject", Boolean.TRUE);
		map.addAttribute("state", 2);
		map.addAttribute("projectId", id);
		map.addAttribute("projectinfo", service.getProjectInfo2(id));
		
		return "project/step2";
	}
	
	@ResponseBody
	@PostMapping("/project/updateStep2")
	public SimpleResult updateStep2(ProjectStep2 ps2) {
		ProjectStep1 ps1 = service.getProjectInfo(ps2.getId());
		
		service.updateProjectStatus(ps2.getId(), 4);
		service.clearNoise(ps1.getTableName());

		try {
			service.deleteQtdProject(ps2.getId());
			service.deleteNextPatent(ps1.getTableName());
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		
		Gson gson = new Gson();
		GetPatent getPatent = new GetPatent();
		
		try {
			List<ClassificationInfo> list = service.getClassificationInfos(ps2.getId());
			for (ClassificationInfo clss : list) {
				JsonObject object = new JsonObject();
				object.addProperty("search_expr", clss.getSearch());
				object.addProperty("query_sentence", clss.getRemarks());
				
				JsonArray array = new JsonArray();
				List<Patent> patents = service.getPatents(ps1.getTableName(), clss.getId());
				for (Patent patent : patents) {
					JsonObject tmp = new JsonObject();
					tmp.addProperty("id", patent.getId());
					tmp.addProperty("title", patent.getNMITN());
					tmp.addProperty("abstract", patent.getRMKS());
					tmp.addProperty("claim", patent.getRTVCLS());
					array.add(tmp);
				}
				object.add("patents", array);
				
				List<Integer> noiseIds = getPatent.getPatent(patentNoiseUri, gson.toJson(object));
				if (noiseIds.size() > 0)
				service.updateNoise(ps1.getTableName(), noiseIds);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleResult().failure().message(e.getMessage());
		}
		
		service.updateStep2Check(ps2.getId());
		return new SimpleResult();
	}
	
	@PostMapping("/project/step2/getNoiseInfo")
	public String getFinalNoiseInfo(ModelMap map, @RequestParam("id") Integer projectId) {
		ProjectStep1 ps1 = service.getProjectInfo(projectId);
		if (ps1.getIsNs().intValue() == 1) {
			pageFactory.setTableName(ps1.getTableName());
			
			Pair<Integer, Integer> noise = new Pair<>();
			noise.setV1(service.getDuplicatePatentListSize(pageFactory));
			noise.setV2(noise.getV1() - service.getFinalNoisePatentListSize(pageFactory));
	
			map.addAttribute("noise", noise);
			
			return "fragments/project :: noisePatent";
		} else {
			return "fragments/project :: stepFinalPatentEmpty";
		}
	}
	
	@PostMapping("/project/step2/getFinalNoise")
	public String getFinalNoiseList(ModelMap map, @RequestParam("id") Integer projectId, PageParam info) {
		ProjectStep1 ps1 = service.getProjectInfo(projectId);
		if (ps1.getIsNs().intValue() == 1) {
			try {
				pageFactory.setTableName(ps1.getTableName());
				
				Integer totalSize = service.getFinalNoisePatentListSize(pageFactory.info(info));
				pageFactory.setTotalsize(totalSize==0?1:totalSize);
				pageFactory.calc();
				
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
			map.addAttribute("pageable", pageFactory);
			map.addAttribute("list", service.getFinalNoisePatentList(pageFactory));
	
			return "fragments/project :: stepFinalPatentList";
		} else {
			return "fragments/project :: stepFinalPatentEmpty";
		}
	}
	
	@ResponseBody
	@PostMapping("/project/step2/finish")
	public SimpleResult finish(@RequestParam("id") Integer projectId) {
		ProjectStep1 ps1 = service.getProjectInfo(projectId);
		if (ps1.getStatus().intValue() < 5) {
			service.updateProjectStatus(projectId, 5);
		}
		
		return new SimpleResult();
	}
}

