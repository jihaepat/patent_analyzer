package com.patent.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.dao.DataAccessException;

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

public interface ProjectMapper {
	
	void insertUnification(@Param("id") Integer id, @Param("name") String name, @Param("unfcn") String unfcn) throws DataAccessException;
	
	
	Integer getProjectListSize(Integer id) throws DataAccessException;
	List<ProjectStep1> getProjectList(PageFactoryService info) throws DataAccessException;
	void createPatentTable(ProjectStep1 ps) throws DataAccessException;
	void insertProjectStep1(ProjectStep1 ps) throws DataAccessException;
	void insertProjectChart(ProjectStep1 ps) throws DataAccessException;
	Integer getProjectStatus(@Param("projectId") Integer projectId) throws DataAccessException;
	void updateProjectStatus(@Param("projectId") Integer projectId, @Param("status") Integer status) throws DataAccessException;
	List<CLMN> getClmnProject(@Param("id") Integer id) throws DataAccessException;
	
	List<Patent> getExcelPatentList(@Param("tableName") String tableName, @Param("status") int status, @Param("search") String search) throws DataAccessException;
	void deleteQtdProject(@Param("id") Integer id) throws DataAccessException;
	void deleteNextPatent(@Param("tableName") String tableName) throws DataAccessException;
	/******************************************************************************************************************/
	// STEP1
	/******************************************************************************************************************/
	ProjectStep1 getProjectInfo(Integer id) throws DataAccessException;
	void updateProjectClassification(@Param("id") Integer id, @Param("dept") Integer dept) throws DataAccessException;
	
	List<Classification> getClassification(Integer projectId) throws DataAccessException;
	Classification getClassificationById(Integer id) throws DataAccessException;
	void insertClassification(Classification cf) throws DataAccessException;
	void updateClassification(Classification cf) throws DataAccessException;
	void deleteAllClassification(Integer id) throws DataAccessException;
	
	void deleteAllPatent(@Param("tableName") String tableName) throws DataAccessException;
	void deleteClassification(Classification cf) throws DataAccessException;
	
	List<ClassificationInfo> getClassificationInfos(Integer id) throws DataAccessException;
	List<Patent> getPatents(@Param("tableName") String tableName, @Param("id") Integer id) throws DataAccessException;
	
	ClassificationInfo getClassificationInfo(Integer id) throws DataAccessException;
	void insertClassificationInfo(ClassificationInfo info) throws DataAccessException;
	
	List<PatentFile> getClassificationFiles(Integer id) throws DataAccessException;
	List<PatentFile> getClassificationFilesByProjectId(Integer id) throws DataAccessException;
	void insertClassificationFile(PatentFile file) throws DataAccessException;
	void deleteClassificationFile(Integer id) throws DataAccessException;
	
	void updateProjectStep1(ProjectStep1 ps1) throws DataAccessException;
	void insertIntoPatentInfos(@Param("tableName") String tableName, @Param("list")List<Patent> list) throws DataAccessException;
	String getUnification(@Param("APLCT") String APLCT) throws DataAccessException;
	void insertIntoPatentInfo(Patent patent) throws DataAccessException;
	void deletePatentInfos(@Param("tableName") String tableName, @Param("fileId") Integer fileId) throws DataAccessException;
	void insertUnificationPatent(@Param("id") Integer id, @Param("unfcn") String unfcn, @Param("cnt") Integer cnt);
	void deleteAllUnificationPatent(@Param("id") Integer id) throws DataAccessException;
	
	List<Patent> getDuplicatePatentAppnm(@Param("tableName") String tableName) throws DataAccessException;
	List<Patent> getDplicationClassificaitonInfo(@Param("tableName") String tableName, @Param("appnm") String appnm, @Param("dct") String dct) throws DataAccessException;
	void updateProjectDuplicateAppnm(@Param("projectId") Integer projectId, @Param("duplicate") Integer duplicate, @Param("aplct") String aplct) throws DataAccessException;
	
	void updateDuplicatePatentByAppnm(@Param("tableName") String tableName, @Param("ids") List<Integer> ids) throws DataAccessException;
	void updateClassificationFileDone(@Param("projectId") Integer projectId) throws DataAccessException;
	Integer getDuplicateProject(@Param("tableName") String tableName) throws DataAccessException;
	Integer getUnificationProjectCnt(@Param("id") Integer id) throws DataAccessException;
	List<String> getUnificationProject(@Param("id") Integer id) throws DataAccessException;
	Integer getDuplicatePatentListSize(PageFactoryService pageFactory) throws DataAccessException;
	List<Patent> getDuplicatePatentList(PageFactoryService pageFactory) throws DataAccessException;
	
	void deletePatentClmns(@Param("id") int id) throws DataAccessException;
	void insertPatentClmns(@Param("id") int id, @Param("clmnId") int clmnId, @Param("ordr") int order) throws DataAccessException;
	List<ClssTree> getClssTree(@Param("id") int id, @Param("dept") Integer dept) throws DataAccessException;
	List<CLMN> getTotalClmns() throws DataAccessException;
	List<CLMN> getTotalClmns2() throws DataAccessException;
	List<Patent> getDuplicatePatentByClssId(@Param("tableName") String tableName, @Param("clssId") int clssId) throws DataAccessException;
	List<Patent> getDuplicatePatent2(Patent p) throws DataAccessException;
	void updateDuplicatePatent(Patent p) throws DataAccessException;
	List<Patent> getDuplicatePatentForClssId(@Param("tableName") String tableName) throws DataAccessException;
	List<Patent> getDuplicatePatentForClssId2(Patent p) throws DataAccessException;
	
	List<Patent> getDuplicatePatentInfo(@Param("tableName") String tableName) throws DataAccessException;
	List<Patent> getDuplicatePatent(Patent patent) throws DataAccessException;
	void updateDuplicatePatentByIds(@Param("tableName") String tableName, @Param("deleteList") List<Integer> deleteList) throws DataAccessException;
	
	/******************************************************************************************************************/
	// STEP2
	/******************************************************************************************************************/
	ProjectStep2 getProjectInfo2(Integer id) throws DataAccessException;
	void updateProjectStep2(ProjectStep2 ps2) throws DataAccessException;
	
	List<Patent> getTmpNoiseList(@Param("tableName") String tableName, @Param("list") List<Integer> list) throws DataAccessException;
	Patent getTmpNoiseInfo(@Param("tableName") String tableName, @Param("id") Integer id) throws DataAccessException;
	void updateNoiseCompleate(@Param("tableName") String tableName, @Param("list") List<Integer> list, @Param("lang") Integer lang) throws DataAccessException;
	void updateStep2Check(@Param("id") Integer id) throws DataAccessException;
	
	List<Patent> getNoisePatents(@Param("tableName") String tableName, @Param("ln") Integer ln) throws DataAccessException;
	
	void clearNoise(@Param("tableName") String tableName) throws DataAccessException;
	void updateNoise(@Param("tableName") String tableName, @Param("ids") List<Integer> ids) throws DataAccessException;
	Integer getFinalNoisePatentListSize(PageFactoryService info) throws DataAccessException;
	List<Patent> getFinalNoisePatentList(PageFactoryService info) throws DataAccessException;
	
	/******************************************************************************************************************/
	// STEP3
	/******************************************************************************************************************/
	ProjectStep3 getProjectInfo3(Integer id) throws DataAccessException;
	void updateProjectStep3(ProjectStep3 ps3) throws DataAccessException;
	List<Pair<Integer, String>> getFirstClassification(Integer id) throws DataAccessException;
	List<Pair<Integer, String>> getChildClassification(@Param("pId") Integer pId, @Param("id") Integer id) throws DataAccessException;
	void deleteOmissionPatent(@Param("tableName") String tableName, @Param("status") Integer status) throws DataAccessException;
	void updateDeleteOmissionDuplicatePatent(@Param("tableName") String tableName) throws DataAccessException;
	
	int getCheckOmissionPatent(@Param("tableName") String tableName, @Param("nat") String nat, @Param("appnm") String appnm, @Param("appnm2") String appnm2) throws DataAccessException;
	
	List<Patent> getTmpOmissionFamilyPatent(@Param("tableName") String tableName) throws DataAccessException;
	void insertOmissionFamilyPatent(@Param("tableName") String tableName) throws DataAccessException;
	List<Patent> getOmissionFamilyPatent(@Param("tableName") String tableName) throws DataAccessException;

	List<Patent> getTmpOmissionQuotedPatent(@Param("tableName") String tableName, @Param("quoted") Integer quoted) throws DataAccessException;
	List<Patent> getOmissionCitationPatent(@Param("tableName") String tableName, @Param("quoted") Integer quoted) throws DataAccessException;
	Integer getOmissionCitationPatentClssId(Patent patent) throws DataAccessException;
	
	void insertTmpPatentInfo(NexitPatent patent) throws DataAccessException;
	List<Pair<Integer, String>> getDefaultOmissionPatent(@Param("id") Integer id) throws DataAccessException;
	
	void updateDuplicateOmission(@Param("tableName") String tableName, @Param("list") List<Integer> list) throws DataAccessException;
	void updateOmissionClssId(NexitPatent pt) throws DataAccessException;
	
	Integer getFinalFmlyPatentSize(@Param("tableName") String tableName) throws DataAccessException;
	Integer getFinalQtdPatentSize(@Param("tableName") String tableName) throws DataAccessException;
	Integer getIntegrityPatentListSize(PageFactoryService pageFactory) throws DataAccessException;
	List<Patent> getIntegrityPatentList(PageFactoryService pageFactory) throws DataAccessException;
	
	NexitPatent getQtdPatent(@Param("tableName") String tableName, @Param("page") Integer page) throws DataAccessException;
	String getClassificationNameById(@Param("id") Integer id) throws DataAccessException;
	Integer getQtdPatentTotalSize(@Param("tableName") String tableName) throws DataAccessException;
	void updateQtdForm(Patent patent) throws DataAccessException;
	void updateQtdComplete(@Param("id") Integer id) throws DataAccessException;
	
	List<Patent> getDuplicateRemotePatent(Patent patent) throws DataAccessException;
	void deleteUpdateDuplicatePatent(Patent patent) throws DataAccessException;
	/******************************************************************************************************************/
	// STEP4
	/******************************************************************************************************************/
	ProjectStep4 getProjectInfo4(@Param("id") Integer id) throws DataAccessException;
	void updateProjectStep4(ProjectStep4 ps4) throws DataAccessException;
	List<Patent> getCalcPatent(@Param("tableName") String tableName) throws DataAccessException;
	List<Patent> getCalcIdxcntnrghtPatent(@Param("indt") String indt, @Param("tableName") String tableName) throws DataAccessException;
	List<Index> getCalcQtdPatent(@Param("tableName") String tableName) throws DataAccessException;
	List<Index> getCalcFmlyPatent(@Param("tableName") String tableName) throws DataAccessException;
	List<Index> getCalcClnmPatent(@Param("tableName") String tableName) throws DataAccessException;
	void clearScr(@Param("tableName") String tableName) throws DataAccessException;
	void updateCalcPatent(Patent patent) throws DataAccessException;
	List<Index> getRcmndPatent(@Param("id") Integer id, @Param("tableName") String tableName, @Param("stnd") Integer stnd, @Param("numb") Integer numb, @Param("scre") Integer scre) throws DataAccessException;
	void getRcmndPatent(Index index) throws DataAccessException;
	void updateClearCalcRtPatent(@Param("tableName") String tableName) throws DataAccessException;
	void updateCalcRtPatent(Index index) throws DataAccessException;
	
	Integer getRcmndPatentListSize(PageFactoryService pageFactory) throws DataAccessException;
	List<Patent> getRcmndPatentList(PageFactoryService pageFactory) throws DataAccessException;
	
	/******************************************************************************************************************/
	// STEP5
	/******************************************************************************************************************/
	ProjectStep5 getProjectInfo5(Integer id) throws DataAccessException;
	Integer getPatentDateInfo(@Param("tableName") String tableName) throws DataAccessException;
	ProjectStep5Chart getProjectInfo5Chart(Integer id) throws DataAccessException;
	void updateProjectStep5(ProjectStep5 ps5) throws DataAccessException;
	void updateProjectStep5File(ProjectStep5 ps5) throws DataAccessException;
	void updateProjectStep5Chart(ProjectStep5Chart ps5) throws DataAccessException;

	List<String> getIptNatList(@Param("tableName") String tableName, @Param("rcmnd") Integer rcmnd) throws DataAccessException;
	List<Dataset> getClssInfo(@Param("tableName") String tableName, @Param("rcmnd") Integer rcmnd) throws DataAccessException;
	
	String getTableName(@Param("id") Integer id) throws DataAccessException;
	Pair<Integer, Integer> getPatentYearInfo(@Param("tableName") String tableName, @Param("rcmnd") Integer rcmnd) throws DataAccessException;
	Pair<Integer, Integer> getPatentYearInfoByClssId(@Param("tableName") String tableName, @Param("clssId") Integer clssId, @Param("rcmnd") Integer rcmnd) throws DataAccessException;
	Pair<Integer, Integer> getPatentYearInfoByNation(@Param("tableName") String tableName, @Param("nat") String nat, @Param("rcmnd") Integer rcmnd) throws DataAccessException;
	List<Dataset> getIpt11(@Param("tableName") String tableName, @Param("rcmnd") Integer rcmnd) throws DataAccessException;
	List<Dataset> getIpt12(@Param("tableName") String tableName, @Param("rcmnd") Integer rcmnd) throws DataAccessException;
	List<String> getIpt13NatList(@Param("tableName") String tableName, @Param("rcmnd") Integer rcmnd) throws DataAccessException;
	List<Dataset> getIpt13(@Param("tableName") String tableName, @Param("nat") String nat, @Param("rcmnd") Integer rcmnd) throws DataAccessException;
	
	List<Dataset> getIpt21(@Param("tableName") String tableName, @Param("rcmnd") Integer rcmnd) throws DataAccessException;
	Dataset getIpt22(@Param("tableName") String tableName, @Param("nat") String nat, @Param("rcmnd") Integer rcmnd) throws DataAccessException;
	List<Dataset> getIpt23(@Param("tableName") String tableName, @Param("nat") String nat, @Param("rcmnd") Integer rcmnd) throws DataAccessException;
	List<Dataset> getIpt24(@Param("tableName") String tableName, @Param("nat") String nat, @Param("rcmnd") Integer rcmnd) throws DataAccessException;
	
	Dataset getIpt31(@Param("tableName") String tableName, @Param("st") String start, @Param("fh") String finish, @Param("rcmnd") Integer rcmnd) throws DataAccessException;
	Dataset getIpt32(@Param("tableName") String tableName, @Param("nat") String nat, @Param("st") String start, @Param("fh") String finish, @Param("rcmnd") Integer rcmnd) throws DataAccessException;
	
	List<Dataset> getIpt41(@Param("tableName") String tableName, @Param("nat") String nat, @Param("rcmnd") Integer rcmnd) throws DataAccessException;
	
	List<Dataset> getIpt51(@Param("tableName") String tableName, @Param("rcmnd") Integer rcmnd) throws DataAccessException;
	List<Dataset> getIpt51ClssInfo(@Param("tableName") String tableName, @Param("rcmnd") Integer rcmnd) throws DataAccessException;
	List<Dataset> getIpt52(@Param("tableName") String tableName, @Param("clssId") Integer clssId, @Param("nat") String nat, @Param("rcmnd") Integer rcmnd) throws DataAccessException;
	
	List<Dataset> getIpt61(@Param("tableName") String tableName, @Param("st") String start, @Param("fh") String finish, @Param("rcmnd") Integer rcmnd) throws DataAccessException;
	List<Dataset> getIpt62(@Param("tableName") String tableName, @Param("st") String start, @Param("fh") String finish, @Param("rcmnd") Integer rcmnd) throws DataAccessException;
	
	List<Dataset> getIpt71(@Param("tableName") String tableName, @Param("clssId") Integer clssId, @Param("rcmnd") Integer rcmnd) throws DataAccessException;
	
	List<Tableset> getIpt81(@Param("tableName") String tableName, @Param("param") String param, @Param("cnt") Integer cnt, @Param("rcmnd") Integer rcmnd) throws DataAccessException;
	
	List<Dataset> getIpt91(@Param("tableName") String tableName, @Param("count") Integer count, @Param("rcmnd") Integer rcmnd) throws DataAccessException;
	List<Dataset> getIpt92(@Param("tableName") String tableName, @Param("count") Integer count, @Param("rcmnd") Integer rcmnd) throws DataAccessException;
	
	List<Dataset> getIpt101(@Param("tableName") String tableName, @Param("nat") String nat, @Param("count") Integer count, @Param("rcmnd") Integer rcmnd) throws DataAccessException;
	List<Dataset> getIpt102(@Param("tableName") String tableName, @Param("clssId") Integer clssid, @Param("count") Integer count, @Param("rcmnd") Integer rcmnd) throws DataAccessException;
}
