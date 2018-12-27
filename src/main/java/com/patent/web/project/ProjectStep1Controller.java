package com.patent.web.project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.patent.web.domain.PageFactoryService;
import com.patent.web.domain.PageParam;
import com.patent.web.domain.SimpleResult;
import com.patent.web.project.domain.Classification;
import com.patent.web.project.domain.ClassificationInfo;
import com.patent.web.project.domain.ClassificationTr;
import com.patent.web.project.domain.Patent;
import com.patent.web.project.domain.ProjectStep1;
import com.patent.web.project.domain.TreeItem;
import com.patent.web.project.domain.TreeMenuItem;
import com.patent.web.project.service.ProjectService;
import com.patent.web.utils.PatentUtiles;


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
public class ProjectStep1Controller {
	
	@Autowired PageFactoryService pageFactory;
	@Autowired ProjectService service;
	@Autowired MessageSource message;
	@Value("${filepath}") String filepath;
	
	@PostMapping("/project/step1")
	public String step1(ModelMap map, @RequestParam("id") Integer id) {
		map.addAttribute("menuProject", Boolean.TRUE);
		map.addAttribute("state", 1);
		map.addAttribute("projectId", id);
		map.addAttribute("projectinfo", service.getProjectInfo(id));
		
		return "project/step1";
	}
	
	@PostMapping("/project/step1/classificationInfo")
	public String classificationForm(ModelMap map, @RequestParam("id") Integer projectId, @RequestParam("di") Integer deptId, HttpServletResponse response) {
		
		List<Classification> list = service.getClassification(projectId);
		List<ClassificationTr> table = new ArrayList<>();
		
		try {
			List<Classification> dept1List = list.stream().filter(it -> it.getParentId() == -1).collect(Collectors.toList());
			for (int i=0; i<dept1List.size(); i++) {
				ClassificationTr tr = new ClassificationTr();
				Classification dept1 = dept1List.get(i);
				
				table.add(tr);
				tr.setDept1Value(dept1.getId(), dept1.getName(), dept1.getCode());
				
				List<Classification> dept2List = list.stream().filter(it -> it.getParentId().intValue() == dept1.getId().intValue()).collect(Collectors.toList());
				if (deptId == 1) {
					if (dept2List.size() > 0) {
						map.addAttribute("ERROR", message.getMessage("dept2.error_on", null, response.getLocale()));
						return "fragments/project :: classificationForm";
					} else {
						setClassificatinInfo(tr, dept1.getId());
					}
				} else if (deptId >= 2 && dept2List.size() == 0) {
					map.addAttribute("ERROR", message.getMessage("dept2.error_no", null, response.getLocale()));
					return "fragments/project :: classificationForm";
				}
				
				for (int j=0; j<dept2List.size(); j++) {
					Classification dept2 = dept2List.get(j);
					ClassificationTr tr1 = tr;
					
					if (j !=0 ) {
						tr1 = new ClassificationTr();
						table.add(tr1);
					}
					
					tr.setDept1Count();
					tr1.setDept2Value(dept2.getId(), dept2.getName(), dept2.getCode());
					
					List<Classification> dept3List = list.stream().filter(it -> it.getParentId().intValue() == dept2.getId().intValue()).collect(Collectors.toList());
					if (deptId == 2) {
						if (dept3List.size() > 0) {
							map.addAttribute("ERROR", message.getMessage("dept3.error_no", null, response.getLocale()));
							return "fragments/project :: classificationForm";
						} else {
							setClassificatinInfo(tr1, dept2.getId());	
						}
					} else if (deptId == 3 && dept3List.size() == 0) {
						map.addAttribute("ERROR", message.getMessage("dept3.error_no", null, response.getLocale()));
						return "fragments/project :: classificationForm";
					} else if (deptId != 3 && dept3List.size() > 0){
						map.addAttribute("ERROR", message.getMessage("dept3.error_on", null, response.getLocale()));
						return "fragments/project :: classificationForm";
					}
					
					for (int k=0; k<dept3List.size(); k++) {
						Classification dept3 = dept3List.get(k);
						ClassificationTr tr2 = tr1;
						
						if (k !=0 ) {
							tr.setDept1Count();
							tr2 = new ClassificationTr();
							table.add(tr2);
						}
						
						tr1.setDept2Count();
						tr2.setDept3Value(dept3.getId(), dept3.getName(), dept3.getCode());
						setClassificatinInfo(tr2, dept3.getId());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		map.addAttribute("table", table);
		return "fragments/project :: classificationForm";
	}
	void setClassificatinInfo(ClassificationTr ct, Integer id) throws Exception {
		ClassificationInfo ci = service.getClassificationInfo(id);
		if (ci != null) {
			ct.setInfo(ci.getSearch(), ci.getRemarks(), service.getClassificationFiles(id));
		} else {
			ct.setInfo("", "", service.getClassificationFiles(id));
		}
	}
	
	
	@SuppressWarnings({ "unchecked" })
	@PostMapping("/project/step1/classificationTable")
	public String classificationTable(ModelMap map, @RequestParam("id") Integer projectId) {
		List<TreeMenuItem<TreeItem>> tree = new ArrayList<>();
		TreeItem root = new TreeItem();
		root.setId(-1);
		root.setValue("ROOT");
		tree.add( new TreeMenuItem<>(root) );
		
		List<Classification> list = service.getClassification(projectId);
		List<Classification> dept1List = list.stream().filter(it -> it.getParentId().intValue() == -1).collect(Collectors.toList());
		for (int i=0; i<dept1List.size(); i++) {
			Classification dept1 = dept1List.get(i);
			getTreeParentItem(tree, null, null).getChildren().add( new TreeMenuItem<>(getTreeItem(dept1)) );
			
			List<Classification> dept2List = list.stream().filter(it -> it.getParentId().intValue() == dept1.getId().intValue()).collect(Collectors.toList());
			for (int j=0; j<dept2List.size(); j++) {
				Classification dept2 = dept2List.get(j);
				
				getTreeParentItem(tree, i, null).getChildren().add( new TreeMenuItem<>(getTreeItem(dept2)) );
				List<Classification> dept3List = list.stream().filter(it -> it.getParentId().intValue() == dept2.getId().intValue()).collect(Collectors.toList());
				for (int k=0; k<dept3List.size(); k++) {
					Classification dept3 = dept3List.get(k);
					
					getTreeParentItem(tree, i, j).getChildren().add( getTreeItem(dept3) );
				}
			}
		}
		
		map.addAttribute("data", tree);
        return "fragments/project :: classificationTable";
	}
	@SuppressWarnings("rawtypes")
	TreeMenuItem getTreeParentItem(List<TreeMenuItem<TreeItem>> tree, Integer i, Integer j) {
		TreeMenuItem root = tree.get(0);
		if (i == null) {
			return root;
		} else if (j == null) {
			return (TreeMenuItem) root.getChildren().get(i);
		} else {
			return (TreeMenuItem) ((TreeMenuItem) root.getChildren().get(i)).getChildren().get(j);
		}
	}
	TreeItem getTreeItem(Classification cf) {
		TreeItem tree = new TreeItem();
		tree.setId(cf.getId());
		tree.setValue(cf.getName());
		tree.setCode(cf.getCode());
		return tree;
	}
	
	@ResponseBody
	@PostMapping("/project/step1/insertClassification")
	public SimpleResult insertClassification(HttpServletRequest request) {
		Classification cf = new Classification();
		
		try {
			service.insertClassificationService(request, cf, filepath);
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleResult().failure();	
		}
		
		return new SimpleResult().message(cf.toString());
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@ResponseBody
	@PostMapping("/project/step1/getClassification")
	public Map getClassification(@RequestParam("id") Integer id) {
		ClassificationInfo ci = service.getClassificationInfo(id);
		if (ci != null) {
			ci.setFiles(service.getClassificationFiles(id));
			
			return ci.toJSON(Boolean.TRUE);
//			return new SimpleResult().message(ci.toJSON());
		} else {
//			return new SimpleResult().nullMessage();
			Map map = new HashMap();
			map.put("result", Boolean.FALSE);
			map.put("message", "");
			return map;
		}
	}	
	@ResponseBody
	@PostMapping("/project/step1/deleteAllClassification")
	public SimpleResult deleteAllClassification(@RequestParam("id") Integer id, @RequestParam("dept") Integer dept) {
		
		try {
			service.updateProjectClassification(id, dept);
			service.deleteAllClassification(id);
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleResult().failure();
		}
		
		return new SimpleResult();
	}
	
	@ResponseBody
	@PostMapping("/project/step1/deleteClassification")
	public SimpleResult deleteClassification(Classification cf) {
		try {
			service.deleteClassification(cf);
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleResult().failure();	
		}
		
		return new SimpleResult().message(cf.toString());
	}
	
	@ResponseBody
	@PostMapping("/project/step1/updateStep1")
	public SimpleResult updateStep1(ProjectStep1 ps1, HttpSession session) {
		
		try {
			service.updateProjectStep1(ps1, filepath);
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleResult().failure();
		}
		
		return new SimpleResult().message(String.format("{\"id\":%d}", ps1.getId()));
	}
	
	@PostMapping("/project/step1/checkDuplicateAppnm")
	public String checkDuplicateAppnm(ModelMap map, ProjectStep1 ps1) {
		Map<Patent, List<Patent>> list = service.getDuplicatePatentByAppnm(ps1);
		map.addAttribute("map", list);
        return "fragments/project :: duplicatePatentForm";
	}
	
	@ResponseBody
	@PostMapping("/project/step1/updateDuplicateAppnm")
	public SimpleResult updateDuplicateAppnm(HttpServletRequest request) {
		try {
			Integer projectId = PatentUtiles.getNumberParameter(request, "projectId");
			List<Integer> ids = PatentUtiles.getNumberParameters(request, "arr[]");

			ProjectStep1 ps1 = service.getProjectInfo(projectId);
			service.updateProjectDuplicateAppnm(ps1.getId(), ids.size(), "");
			if (ids.size() > 0) {
				service.updateDuplicatePatentByAppnm(ps1.getTableName(), ids);	
			}
			service.updateClassificationFileDone(projectId);
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleResult().failure();
		}
		
		return new SimpleResult();
	}
	
	@PostMapping("/project/step1/getDuplicatePatent")
	public String getDuplicatePatent(ModelMap map, @RequestParam("id") Integer projectId, HttpServletResponse response) {
		try {
			StringBuffer unificationString = new StringBuffer();
			ProjectStep1 ps1 = service.getProjectInfo(projectId);
			
			List<String> list = service.getUnificationProject(projectId);
			for (String tmp : list) {
				if (unificationString.length() != 0) { unificationString.append(", "); }
				if (tmp != null) {
					unificationString.append(tmp);
				}
			}
			Integer cnt = service.getUnificationProjectCnt(projectId);
			if (cnt != null) {
				if (cnt.intValue() > 5) {
					unificationString.append("등 ");
				}
			} else {
				cnt = 0;
			}
		
			map.addAttribute("dpcnt", service.getDuplicateProject(ps1.getTableName()));
			map.addAttribute("unisize", cnt);
			map.addAttribute("unistring", unificationString.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "fragments/project :: duplicatePatent";
	}
	
	@PostMapping("/project/step1/getDuplicatePatentList")
	public String getDuplicatePatentList(ModelMap map, @RequestParam("id") Integer projectId, PageParam info) {
		ProjectStep1 ps1 = service.getProjectInfo(projectId);
		
		try {
			pageFactory.setTableName(ps1.getTableName());
			
			Integer totalSize = service.getDuplicatePatentListSize(pageFactory.info(info));
			pageFactory.setTotalsize(totalSize==0?1:totalSize);
			pageFactory.calc();
			
			map.addAttribute("pageable", pageFactory);
			map.addAttribute("list", service.getDuplicatePatentList(pageFactory.calc()));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return "fragments/project :: stepFinalPatentList";
	}
	
	@ResponseBody
	@PostMapping("/project/step1/finish")
	public SimpleResult finish(@RequestParam("id") Integer projectId) {
		ProjectStep1 ps1 = service.getProjectInfo(projectId);
		if (ps1.getStatus().intValue() < 3) {
			service.updateProjectStatus(projectId, 3);
		}
		
		return new SimpleResult();
	}
}

