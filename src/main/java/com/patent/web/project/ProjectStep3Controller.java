package com.patent.web.project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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
import com.patent.web.domain.Pair;
import com.patent.web.domain.SimpleResult;
import com.patent.web.project.domain.CLMN;
import com.patent.web.project.domain.ClssTree;
import com.patent.web.project.domain.NexitPatent;
import com.patent.web.project.domain.Patent;
import com.patent.web.project.domain.ProjectStep1;
import com.patent.web.project.domain.ProjectStep3;
import com.patent.web.project.service.ProjectService;
import com.patent.web.utils.ExcelReadUtiles;
import com.patent.web.utils.ExcelReadUtiles.EnRT;
import com.patent.web.utils.GetPatent;
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
public class ProjectStep3Controller {
	
	@Autowired PageFactoryService pageFactory;
	@Autowired ProjectService service;
	@Autowired MessageSource message;
	@Value("${filepath}") String filepath;
	
	@PostMapping("/project/step3")
	public String step1(ModelMap map, @RequestParam("id") Integer id) {
		map.addAttribute("menuProject", Boolean.TRUE);
		map.addAttribute("state", 3);
		map.addAttribute("projectId", id);
		map.addAttribute("projectinfo", service.getProjectInfo3(id));
		
		return "project/step3";
	}
	
	@ResponseBody
	@PostMapping("/project/updateStep3")
	public SimpleResult updateStep2(ProjectStep3 ps3) {
		service.updateProjectStep3(ps3);
		
		return new SimpleResult();
	}
	
	@Value("${patent.api.uri}")
	String apiUri;
	
	@PostMapping("/project/step3/integrityModal")
	public String integrityModal(ModelMap map, @RequestParam("id") Integer id, @RequestParam("quoted") Integer quoted) {
		ProjectStep1 ps1 = service.getProjectInfo(id);
		
		int fmListSize = 0;
		int cttnListSize = 0;
		List<CLMN> totalClmns = service.getTotalClmns2();
		
		Map<String, Integer> strModyAplct = new HashMap<>();
		
		boolean hasError = false;
		
		GetPatent getPatent = new GetPatent(totalClmns);
		service.deleteOmissionPatent(ps1.getTableName(), ps1.getStatus());
		service.updateDeleteOmissionDuplicatePatent(ps1.getTableName());
		if (ps1.getStatus() < 6) {
			try {
				List<Patent> tmpList = service.getTmpOmissionFamilyPatent(ps1.getTableName());
				for (Patent pt : tmpList) {
					List<NexitPatent> patentList = getPatent.getPatent(String.format(apiUri, pt.getNAT(), pt.getAPPNM()));
					
					// 다른 특허번호로 검색이 나올 수 있음.
					// 같은 국가,특허번호로 묶음.
					Map<Pair<String, String>, List<NexitPatent>> mapList = new HashMap<>();
					Iterator<NexitPatent> it = patentList.iterator();
					while (it.hasNext()) {
						NexitPatent patent = it.next();
						Pair<String, String> key = new Pair<>();
						if (!mapList.containsKey(key)) {
							mapList.put(key, new ArrayList<>());
							
						}
						mapList.get(key).add(patent);
					}
					
					// 등록공보/공개공보/등록,공개공보 중복 삭제.
					Patent tmp2 = null;
					Iterator<Pair<String, String>> keys = mapList.keySet().iterator();
					while (keys.hasNext()) { 
						Iterator<NexitPatent> npit = mapList.get(keys.next()).iterator();
						while (npit.hasNext()) {
							NexitPatent patent = npit.next();
							if (patent.getDCT() == null) continue;
							
							if (tmp2 == null) tmp2 = patent;
							else {
								EnRT tmpRt = ExcelReadUtiles.checkDCT(ps1.getBulletin(), tmp2.getDCT(), patent.getDCT());
								if (tmpRt == EnRT.F) {
									npit.remove();
								}
							}
						}
					}
					
					// 중복체크
					keys = mapList.keySet().iterator();
					while (keys.hasNext()) { 
						Iterator<NexitPatent> npit = mapList.get(keys.next()).iterator();
						while (npit.hasNext()) {
							NexitPatent patent = npit.next();
							patent.setTableName(ps1.getTableName());
							List<Patent> dplicateList = service.getDuplicateRemotePatent(patent);
							for (Patent tmp : dplicateList) {
								EnRT tmpRt = ExcelReadUtiles.checkDCT(ps1.getBulletin(), tmp.getDCT(), patent.getDCT());
								if (tmpRt == EnRT.T) {
									tmp.setTableName(ps1.getTableName());
									service.deleteUpdateDuplicatePatent(tmp);
								} else if (tmpRt == EnRT.F) {
									npit.remove();
									break;
								}
							}
						}
					}
					
					// 등록
					keys = mapList.keySet().iterator();
					while (keys.hasNext()) {
						for (NexitPatent patent : mapList.get(keys.next())) {
						
							patent.setTableName(ps1.getTableName());
							patent.setProjectId(ps1.getId());
							patent.setFileId(-1);
							patent.setFmly(1);
							patent.setQtd(0);
							
							List<Pair<Integer, String>> clss = service.getDefaultOmissionPatent(pt.getClssId());
							Pair<Integer, String> tmp;
							for (int i=0; i<clss.size(); i++) {
								tmp = clss.get(i);
								if (tmp != null) {
									if (i==0) { 
										patent.setClssId(tmp.getV1());
										patent.setClss1(tmp.getV1());
										patent.setStrclss1(tmp.getV2());
									} else if (i==1) { 
										patent.setClssId(tmp.getV1());
										patent.setClss2(tmp.getV1());
										patent.setStrclss2(tmp.getV2());
									} else if (i==2) { 
										patent.setClssId(tmp.getV1());
										patent.setClss3(tmp.getV1());
										patent.setStrclss3(tmp.getV2());
									}
								}
							}
							
							//유니피케이션
							try {
								if (patent.getAPLCT() != null) {
									StringBuffer strAplct = new StringBuffer();
									String aplct = patent.getAPLCT();
									patent.setOriAPLCT(aplct);
									patent.setModyCntAPLCT(0);
									for (String split : aplct.split("\\|")) {
										String aplct2 = service.getUnification(split.trim());
										if (strAplct.length() != 0) { strAplct.append(" | "); }
										if (aplct2 != null) {
											strAplct.append(aplct2);
											if (strModyAplct.containsKey(aplct2)) {
												Integer cnt = strModyAplct.get(aplct2);
												strModyAplct.put(aplct2, cnt+1);
											} else {
												strModyAplct.put(aplct2, 1);
											}
											patent.setModyCntAPLCT(patent.getModyCntAPLCT()+1);
										} else {
											strAplct.append(split.trim());
										}
									}
									patent.setAPLCT(strAplct.toString());
								}
								
								service.insertTmpPatentInfo(patent);
							} catch (Exception e) {
								e.printStackTrace();
							}
							
							fmListSize++;
						}
					}
				}

				service.updateProjectStatus(id, 6);
			} catch (Exception e) {
				hasError = true;
				map.addAttribute("error", e.getMessage());
			}
		}
		
		if (!hasError) {
			try {
				
				List<Patent> tmpList = service.getTmpOmissionQuotedPatent(ps1.getTableName(), quoted);
				for (Patent pt : tmpList) {
					pt.setTableName(ps1.getTableName());
					List<NexitPatent> patentList = getPatent.getPatent(String.format(apiUri, pt.getNAT(), pt.getAPPNM()));
					
					// 다른 특허번호로 검색이 나올 수 있음.
					// 같은 국가,특허번호로 묶음.
					Map<Pair<String, String>, List<NexitPatent>> mapList = new HashMap<>();
					Iterator<NexitPatent> it = patentList.iterator();
					while (it.hasNext()) {
						NexitPatent patent = it.next();
						Pair<String, String> key = new Pair<>();
						if (!mapList.containsKey(key)) {
							mapList.put(key, new ArrayList<>());
							
						}
						mapList.get(key).add(patent);
					}
					
					// 등록공보/공개공보/등록,공개공보 중복 삭제.
					Patent tmp2 = null;
					Iterator<Pair<String, String>> keys = mapList.keySet().iterator();
					while (keys.hasNext()) { 
						Iterator<NexitPatent> npit = mapList.get(keys.next()).iterator();
						while (npit.hasNext()) {
							NexitPatent patent = npit.next();
							if (patent.getDCT() == null) continue;
							
							if (tmp2 == null) tmp2 = patent;
							else {
								EnRT tmpRt = ExcelReadUtiles.checkDCT(ps1.getBulletin(), tmp2.getDCT(), patent.getDCT());
								if (tmpRt == EnRT.F) {
									npit.remove();
								}
							}
						}
					}
					
					// 중복체크
					keys = mapList.keySet().iterator();
					while (keys.hasNext()) { 
						Iterator<NexitPatent> npit = mapList.get(keys.next()).iterator();
						while (npit.hasNext()) {
							NexitPatent patent = npit.next();
							patent.setTableName(ps1.getTableName());
							List<Patent> dplicateList = service.getDuplicateRemotePatent(patent);
							for (Patent tmp : dplicateList) {
								EnRT tmpRt = ExcelReadUtiles.checkDCT(ps1.getBulletin(), tmp.getDCT(), patent.getDCT());
								if (tmpRt == EnRT.T) {
									tmp.setTableName(ps1.getTableName());
									service.deleteUpdateDuplicatePatent(tmp);
								} else if (tmpRt == EnRT.F) {
									npit.remove();
									break;
								}
							}
						}
					}
					
					// 등록
					keys = mapList.keySet().iterator();
					while (keys.hasNext()) {
						for (NexitPatent patent : mapList.get(keys.next())) {
							
							patent.setTableName(ps1.getTableName());
							patent.setProjectId(ps1.getId());
							patent.setFileId(-1);
							patent.setFmly(0);
							patent.setQtd(1);
							
							List<Pair<Integer, String>> clss = service.getDefaultOmissionPatent(pt.getClssId());
							Pair<Integer, String> tmp;
							for (int i=0; i<clss.size(); i++) {
								tmp = clss.get(i);
								if (tmp != null) {
									if (i==0) { 
										patent.setClssId(tmp.getV1());
										patent.setClss1(tmp.getV1());
										patent.setStrclss1(tmp.getV2());
									} else if (i==1) { 
										patent.setClssId(tmp.getV1());
										patent.setClss2(tmp.getV1());
										patent.setStrclss2(tmp.getV2());
									} else if (i==2) { 
										patent.setClssId(tmp.getV1());
										patent.setClss3(tmp.getV1());
										patent.setStrclss3(tmp.getV2());
									}
								}
							}
							
							//유니피케이션
							try {
								if (patent.getAPLCT() != null) {
									StringBuffer strAplct = new StringBuffer();
									String aplct = patent.getAPLCT();
									patent.setOriAPLCT(aplct);
									patent.setModyCntAPLCT(0);
									for (String split : aplct.split("\\|")) {
										String aplct2 = service.getUnification(split.trim());
										if (strAplct.length() != 0) { strAplct.append(" | "); }
										if (aplct2 != null) {
											strAplct.append(aplct2);
											if (strModyAplct.containsKey(aplct2)) {
												Integer cnt = strModyAplct.get(aplct2);
												strModyAplct.put(aplct2, cnt+1);
											} else {
												strModyAplct.put(aplct2, 1);
											}
											patent.setModyCntAPLCT(patent.getModyCntAPLCT()+1);
										} else {
											strAplct.append(split.trim());
										}
									}
									patent.setAPLCT(strAplct.toString());
								}
								
								service.insertTmpPatentInfo(patent);
							} catch (Exception e) {
								e.printStackTrace();
							}
							cttnListSize++;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				hasError = true;
				map.addAttribute("error", e.getMessage());
			}
		}
		
		map.addAttribute("hasError", hasError);
		
		map.addAttribute("projectId", id);
		map.addAttribute("fmListSize", fmListSize);
		map.addAttribute("qtdListSize", cttnListSize);
		map.addAttribute("clf", ps1.getClassification());
		map.addAttribute("frstCls", service.getFirstClassification(id));
		
		return "fragments/project :: integrityForm";
	}
	
	
	@PostMapping("/project/step3/getQtd")
	public String qtdPatent(ModelMap map, @RequestParam("id") Integer id, @RequestParam("page") Integer page) {
		try {
			ProjectStep1 ps1 = service.getProjectInfo(id);
			
			NexitPatent patent = service.getQtdPatent(ps1.getTableName(), page);
			if (patent == null) {
				patent = new NexitPatent();
			}
			patent.setProjectId(id);
			if (ps1.getClassification() == 1) {
				patent.setClss1(patent.getClss3());
			} else if (ps1.getClassification() == 2) { 
				patent.setClss1(patent.getClss2());
				patent.setClss2(patent.getClss3());
				patent.setClss2List(service.getChildClassification(id, patent.getClss1()));
			} else if (ps1.getClassification() == 3) {
				patent.setClss2List(service.getChildClassification(id, patent.getClss1()));
				patent.setClss3List(service.getChildClassification(id, patent.getClss2()));
			}
			
			map.addAttribute("pg", page);
			map.addAttribute("ttlPg", service.getQtdPatentTotalSize(ps1.getTableName()));
			map.addAttribute("data", patent);
			map.addAttribute("clf", ps1.getClassification());
			map.addAttribute("frstCls", service.getFirstClassification(id));	
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "fragments/project :: qutedForm";
	}
	
	@ResponseBody
	@PostMapping("/project/step3/updateQtdForm")
	public SimpleResult updateQtdForm(NexitPatent patent) {
		
		try {
			ProjectStep1 ps1 = service.getProjectInfo(patent.getProjectId());
			
			patent.setTableName(ps1.getTableName());
			patent.setStrclss1(service.getClassificationNameById(patent.getClss1()));
			patent.setStrclss2(service.getClassificationNameById(patent.getClss2()));
			patent.setStrclss3(service.getClassificationNameById(patent.getClss3()));
			
			if (patent.getClss3() != null) patent.setClssId(patent.getClss3());
			else if (patent.getClss2() != null) patent.setClssId(patent.getClss2());
			else if (patent.getClss1() != null) patent.setClssId(patent.getClss1());
			
			service.updateQtdForm(patent);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new SimpleResult();
	}
	
	@ResponseBody
	@PostMapping("/project/step3/getClassification")
	public SimpleResult getClf(ModelMap map, @RequestParam("pi") Integer pi, @RequestParam("id") Integer id) {
		List<Pair<Integer, String>> list = service.getChildClassification(pi, id);
		return new SimpleResult().message(list.toString());
	}
	
	
	@ResponseBody
	@PostMapping("/project/step3/updateOmission")
	public SimpleResult updateOmission(HttpServletRequest req) {
		String psId = req.getParameter("psId");
		if (!PatentUtiles.isEmpty(psId)) {

			String fmlSize = req.getParameter("fmsize");
			String qtdSize = req.getParameter("qtsize");
			List<NexitPatent> list = new ArrayList<>();
			List<Integer> duplicateList = new ArrayList<>();
			
			ProjectStep1 ps1 = service.getProjectInfo(Integer.parseInt(psId));
			
			List<ClssTree> clssList = service.getClssTree(ps1.getId(), ps1.getClassification());
			
			if (!PatentUtiles.isEmpty(fmlSize)) {
				Integer fmlsize = Integer.parseInt(fmlSize);
				for (int i=0; i<fmlsize; i++) {
					if (!PatentUtiles.isEmpty(req.getParameter("fmid"+i))) {
						NexitPatent np = setPatent(clssList, req.getParameter("fmid"+i), req.getParameter("fmappnm"+i), req.getParameter("fm1"+i), req.getParameter("fm2"+i), req.getParameter("fm3"+i));
						if (!list.contains(np)) {
							list.add(np);
						} else {
							duplicateList.add(np.getId());
						}	
					}
				}
			}
			
			if (!PatentUtiles.isEmpty(qtdSize)) {
				Integer qtdsize = Integer.parseInt(qtdSize);
				for (int i=0; i<qtdsize; i++) {
					NexitPatent np = setPatent(clssList, req.getParameter("qtid"+i), req.getParameter("qtappnm"+i), req.getParameter("qt1"+i), req.getParameter("qt2"+i), req.getParameter("qt3"+i));
					if (!list.contains(np)) {
						list.add(np);
					} else {
						duplicateList.add(np.getId());
					}
				}
			}
			
			if (duplicateList.size() > 0) {
				service.updateDuplicateOmission(ps1.getTableName(), duplicateList);
			}
			for(NexitPatent pt : list) {
				pt.setTableName(ps1.getTableName());
				service.updateOmissionClssId(pt);
			}
		}
		
		return new SimpleResult();
	}
	
	NexitPatent setPatent(List<ClssTree> clssList, String id, String appnm, String clss1, String clss2, String clss3) {
		NexitPatent patent = new NexitPatent();
		
		int clssId = Integer.parseInt(clss1);
		patent.setId(Integer.parseInt(id));
		patent.setAPPNM(appnm);
		patent.setClss1(clssId);
		if (!PatentUtiles.isEmpty(clss2)) {
			clssId = Integer.parseInt(clss2);
			patent.setClss2(clssId);
		}
		if (!PatentUtiles.isEmpty(clss3)) {
			clssId= Integer.parseInt(clss3);
			patent.setClss3(clssId);
		}
		
		patent.setClssId(clssId);
		for (ClssTree tree : clssList) {
			if (tree.getId().intValue() == clssId) {
				patent.setStrclss1(tree.getClss1());
				if (!PatentUtiles.isEmpty(tree.getClss2())) {
					patent.setStrclss2(tree.getClss2());
				}
				if (!PatentUtiles.isEmpty(tree.getClss3())) {
					patent.setStrclss3(tree.getClss3());
				}
			}
		}
		
		return patent;
	}
	
	@ResponseBody
	@PostMapping("/project/step3/updateQtdComplete")
	public SimpleResult updateQtdComplete(ModelMap map, @RequestParam("id") Integer projectId) {
		try {
			service.updateQtdComplete(projectId);
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleResult().failure();
		}
		
		return new SimpleResult();
	}
	
	@PostMapping("/project/step3/getIntegrityInfo")
	public String getIntegrityPatentInfo(ModelMap map, @RequestParam("id") Integer projectId) {
		ProjectStep1 ps1 = service.getProjectInfo(projectId);
		if (ps1.getIsQtd().intValue() == 1) {
			pageFactory.setTableName(ps1.getTableName());
			
			map.addAttribute("total", service.getFinalNoisePatentListSize(pageFactory));
			map.addAttribute("fmly", service.getFinalFmlyPatentSize(ps1.getTableName()));
			map.addAttribute("qtd", service.getFinalQtdPatentSize(ps1.getTableName()));
		
			return "fragments/project :: integrityPatent";
		} else {
			return "fragments/project :: stepFinalPatentEmpty";
		}
	}
	
	@PostMapping("/project/step3/getIntegrityPatentList")
	public String getIntegrityPatentList(ModelMap map, @RequestParam("id") Integer projectId, PageParam info) {
		ProjectStep1 ps1 = service.getProjectInfo(projectId);
		
		if (ps1.getIsQtd().intValue() == 1) {
			try {
				pageFactory.setTableName(ps1.getTableName());
				
				Integer totalSize = service.getIntegrityPatentListSize(pageFactory.info(info));
				pageFactory.setTotalsize(totalSize==0?1:totalSize);
				pageFactory.calc();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
			map.addAttribute("pageable", pageFactory);
			map.addAttribute("list", service.getIntegrityPatentList(pageFactory));
			
			return "fragments/project :: stepFinalPatentList";
		} else {
			return "fragments/project :: stepFinalPatentEmpty";
		}
	}
	
	@ResponseBody
	@PostMapping("/project/step3/finish")
	public SimpleResult finish(@RequestParam("id") Integer projectId) {
		ProjectStep1 ps1 = service.getProjectInfo(projectId);
		
		if (ps1.getStatus().intValue() < 7) {
			service.updateProjectStatus(projectId, 7);
		}
		return new SimpleResult();
	}
}

