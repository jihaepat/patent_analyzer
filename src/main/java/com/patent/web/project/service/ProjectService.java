package com.patent.web.project.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.patent.dao.mapper.ProjectMapper;
import com.patent.web.chart.Dataset;
import com.patent.web.chart.Tableset;
import com.patent.web.domain.PageFactoryService;
import com.patent.web.domain.Pair;
import com.patent.web.domain.PatentFile;
import com.patent.web.project.domain.CLMN;
import com.patent.web.project.domain.Classification;
import com.patent.web.project.domain.ClassificationInfo;
import com.patent.web.project.domain.ClssTree;
import com.patent.web.project.domain.Index;
import com.patent.web.project.domain.NexitPatent;
import com.patent.web.project.domain.Patent;
import com.patent.web.project.domain.ProjectStep1;
import com.patent.web.project.domain.ProjectStep2;
import com.patent.web.project.domain.ProjectStep3;
import com.patent.web.project.domain.ProjectStep4;
import com.patent.web.project.domain.ProjectStep5;
import com.patent.web.project.domain.ProjectStep5Chart;
import com.patent.web.utils.ExcelReadUtiles;
import com.patent.web.utils.ExcelReadUtiles.EnRT;
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

@Service("projectService")
public class ProjectService {
	@Autowired ProjectMapper mapper;
	
	public void insertUnification(Integer id, String name, String unfcn) throws DataAccessException {
		mapper.insertUnification(id, name, unfcn);
	}
	
	
	
	public Integer getProjectListSize(Integer id) throws DataAccessException {
		return mapper.getProjectListSize(id);
	}
	
	public List<ProjectStep1> getProjectList(PageFactoryService info) throws DataAccessException {
		return mapper.getProjectList(info);
	}
	
	@Transactional
	public void insertProjectStep1(ProjectStep1 ps1) throws DataAccessException {
		mapper.createPatentTable(ps1);
		mapper.insertProjectStep1(ps1);
		mapper.insertProjectChart(ps1);
	}
	
	public Integer getProjectStatus(Integer projectId) throws DataAccessException {
		return mapper.getProjectStatus(projectId);
	}
	
	public void updateProjectStatus(Integer projectId, Integer status) throws DataAccessException {
		mapper.updateProjectStatus(projectId, status);
	}
	
	public List<CLMN> getClmnProject(Integer id) throws DataAccessException {
		return mapper.getClmnProject(id);
	}
	
	public List<Patent> getExcelPatentList(String tableName, int status, String search) throws DataAccessException {
		return mapper.getExcelPatentList(tableName, status, search);
	}
	
	public void deleteQtdProject(Integer id) throws DataAccessException {
		mapper.deleteQtdProject(id);
	}
	
	public void deleteNextPatent(String tableName) throws DataAccessException {
		mapper.deleteNextPatent(tableName);
	}
	
	/******************************************************************************************************************/
	// STEP1
	/******************************************************************************************************************/
	public ProjectStep1 getProjectInfo(Integer id) throws DataAccessException {
		return mapper.getProjectInfo(id);
	}
	
	public void updateProjectClassification(Integer id, Integer dept) throws DataAccessException {
		mapper.updateProjectClassification(id, dept);
	}

	@Transactional
	public void insertClassificationService(HttpServletRequest request, Classification cf, String filepath) throws Exception {
		MultipartHttpServletRequest mtprt = (MultipartHttpServletRequest) request;
		
		cf.setProjectId(PatentUtiles.getNumberParameter(mtprt, "projectId"));
		cf.setParentId(PatentUtiles.getNumberParameter(mtprt, "parentId"));
		cf.setId(PatentUtiles.getNumberParameter(mtprt, "id"));
		cf.setName(mtprt.getParameter("name"));
		cf.setDept(PatentUtiles.getNumberParameter(mtprt, "dept"));
		cf.setCode(mtprt.getParameter("code"));
		
		if (cf.getId() == null) {
			insertClassification(cf);
		} else {
			updateClassification(cf);
		}
		
		ClassificationInfo info = new ClassificationInfo();
		info.setId(cf.getId());
		info.setSearch(mtprt.getParameter("search"));
		info.setRemarks(mtprt.getParameter("remarks"));
		
		insertClassificationInfo(info);
		
		boolean isClear = false;
		String deleteFiles = request.getParameter("deleteFiles");
		if (!PatentUtiles.isEmpty(deleteFiles)) {
			isClear = true;
			ProjectStep1 ps1 = mapper.getProjectInfo(cf.getProjectId());
			String[] ids = deleteFiles.split(",");
			for (String id : ids) {
				deleteClassificationFile(ps1.getTableName(), Integer.parseInt(id.trim()));
			}	
		}
		
		Iterator<String> iterator = mtprt.getFileNames();
	    MultipartFile multipartFile = null;
	    while (iterator.hasNext()) {
	    	multipartFile = mtprt.getFile(iterator.next());
	        if(multipartFile.isEmpty() == false) {
	        	isClear = true;
	        	
	        	PatentFile pf = new PatentFile();
	        	pf.setProjectId(cf.getProjectId());
	        	pf.setParentId(cf.getId());
	        	pf.setOrgname(multipartFile.getOriginalFilename());
	        	pf.setSavename(UUID.randomUUID().toString());
	        	
	        	multipartFile.transferTo(new File(String.format("%s/%s", filepath, pf.getSavename())));
	        	
	        	insertClassificationFile(pf);
	        }
	    }
	    
	    if (isClear) {
	    	ProjectStep1 ps1 = mapper.getProjectInfo(cf.getProjectId());
			mapper.deleteAllPatent(ps1.getTableName());
			mapper.updateProjectStatus(cf.getProjectId(), 1);
	    }
	}
	
	public List<Classification> getClassification(Integer projectId) throws DataAccessException {
		return mapper.getClassification(projectId);
	}
	
	public  Classification getClassificationById(Integer id) throws DataAccessException {
		return mapper.getClassificationById(id);
	}
	
	public void insertClassification(Classification cf) throws DataAccessException {
		mapper.insertClassification(cf);
	}
	
	public void updateClassification(Classification cf) throws DataAccessException {
		mapper.updateClassification(cf);
	}
	
	@Transactional
	public void deleteAllClassification(Integer id) throws DataAccessException {
		mapper.deleteAllClassification(id);
	}
	
	@Transactional
	public void deleteClassification(Classification cf) throws DataAccessException {
		ProjectStep1 ps1 = mapper.getProjectInfo(cf.getProjectId());
		mapper.deleteAllPatent(ps1.getTableName());
		mapper.updateProjectStatus(cf.getProjectId(), 1);
		
		mapper.deleteClassification(cf);
	}
	
	public List<ClassificationInfo> getClassificationInfos(Integer id) throws DataAccessException {
		return mapper.getClassificationInfos(id);
	}
	
	public List<Patent> getPatents(String tableName, Integer id) throws DataAccessException {
		return mapper.getPatents(tableName, id);
	}
	
	public ClassificationInfo getClassificationInfo(Integer id) throws DataAccessException {
		return mapper.getClassificationInfo(id);
	}
	
	public void insertClassificationInfo(ClassificationInfo info) throws DataAccessException {
		mapper.insertClassificationInfo(info);
	}
	
	public List<PatentFile> getClassificationFiles(Integer id) throws DataAccessException {
		return mapper.getClassificationFiles(id);
	}
	
	public void insertClassificationFile(PatentFile file) throws DataAccessException {
		mapper.insertClassificationFile(file);
	}
	
	public void deleteClassificationFile(String tableName, Integer id) throws DataAccessException {
		mapper.deleteClassificationFile(id);
		mapper.deletePatentInfos(tableName, id);
	}
	
	public List<CLMN> getTotalClmns() throws DataAccessException {
		return mapper.getTotalClmns();
	}
	public List<CLMN> getTotalClmns2() throws DataAccessException {
		return mapper.getTotalClmns2();
	}
	
	public List<ClssTree> getClssTree(Integer id, Integer dept) throws DataAccessException {
		return mapper.getClssTree(id, dept);
	}
	
	public String getUnification(String APLCT) throws DataAccessException {
		return mapper.getUnification(APLCT);
	}
	
	@Transactional
	public void updateProjectStep1(ProjectStep1 ps1, String filepath) throws DataAccessException {
		mapper.updateProjectStep1(ps1);
		ps1 = mapper.getProjectInfo(ps1.getId());
		
		List<ClssTree> clssList = mapper.getClssTree(ps1.getId(), ps1.getClassification());
		List<CLMN> totalClmns = mapper.getTotalClmns();
		List<CLMN> iptClmns = new ArrayList<>();
		
		String aplct, tmp;
		StringBuffer strAplct;
		Map<String, Integer> strModyAplct = new HashMap<>();
		mapper.deleteAllUnificationPatent(ps1.getId());
		
		mapper.deletePatentInfos(ps1.getTableName(), -1);
		for (PatentFile file : mapper.getClassificationFilesByProjectId(ps1.getId())) {
			ExcelReadUtiles utils = new ExcelReadUtiles(file, filepath, ps1.getId(), totalClmns);
			
			mapper.deletePatentInfos(ps1.getTableName(), file.getId());
			for (CLMN clmn : utils.getClmns()) {
				if (!iptClmns.contains(clmn)) {
					iptClmns.add(clmn);
				}
			}
			for (Patent patent : utils.getList()) {
				patent.setProjectId(ps1.getId());
				patent.setFileId(file.getId());
				patent.setClssId(file.getParentId());
				
				for (ClssTree ct : clssList) {
					if (ct.getId().intValue() == patent.getClssId().intValue()) {
						patent.setStrclss1(ct.getClss1());
						patent.setStrclss2(ct.getClss2());
						patent.setStrclss3(ct.getClss3());
						break;
					}
				}
				patent.setTableName(ps1.getTableName());
				
				try {
					strAplct = new StringBuffer();
					aplct = patent.getAPLCT();
					patent.setOriAPLCT(aplct);
					patent.setModyCntAPLCT(0);
					for (String split : aplct.split("\\|")) {
						split = split.trim().toUpperCase();
						tmp = mapper.getUnification(split);
						if (strAplct.length() != 0) { strAplct.append(" | "); }
						if (tmp != null) {
							strAplct.append(tmp);
							if (strModyAplct.containsKey(tmp)) {
								Integer cnt = strModyAplct.get(tmp);
								strModyAplct.put(tmp, cnt+1);
							} else {
								strModyAplct.put(tmp, 1);
							}
							patent.setModyCntAPLCT(patent.getModyCntAPLCT()+1);
						} else {
							strAplct.append(split);
						}
					}
					patent.setAPLCT(strAplct.toString());
					mapper.insertIntoPatentInfo(patent);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			// 국가, 문헌종류, 특허번호중복 제거
			List<Patent> duplicate = mapper.getDuplicatePatentByClssId(ps1.getTableName(), file.getParentId());
			for (Patent p : duplicate)  {
				List<Patent> d2 = mapper.getDuplicatePatent2(p);
				Patent tmp2 = null;
				for (Patent p2 : d2) {
					if (tmp2 == null) tmp2 = p2;
					else {
						if (isEmpty(p2.getAPLCT()) && !isEmpty(tmp2.getAPLCT())) {
							mapper.updateDuplicatePatent(p2);
						} else if (isEmpty(tmp2.getAPLCT()) && !isEmpty(p2.getAPLCT())) {
							mapper.updateDuplicatePatent(tmp2);
							tmp2 = p2;
						} else {
							EnRT tmpRt = ExcelReadUtiles.checkDCT(ps1.getBulletin(), tmp2.getDCT(), p2.getDCT());
							if (tmpRt == EnRT.T) {
								if (isEmpty(p2.getAPLCT()) && !isEmpty(tmp2.getAPLCT())) {
									mapper.updateDuplicatePatent(p2);
								} else {
									mapper.updateDuplicatePatent(tmp2);
									tmp2 = p2;
								}
							} else if (tmpRt == EnRT.F) {
								mapper.updateDuplicatePatent(p2);
							}
						}
					}
				}
			}
		}
		for (Map.Entry<String, Integer> entry : strModyAplct.entrySet()) {
			mapper.insertUnificationPatent(ps1.getId(), entry.getKey(), entry.getValue());	
		}
		
		int i=0;
		mapper.deletePatentClmns(ps1.getId());
		for (CLMN clmn : iptClmns) {
			if (clmn != null) {
				mapper.insertPatentClmns(ps1.getId(), clmn.getId(), i++);
			}
		}
	}
	boolean isEmpty(String aplct) {
		if (aplct == null || (aplct != null && aplct.isEmpty())) {
			return true;
		}
		return false;
	}
	public List<Patent> getDuplicatePatentForClssId(String tableName) throws DataAccessException {
		return mapper.getDuplicatePatentForClssId(tableName);
	}
	public List<Patent> getDuplicatePatentForClssId2(Patent p) throws DataAccessException {
		return mapper.getDuplicatePatentForClssId2(p);
	}
	
	public Map<Patent, List<Patent>> getDuplicatePatentByAppnm(ProjectStep1 ps1) throws DataAccessException {
		ps1 = mapper.getProjectInfo(ps1.getId());
		Map<Patent, List<Patent>> map = new HashMap<>();
		
//		for (Patent patent : mapper.getDuplicatePatentAppnm(ps1.getTableName())) {
		for (Patent p : mapper.getDuplicatePatentForClssId(ps1.getTableName())) {
			List<Patent> list = new ArrayList<>();
			for (Patent p2 : mapper.getDuplicatePatentForClssId2(p)) {
				if (p2.getRMKS() != null && !p2.getRMKS().isEmpty()) {
					p.setRMKS(p2.getRMKS());
				}
				list.add(p2);
			}
			
			map.put(p, list);
		}
		
		return map;
	}
	
	public void updateProjectDuplicateAppnm(Integer projectId, Integer duplicate, String aplct) throws DataAccessException {
		mapper.updateProjectDuplicateAppnm(projectId, duplicate, aplct);
	}
	
	public void updateDuplicatePatentByAppnm(String tableName, List<Integer> ids) throws DataAccessException {
		mapper.updateDuplicatePatentByAppnm(tableName, ids);
	}
	
	public void updateClassificationFileDone(Integer projectId) throws DataAccessException {
		mapper.updateClassificationFileDone(projectId);
	}
	
	public Integer getDuplicateProject(String tableName) throws DataAccessException {
		return mapper.getDuplicateProject(tableName);
	}
	
	public Integer getUnificationProjectCnt(Integer id) throws DataAccessException {
		return mapper.getUnificationProjectCnt(id);
	}
	
	public List<String> getUnificationProject(Integer id) throws DataAccessException {
		return mapper.getUnificationProject(id);
	}
	
	public Integer getDuplicatePatentListSize(PageFactoryService pageFactory) throws DataAccessException {
		return mapper.getDuplicatePatentListSize(pageFactory);
	}
	public List<Patent> getDuplicatePatentList(PageFactoryService pageFactory) throws DataAccessException {
		return mapper.getDuplicatePatentList(pageFactory);
	}
	
	
	/******************************************************************************************************************/
	// STEP2
	/******************************************************************************************************************/
	public ProjectStep2 getProjectInfo2(Integer id) throws DataAccessException {
		return mapper.getProjectInfo2(id);
	}
	
	public void updateProjectStep2(ProjectStep2 ps2) throws DataAccessException {
		mapper.updateProjectStep2(ps2);
	}
	
	public List<Patent> getTmpNoiseList(String tableName, List<Integer> list) throws DataAccessException {
		return mapper.getTmpNoiseList(tableName, list);
	}
	
	public Patent getTmpNoiseInfo(String tableName, Integer id) throws DataAccessException {
		return mapper.getTmpNoiseInfo(tableName, id);
	}
	
	public void updateNoiseCompleate(String tableName, List<Integer> list, Integer lang) throws DataAccessException {
		mapper.updateNoiseCompleate(tableName, list, lang);
	}
	
	public void updateStep2Check(int id) throws DataAccessException {
		mapper.updateStep2Check(id);
	}
	
	public List<Patent> getNoisePatents(String tableName, Integer ln) throws DataAccessException {
		return mapper.getNoisePatents(tableName, ln);
	}
	
	public void clearNoise(String tableName) throws DataAccessException {
		mapper.clearNoise(tableName);
	}
	
	public void updateNoise(String tableName, List<Integer> ids) throws DataAccessException {
		mapper.updateNoise(tableName, ids);
	}
	
	public Integer getFinalNoisePatentListSize(PageFactoryService info) throws DataAccessException {
		return mapper.getFinalNoisePatentListSize(info);
	}
	
	public List<Patent> getFinalNoisePatentList(PageFactoryService info) throws DataAccessException {
		return mapper.getFinalNoisePatentList(info);
	}
	
	/******************************************************************************************************************/
	// STEP3
	/******************************************************************************************************************/
	public ProjectStep3 getProjectInfo3(Integer id) throws DataAccessException {
		return mapper.getProjectInfo3(id);
	}
	
	public void updateProjectStep3(ProjectStep3 ps3) throws DataAccessException {
		mapper.updateProjectStep3(ps3);
	}
	
	public List<Pair<Integer, String>> getFirstClassification(Integer id) throws DataAccessException {
		return mapper.getFirstClassification(id);
	}
	public List<Pair<Integer, String>> getChildClassification(Integer pId, Integer id) throws DataAccessException {
		return mapper.getChildClassification(pId, id);
	}
	
	public void deleteOmissionPatent(String tableName, Integer status) throws DataAccessException {
		mapper.deleteOmissionPatent(tableName, status);
	}
	public void updateDeleteOmissionDuplicatePatent(String tableName) throws DataAccessException {
		mapper.updateDeleteOmissionDuplicatePatent(tableName);
	}
	
	public List<Patent> getTmpOmissionFamilyPatent(String tableName) throws DataAccessException {
		return mapper.getTmpOmissionFamilyPatent(tableName);
	}
	
	public int getCheckOmissionPatent(String tableName, String nat, String appnm, String appnm2) throws DataAccessException {
		return mapper.getCheckOmissionPatent(tableName, nat, appnm, appnm2);
	}
	
	public void insertOmissionFamilyPatent(String tableName) throws DataAccessException {
		mapper.insertOmissionFamilyPatent(tableName);
	}
	
	public List<Patent> getOmissionFamilyPatent(String tableName) throws DataAccessException {
		return mapper.getOmissionFamilyPatent(tableName);
	}
	
	public List<Patent> getTmpOmissionQuotedPatent(String tableName, Integer quoted) throws DataAccessException {
		return mapper.getTmpOmissionQuotedPatent(tableName, quoted);
	}
	
	public List<Patent> getOmissionCitationPatent(String tableName, Integer quoted) throws DataAccessException {
		return mapper.getOmissionCitationPatent(tableName, quoted);
	}
	
	public Integer getOmissionCitationPatentClssId(Patent patent) throws DataAccessException {
		return mapper.getOmissionCitationPatentClssId(patent);
	}
	
	public void insertTmpPatentInfo(NexitPatent patent) throws DataAccessException {
		mapper.insertTmpPatentInfo(patent);
	}
	
	public List<Pair<Integer, String>> getDefaultOmissionPatent(Integer id) throws DataAccessException {
		return mapper.getDefaultOmissionPatent(id);
	}
	
	public void updateDuplicateOmission(String tableName, List<Integer> list) throws DataAccessException {
		mapper.updateDuplicateOmission(tableName, list);
	}
	
	public void updateOmissionClssId(NexitPatent pt) throws DataAccessException {
		mapper.updateOmissionClssId(pt);
	}
	
	public Integer getFinalFmlyPatentSize(String tableName) throws DataAccessException {
		return mapper.getFinalFmlyPatentSize(tableName);
	}
	
	public Integer getFinalQtdPatentSize(String tableName) throws DataAccessException {
		return mapper.getFinalQtdPatentSize(tableName);
	}
	
	public Integer getIntegrityPatentListSize(PageFactoryService pageFactory) throws DataAccessException {
		return mapper.getIntegrityPatentListSize(pageFactory);
	}
	
	public List<Patent> getIntegrityPatentList(PageFactoryService pageFactory) throws DataAccessException {
		return mapper.getIntegrityPatentList(pageFactory); 
	}
	
	public NexitPatent getQtdPatent(String tableName, Integer page) throws DataAccessException {
		return mapper.getQtdPatent(tableName, page);
	}
	
	public Integer getQtdPatentTotalSize(String tableName) throws DataAccessException {
		return mapper.getQtdPatentTotalSize(tableName);
	}
	
	public String getClassificationNameById(Integer id) throws DataAccessException {
		if (id == null) return null;
		
		return mapper.getClassificationNameById(id);
	}
	public void updateQtdForm(Patent patent) throws DataAccessException {
		mapper.updateQtdForm(patent);
	}
	
	public void updateQtdComplete(Integer id) throws DataAccessException {
		mapper.updateQtdComplete(id);
	}
	
	public List<Patent> getDuplicateRemotePatent(Patent patent) throws DataAccessException {
		return mapper.getDuplicateRemotePatent(patent);
	}
	public void deleteUpdateDuplicatePatent(Patent patent) throws DataAccessException {
		mapper.deleteUpdateDuplicatePatent(patent);
	}
	/******************************************************************************************************************/
	// STEP4
	/******************************************************************************************************************/
	public ProjectStep4 getProjectInfo4(Integer id) throws DataAccessException {
		return mapper.getProjectInfo4(id);
	}
	
	public void updateProjectStep4(ProjectStep4 ps4) throws DataAccessException {
		mapper.updateProjectStep4(ps4);
	}
	
	public List<Patent> getCalcPatent(String tableName) throws DataAccessException {
		return mapper.getCalcPatent(tableName);
	}
	
	public List<Patent> getCalcIdxcntnrghtPatent(String indt, String tableName) throws DataAccessException {
		return mapper.getCalcIdxcntnrghtPatent(indt, tableName);
	}
	
	public List<Index> getCalcQtdPatent(String tableName) throws DataAccessException {
		return mapper.getCalcQtdPatent(tableName);
	}
	
	public List<Index> getCalcFmlyPatent(String tableName) throws DataAccessException {
		return mapper.getCalcFmlyPatent(tableName);
	}
	
	public List<Index> getCalcClnmPatent(String tableName) throws DataAccessException {
		return mapper.getCalcClnmPatent(tableName);
	}
	
	public void clearScr(String tableName) throws DataAccessException {
		mapper.clearScr(tableName);
	}
	public void updateCalcPatent(Patent patent) throws DataAccessException {
		mapper.updateCalcPatent(patent);
	}
	
	public List<Index> getRcmndPatent(Integer id, String tableName, Integer stnd, Integer numb, Integer scre) throws DataAccessException {
		return mapper.getRcmndPatent(id, tableName, stnd, numb, scre);
	}
	
	public void updateClearCalcRtPatent(String tableName) throws DataAccessException {
		mapper.updateClearCalcRtPatent(tableName);
	}
	public void updateCalcRtPatent(Index index) throws DataAccessException {
		mapper.updateCalcRtPatent(index);
	}
	
	public Integer getRcmndPatentListSize(PageFactoryService pageFactory) throws DataAccessException {
		return mapper.getRcmndPatentListSize(pageFactory);
	}
	
	public List<Patent> getRcmndPatentList(PageFactoryService pageFactory) throws DataAccessException {
		return mapper.getRcmndPatentList(pageFactory);
	}
	
	/******************************************************************************************************************/
	// STEP5
	/******************************************************************************************************************/
	public ProjectStep5 getProjectInfo5(Integer id) throws DataAccessException {
		return mapper.getProjectInfo5(id);
	}
	
	public Integer getPatentDateInfo(String tableName) throws DataAccessException {
		return mapper.getPatentDateInfo(tableName);
	}
	
	public ProjectStep5Chart getProjectInfo5Chart(Integer id) throws DataAccessException {
		return mapper.getProjectInfo5Chart(id);
	}
	
	public void updateProjectStep5(ProjectStep5 ps5) throws DataAccessException {
		mapper.updateProjectStep5(ps5);
	}
	
	public void updateProjectStep5File(ProjectStep5 ps5) throws DataAccessException {
		mapper.updateProjectStep5File(ps5);
	}
	
	public void updateProjectStep5Chart(ProjectStep5Chart ps5) throws DataAccessException {
		mapper.updateProjectStep5Chart(ps5);
	}
	
	


	
	public List<String> getIptNatList(String tableName) throws DataAccessException {
		return mapper.getIptNatList(tableName, -1);
	}
	
	public List<Dataset> getClssInfo(String tableName) throws DataAccessException {
		return mapper.getClssInfo(tableName, -1);
	}
	
	public String getTableName(Integer id) throws DataAccessException {
		return mapper.getTableName(id);
	}
	public Pair<Integer, Integer> getPatentYearInfo(String tableName) throws DataAccessException {
		return mapper.getPatentYearInfo(tableName, -1);
	}
	
	public Pair<Integer, Integer> getPatentYearInfoByClssId(String tableName, Integer clssId) throws DataAccessException {
		return mapper.getPatentYearInfoByClssId(tableName, clssId, -1);
	}
	
	public Pair<Integer, Integer> getPatentYearInfoByNation(String tableName, String nat) throws DataAccessException {
		return mapper.getPatentYearInfoByNation(tableName, nat, -1);
	}
	
	public List<Dataset> getIpt11(String tableName) throws DataAccessException {
		return mapper.getIpt11(tableName, -1);
	}
	
	public List<Dataset> getIpt12(String tableName) throws DataAccessException {
		return mapper.getIpt12(tableName, -1);
	}
	
	public List<String> getIpt13NatList(String tableName) throws DataAccessException {
		return mapper.getIpt13NatList(tableName, -1);
	} 
	
	public List<Dataset> getIpt13(String tableName, String nat) throws DataAccessException {
		return mapper.getIpt13(tableName, nat, -1);
	}
	
	public List<Dataset> getIpt21(String tableName) throws DataAccessException {
		return mapper.getIpt21(tableName, -1);
	}
	
	public Dataset getIpt22(String tableName, String nat) throws DataAccessException {
		return mapper.getIpt22(tableName, nat, -1);
	}
	
	public List<Dataset> getIpt23(String tableName, String nat) throws DataAccessException {
		return mapper.getIpt23(tableName, nat, -1);
	}
	
	public List<Dataset> getIpt24(String tableName, String nat) throws DataAccessException {
		return mapper.getIpt24(tableName, nat, -1);
	}
	
	public Dataset getIpt31(String tableName, String start, String finish) throws DataAccessException {
		return mapper.getIpt31(tableName, start, finish, -1);
	}
	
	public Dataset getIpt32(String tableName, String nat, String start, String finish) throws DataAccessException {
		return mapper.getIpt32(tableName, nat, start, finish, -1);
	}
	
	public List<Dataset> getIpt41(String tableName, String nat) throws DataAccessException {
		return mapper.getIpt41(tableName, nat, -1);
	}
	
	public List<Dataset> getIpt51(String tableName) throws DataAccessException {
		return mapper.getIpt51(tableName, -1);
	}
	
	public List<Dataset> getIpt51ClssInfo(String tableName) throws DataAccessException {
		return mapper.getIpt51ClssInfo(tableName, -1);
	}
	public List<Dataset> getIpt52(String tableName, Integer clssId, String nat) throws DataAccessException {
		return mapper.getIpt52(tableName, clssId, nat, -1);
	}
	
	public List<Dataset> getIpt61(String tableName, String start, String finish) throws DataAccessException {
		return mapper.getIpt61(tableName, start, finish, -1);
	}
	
	public List<Dataset> getIpt62(String tableName, String start, String finish) throws DataAccessException {
		return mapper.getIpt62(tableName, start, finish, -1);
	}
	
	public List<Dataset> getIpt71(String tableName, Integer clssId) throws DataAccessException {
		return mapper.getIpt71(tableName, clssId, -1);
	}
	
	public List<Tableset> getIpt81(String tableName, String param, Integer cnt) throws DataAccessException {
		return mapper.getIpt81(tableName, param, cnt, -1);
	}
	
	public List<Dataset> getIpt91(String tableName, Integer count) throws DataAccessException {
		return mapper.getIpt91(tableName, count, -1);
	}
	
	public List<Dataset> getIpt92(String tableName, Integer count) throws DataAccessException {
		return mapper.getIpt92(tableName, count, -1);
	}
	
	public List<Dataset> getIpt101(String tableName, String nat, Integer count) throws DataAccessException {
		return mapper.getIpt101(tableName, nat, count, -1);
	}
	
	public List<Dataset> getIpt102(String tableName, Integer clssId, Integer count) throws DataAccessException {
		return mapper.getIpt102(tableName, clssId, count, -1);
	}
}
