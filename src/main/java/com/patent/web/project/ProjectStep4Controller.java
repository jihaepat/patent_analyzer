package com.patent.web.project;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.patent.web.domain.PageFactoryService;
import com.patent.web.domain.PageParam;
import com.patent.web.domain.SimpleResult;
import com.patent.web.project.domain.Index;
import com.patent.web.project.domain.Patent;
import com.patent.web.project.domain.ProjectStep1;
import com.patent.web.project.domain.ProjectStep4;
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
public class ProjectStep4Controller {
	
	@Autowired PageFactoryService pageFactory;
	@Autowired ProjectService service;
	@Autowired MessageSource message;
	
	@PostMapping("/project/step4")
	public String step1(ModelMap map, @RequestParam("id") Integer id) {
		map.addAttribute("menuProject", Boolean.TRUE);
		map.addAttribute("state", 4);
		map.addAttribute("projectId", id);
		map.addAttribute("projectinfo", service.getProjectInfo4(id));
		
		return "project/step4";
	}
	
	@ResponseBody
	@PostMapping("/project/updateStep4")
	public SimpleResult updateStep4(ProjectStep4 ps4) {
		try {
			service.updateProjectStep4(ps4);
			ps4 = service.getProjectInfo4(ps4.getId());
			
			service.clearScr(ps4.getTableName());
			
//			DateTime startDt = PatentUtiles.parseDateTime(ps4.getRegDate());
			List<Patent> list = service.getCalcPatent(ps4.getTableName());
			List<Patent> idxList = service.getCalcIdxcntnrghtPatent(ps4.getRegDate(), ps4.getTableName());
			List<Index> qtdList = service.getCalcQtdPatent(ps4.getTableName());
			List<Index> fmlyList = service.getCalcFmlyPatent(ps4.getTableName());
			List<Index> clnmList = service.getCalcClnmPatent(ps4.getTableName());
			
			int tmp = -1; Index index = new Index();
//			Duration duration; 
//			float result; 
			for (Patent pt : list) {
				pt.setTableName(ps4.getTableName());
				
				if (!PatentUtiles.isEmpty(pt.getRGSTDT())) {
					pt.setIdxrght(10F);
					boolean isFind = false;
					for (Patent tp : idxList) {
						if (tp.getId().intValue() == pt.getId().intValue()) {
							isFind = true;
							pt.setIdxcntnrght(tp.getIdxcntnrght());
							break;
						}
					}
					
					if (!isFind) {
						pt.setIdxcntnrght(0f);
					}
					
//					if (!PatentUtiles.isEmpty(pt.getDTRL())) {
//						duration = new Duration(PatentUtiles.parseDateTime(pt.getDTRL()), startDt);
//						result = maxDTRL - (float) duration.getStandardDays();
//						pt.setIdxrght((result>0f)?result:0f);	
//					} else {
//						pt.setIdxrght(0F);	
//					}
				} else {
					pt.setIdxcntnrght(0F);
					pt.setIdxrght(0F);
				}
				pt.setScr(pt.getIdxcntnrght()*ps4.getIdxcntnrght());
				pt.setScr(pt.getScr() + pt.getIdxrght()*ps4.getIdxrght());
				
				if (!PatentUtiles.isEmpty(pt.getPRTNM())) {
					pt.setIdxprrty(10F);
				} else {
					pt.setIdxprrty(0F);
				}
				pt.setScr(pt.getScr() + pt.getIdxprrty()*ps4.getIdxprrty());
				
				if (qtdList != null) {
					tmp = qtdList.indexOf(index.key(pt.getId()));
					if (tmp != -1) {
						Float tmpd = qtdList.get(tmp).getFv();
						pt.setIdxqtd(tmpd);
					} else {
						pt.setIdxqtd(0F);
					}
				} else {
					pt.setIdxqtd(0F);
				}
				pt.setScr(pt.getScr() + pt.getIdxqtd()*ps4.getIdxqtd());
				
				if (fmlyList != null && (tmp = fmlyList.indexOf(index.key(pt.getId()))) != -1) {
					pt.setIdxfmly(fmlyList.get(tmp).getIv().floatValue());
				} else {
					pt.setIdxfmly(0F);
				}
				pt.setScr(pt.getScr() + pt.getIdxfmly()*ps4.getIdxfmly());
				
				if (clnmList != null && (tmp = clnmList.indexOf(index.key(pt.getId()))) != -1) {
					pt.setIdxclm(clnmList.get(tmp).getFv());
				} else {
					pt.setIdxclm(0F);
				}
				pt.setScr(pt.getScr() + pt.getIdxclm()*ps4.getIdxclm());
				
				service.updateCalcPatent(pt);
			}
			
			service.updateClearCalcRtPatent(ps4.getTableName());
			List<Index> rcmmList = service.getRcmndPatent(ps4.getId(), ps4.getTableName(), ps4.getStnd(), ps4.getNumb(), ps4.getScre());
			for (Index idx : rcmmList) {
				service.updateCalcRtPatent(idx.tableName(ps4.getTableName()));
			}
		
			service.updateProjectStatus(ps4.getId(), 8);
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleResult().failure();
		}
		
		return new SimpleResult();
	}
	
	static float maxDTRL = 7300F;
	
	@PostMapping("/project/step4/getRecommendPatentList")
	public String getRecommendPatentList(ModelMap map, @RequestParam("id") Integer projectId, PageParam info) {
		ProjectStep1 ps1 = service.getProjectInfo(projectId);
		
		try {
			pageFactory.setTableName(ps1.getTableName());
			
			Integer totalSize = service.getRcmndPatentListSize(pageFactory.info(info));
			pageFactory.setTotalsize(totalSize==0?1:totalSize);
			pageFactory.calc();
			
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		map.addAttribute("pageable", pageFactory);
		map.addAttribute("list", service.getRcmndPatentList(pageFactory));
		return "fragments/project :: step4FinalPatentList";
	}
	
	@ResponseBody
	@PostMapping("/project/step4/finish")
	public SimpleResult finish(@RequestParam("id") Integer projectId) {
		ProjectStep1 ps1 = service.getProjectInfo(projectId);
		if (ps1.getStatus().intValue() < 9) {
			service.updateProjectStatus(projectId, 9);
		}
		return new SimpleResult();
	}
}

