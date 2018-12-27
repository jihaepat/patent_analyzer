package com.patent.web.project;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.Document;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.TableRowAlign;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.xmlbeans.XmlCursor;
import org.joda.time.DateTime;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTVMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.patent.web.chart.Dataset;
import com.patent.web.chart.Tableset;
import com.patent.web.domain.Pair;
import com.patent.web.domain.SimpleResult;
import com.patent.web.project.ProjectStep5Controller.EnChart;
import com.patent.web.project.domain.ChartBarInfo;
import com.patent.web.project.domain.ChartBubbleInfo;
import com.patent.web.project.domain.ChartMixedInfo;
import com.patent.web.project.domain.ChartMixedInfo2;
import com.patent.web.project.domain.ChartPieInfo;
import com.patent.web.project.domain.PoiChart;
import com.patent.web.project.domain.ProjectStep5;
import com.patent.web.project.domain.SimpleChartResult;
import com.patent.web.project.service.ProjectService;
import com.patent.web.utils.PatentUtiles;

@Controller
public class ProjectStep5ChartController {

	@Autowired
	ProjectService service;
	
	static String defaultValue="0";
	
	@ResponseBody
	@PostMapping("/project/step5/getchart")
	public SimpleResult chart(HttpServletRequest req) {
		String name = req.getParameter("name");
		return new SimpleResult();
	}
	
//	연도별 주요 출원국 출원 건수 stack bar / stacked area
	@ResponseBody
	@PostMapping("/project/step5/getchart1/{id}")
	public SimpleChartResult chart1(HttpServletRequest req, @PathVariable("id") Integer id) {
		String tableName = service.getTableName(id);
		
		Pair<Integer, Integer> dtrlInfo = service.getPatentYearInfo(tableName);
		if (dtrlInfo==null || dtrlInfo.isEmpty()) {
			new SimpleChartResult().failure();
		}
		
		ChartBarInfo info = new ChartBarInfo();
		for(Integer i=dtrlInfo.getV1(); i<=dtrlInfo.getV2(); i++) {
			info.getLabel().add(i.toString());
		}

		int index=-1;
		List<Dataset> tmpList = service.getIpt11(tableName);
		for(Dataset tmp : tmpList) {
			if (!info.getDataSet().containsKey(tmp.getClmnKey())) {
				List<String> labelTmp = new ArrayList<>();
				for (int i=0;i<info.getLabel().size();i++) {
					labelTmp.add(defaultValue);
				}
				info.getDataSet().put(tmp.getClmnKey(), labelTmp);
			}
			if ((index=info.getLabel().indexOf(tmp.getRwKey())) != -1) {
				info.getDataSet().get(tmp.getClmnKey()).set(index, tmp.getCnt().toString());
			}
		}
		
		info.setName("연도별 주요 출원국 출원 건수");
		return new SimpleChartResult().info(info);
	}
	
//	주요 출원국 비율
	@ResponseBody
	@PostMapping("/project/step5/getchart2/{id}")
	public SimpleChartResult chart2(HttpServletRequest req, @PathVariable("id") Integer id) {
		String tableName = service.getTableName(id);
		
		ChartPieInfo info = new ChartPieInfo();

		List<Dataset> tmpList = service.getIpt12(tableName);
		for(Dataset tmp : tmpList) {
			info.getLabel().add(tmp.getClmnKey());
			info.getDataSet().add(tmp.getCnt().toString());
		}
		
		info.setName("주요 출원국 비율");
		return new SimpleChartResult().info(info);
	}
	
//	주요 출원국 연도별 특허 동향
	@ResponseBody
	@PostMapping("/project/step5/getchart3/{id}")
	public SimpleChartResult chart3(HttpServletRequest req, @PathVariable("id") Integer id) {
		String tableName = service.getTableName(id);
		
		List<ChartMixedInfo> infos = new ArrayList<>();

		List<String> nats = service.getIpt13NatList(tableName);
		for(String nat : nats) {
			Pair<Integer, Integer> dtrlInfo = service.getPatentYearInfoByNation(tableName, nat);
			if (dtrlInfo==null || dtrlInfo.isEmpty()) {
				continue;
			}
			
			ChartMixedInfo info = new ChartMixedInfo();
			for(Integer i=dtrlInfo.getV1(); i<=dtrlInfo.getV2(); i++) {
				info.getLabel().add(i.toString());
			}
			Pair<List<Integer>, List<Integer>> dataSet = new Pair<>();
			dataSet.setV1(initListForInteger(info.getLabel().size()));
			dataSet.setV2(initListForInteger(info.getLabel().size()));
			info.getDataSet().put(nat, dataSet);
			
			int index=-1; boolean isValid = false;
			List<Dataset> tmpList = service.getIpt13(tableName, nat);
			info.setName(nat);
			for(Dataset tmp : tmpList) {
				if ((index=info.getLabel().indexOf(tmp.getClmnKey())) != -1) {
					info.getDataSet().get(nat).getV1().set(index, tmp.getCnt());
//					info.getDataSet().get(nat).getV2().set(index, tmp.getCnt2());
					info.getDataSet().get(nat).getV2().set(index, 0);
					
					isValid = true;
				}
			}
			
			info.setName(nat);
			if (isValid) {
				Pair<List<Integer>, List<Integer>> tmp = info.getDataSet().get(nat);
				Integer prvValue = 0;
				for (int i=0; i<tmp.getV1().size();i++) {
					Integer value = tmp.getV1().get(i);
					if (i!=0) {
						tmp.getV2().set(i, value - prvValue);
					}
					prvValue = value;
				}
				infos.add(info);
			}
		}
		
		return new SimpleChartResult().infos(infos);
	}
	
	
//	출원국 주요 출원국 비율
	@ResponseBody
	@PostMapping("/project/step5/getchart4/{id}")
	public SimpleChartResult chart4(HttpServletRequest req, @PathVariable("id") Integer id) {
		String tableName = service.getTableName(id);
		
		ChartPieInfo info = new ChartPieInfo();

		List<Dataset> tmpList = service.getIpt21(tableName);
		for(Dataset tmp : tmpList) {
			info.getLabel().add(tmp.getClmnKey());
			info.getDataSet().add(tmp.getCnt().toString());
		}

		info.setName("출원국 주요 출원국 비율");
		return new SimpleChartResult().info(info);
	}
	
//	주요 출원국 내/외국인 출원 비율
	@ResponseBody
	@PostMapping("/project/step5/getchart5/{id}")
	public SimpleChartResult chart5(HttpServletRequest req, @PathVariable("id") Integer id) {
		String tableName = service.getTableName(id);
		
		List<ChartPieInfo> infos = new ArrayList<>();
		List<String> nats = service.getIpt13NatList(tableName);
		for(String nat : nats) {
			ChartPieInfo info = new ChartPieInfo();
	
			Dataset dt = service.getIpt22(tableName, nat);
			if (dt.getCnt().intValue() != 0 && dt.getCnt2().intValue() != 0) {
				info.getLabel().add("내국");
				info.getDataSet().add(dt.getCnt().toString());
				
				info.getLabel().add("외국");
				info.getDataSet().add(dt.getCnt2().toString());
				
				infos.add(info);
			}
			
			info.setName(nat);
		}
		return new SimpleChartResult().infos(infos);
	}
	
//	주요 출원국 외국인 국적 비율
	@ResponseBody
	@PostMapping("/project/step5/getchart6/{id}")
	public SimpleChartResult chart6(HttpServletRequest req, @PathVariable("id") Integer id) {
		String tableName = service.getTableName(id);
		
		List<ChartPieInfo> infos = new ArrayList<>();
		List<String> nats = service.getIpt13NatList(tableName);
		for(String nat : nats) {
			ChartPieInfo info = new ChartPieInfo();
	
			List<Dataset> tmpList = service.getIpt23(tableName, nat);
			for(Dataset tmp : tmpList) {
				info.getLabel().add(tmp.getClmnKey());
				info.getDataSet().add(tmp.getCnt().toString());
			}
			
			info.setName(nat);
			infos.add(info);
		}
		return new SimpleChartResult().infos(infos);
	}
	
	
	final String dataset1 = "내국인";
	final String dataset2 = "외국인";
//	주요 출원국 연도별 내/외국인 출원 동황
	@ResponseBody
	@PostMapping("/project/step5/getchart7/{id}")
	public SimpleChartResult chart7(HttpServletRequest req, @PathVariable("id") Integer id) {
		String tableName = service.getTableName(id);
		
		List<ChartMixedInfo> infos = new ArrayList<>();
		List<String> nats = service.getIpt13NatList(tableName);
		for(String nat : nats) {
			Pair<Integer, Integer> dtrlInfo = service.getPatentYearInfoByNation(tableName, nat);
			if (dtrlInfo==null || dtrlInfo.isEmpty()) {
				continue;
			}
			
			ChartMixedInfo info = new ChartMixedInfo();
			for(Integer i=dtrlInfo.getV1(); i<=dtrlInfo.getV2(); i++) {
				info.getLabel().add(i.toString());
			}
			Pair<List<Integer>, List<Integer>> dataSet = new Pair<>();
			dataSet.setV1(initListForInteger(info.getLabel().size()));
			dataSet.setV2(initListForInteger(info.getLabel().size()));
			info.getDataSet().put(dataset1, dataSet);

			Pair<List<Integer>, List<Integer>> dataSet2 = new Pair<>();
			dataSet2.setV1(initListForInteger(info.getLabel().size()));
			dataSet2.setV2(initListForInteger(info.getLabel().size()));
			info.getDataSet().put(dataset2, dataSet2);
			
			int index=-1; boolean isValid = false;
			List<Dataset> tmpList = service.getIpt24(tableName, nat);
			info.setName(nat);
			for(Dataset tmp : tmpList) {
				if ((index=info.getLabel().indexOf(tmp.getClmnKey())) != -1) {
					info.getDataSet().get(dataset1).getV1().set(index, tmp.getCnt());
					info.getDataSet().get(dataset1).getV2().set(index, 0);
					info.getDataSet().get(dataset2).getV1().set(index, tmp.getCnt3());
					info.getDataSet().get(dataset2).getV2().set(index, 0);
					isValid = true;
				}
			}
			
			if (isValid) {
				Pair<List<Integer>, List<Integer>> tmp = info.getDataSet().get(dataset1);
				Integer prvValue = 0;
				for (int i=0; i<tmp.getV1().size();i++) {
					Integer value = tmp.getV1().get(i);
					if (i!=0) {
						tmp.getV2().set(i, value - prvValue);
					}
					prvValue = value;
				}
				tmp = info.getDataSet().get(dataset2);
				prvValue = 0;
				for (int i=0; i<tmp.getV1().size();i++) {
					Integer value = tmp.getV1().get(i);
					if (i!=0) {
						tmp.getV2().set(i, value - prvValue);
					}
					prvValue = value;
				}
				
				infos.add(info);
			}
		}
		return new SimpleChartResult().infos(infos);
	}
	
//	전체 주요 출원국의 기술 성장 단계 
	@ResponseBody
	@PostMapping("/project/step5/getchart8/{id}")
	public SimpleChartResult chart8(HttpServletRequest req, @PathVariable("id") Integer id) {
		// 구간 기간 계산.
		Map<Integer, Pair<String, String>> dateList = new HashMap<>();
		ProjectStep5 ps5 = service.getProjectInfo5(id);
		DateTime dt = new DateTime().withMonthOfYear(1).withDayOfMonth(1).withTime(0, 0, 0, 0).plusYears(-1);
		for (int i=ps5.getIpt2().intValue(); i> 0; i--) {
			Pair<String, String> pair = new Pair<>();
			pair.setV2(dt.toString("yyyy"));
			if (ps5.getIpt31().intValue() == 0 && i==ps5.getIpt2().intValue()) {
				pair.setV2(dt.plusYears(1).toString("yyyy"));
			}
			
			dt = dt.plusYears(ps5.getIpt3().intValue()*-1+1);
			pair.setV1(dt.toString("yyyy"));
			
			dateList.put(i, pair);
			
			dt = dt.plusYears(-1);
		}
		ChartBubbleInfo info = new ChartBubbleInfo();
		for (int i=ps5.getIpt2().intValue(); i>0; i--) {
			Pair<String, String> pair = dateList.get(i);
			
			Dataset tmp = service.getIpt31(ps5.getTableName(), pair.getV1(), pair.getV2());

			Pair<String, String> value = new Pair<>();
			value.setV1(tmp.getCnt().toString()); //건수
			value.setV2(tmp.getCnt2().toString()); //출원인수
			
			info.getDataSet().put(String.format("%s~%s", pair.getV1(), pair.getV2()), value);
		}
		
		info.setName("전체 주요 출원국의 기술 성장 단계");
		return new SimpleChartResult().info(info);
	}
	
	
//	주요 출원국 기술 성장 단계
	@ResponseBody
	@PostMapping("/project/step5/getchart9/{id}")
	public SimpleChartResult chart9(HttpServletRequest req, @PathVariable("id") Integer id) {
		// 구간 기간 계산.
		Map<Integer, Pair<String, String>> dateList = new HashMap<>();
		ProjectStep5 ps5 = service.getProjectInfo5(id);
		DateTime dt = new DateTime().withMonthOfYear(1).withDayOfMonth(1).withTime(0, 0, 0, 0).plusYears(-1);
		for (int i=ps5.getIpt2().intValue(); i> 0; i--) {
			Pair<String, String> pair = new Pair<>();
			pair.setV2(dt.toString("yyyy"));
			if (ps5.getIpt31().intValue() == 0 && i==ps5.getIpt2().intValue()) {
				pair.setV2(dt.plusYears(1).toString("yyyy"));
			}
			
			dt = dt.plusYears(ps5.getIpt3().intValue()*-1+1);
			pair.setV1(dt.toString("yyyy"));
			
			dateList.put(i, pair);
			
			dt = dt.plusYears(-1);
		}
		
		List<ChartBubbleInfo> infos = new ArrayList<>();
		List<String> nats = service.getIpt13NatList(ps5.getTableName());
		for(String nat : nats) {
			ChartBubbleInfo info = new ChartBubbleInfo();
			
			for (int i=ps5.getIpt2().intValue(); i>0; i--) {
				Pair<String, String> pair = dateList.get(i);
				Dataset tmp = service.getIpt32(ps5.getTableName(), nat, pair.getV1(), pair.getV2());
				if (tmp.getCnt() > 0 || tmp.getCnt2().intValue() > 0) {
					Pair<String, String> value = new Pair<>();
					value.setV1(tmp.getCnt().toString()); //건수
					value.setV2(tmp.getCnt2().toString()); //출원인수
					
					info.getDataSet().put(String.format("%s~%s", pair.getV1(), pair.getV2()), value);
				}
			}
			
			info.setName(nat);
			infos.add(info);			
		}
		return new SimpleChartResult().infos(infos);
	}
	
//	전체 주요 출원국의 세부 기술 출원 비율
	@ResponseBody
	@PostMapping("/project/step5/getchart10/{id}")
	public SimpleChartResult chart10(HttpServletRequest req, @PathVariable("id") Integer id) {
		String tableName = service.getTableName(id);
		
		ChartPieInfo info = new ChartPieInfo();

		List<Dataset> tmpList = service.getIpt41(tableName, null);
		for(Dataset tmp : tmpList) {
			info.getLabel().add(tmp.getClmnKey());
			info.getDataSet().add(tmp.getCnt().toString());
		}
		
		info.setName("전체 주요 출원국의 세부 기술 출원 비율");
		return new SimpleChartResult().info(info);
	}
	
//	주요 출원국 세부 기술 출원 비율
	@ResponseBody
	@PostMapping("/project/step5/getchart11/{id}")
	public SimpleChartResult chart11(HttpServletRequest req, @PathVariable("id") Integer id) {
		String tableName = service.getTableName(id);
		
		List<ChartPieInfo> infos = new ArrayList<>();
		List<String> nats = service.getIpt13NatList(tableName);
		for(String nat : nats) {
			ChartPieInfo info = new ChartPieInfo();
	
			List<Dataset> tmpList = service.getIpt41(tableName, nat);
			for(Dataset tmp : tmpList) {
				info.getLabel().add(tmp.getClmnKey());
				info.getDataSet().add(tmp.getCnt().toString());
			}
			
			info.setName(nat);
			infos.add(info);
		}
		return new SimpleChartResult().infos(infos);
	}
	
//	전체 세부 기술의 연도별 출원 동향
	@ResponseBody
	@PostMapping("/project/step5/getchart12/{id}")
	public SimpleChartResult chart12(HttpServletRequest req, @PathVariable("id") Integer id) {
		String tableName = service.getTableName(id);
		
		Pair<Integer, Integer> dtrlInfo = service.getPatentYearInfo(tableName);
		if (dtrlInfo==null || dtrlInfo.isEmpty()) {
			new SimpleChartResult().failure();
		}
		
		ChartBarInfo info = new ChartBarInfo();
		for(Integer i=dtrlInfo.getV1(); i<=dtrlInfo.getV2(); i++) {
			info.getLabel().add(i.toString());
		}

		int index=-1;
		List<Dataset> tmpList = service.getIpt51(tableName);
		for(Dataset tmp : tmpList) {
			if (!info.getDataSet().containsKey(tmp.getClmnKey())) {
				List<String> labelTmp = new ArrayList<>();
				for (int i=0;i<info.getLabel().size();i++) {
					labelTmp.add(defaultValue);
				}
				info.getDataSet().put(tmp.getClmnKey(), labelTmp);
			}
			if ((index=info.getLabel().indexOf(tmp.getRwKey())) != -1) {
				info.getDataSet().get(tmp.getClmnKey()).set(index, tmp.getCnt().toString());
			}
		}
		
		info.setName("전체 세부 기술의 연도별 출원 동향");
		return new SimpleChartResult().info(info);
	}
	
//	세부 기술 분류의 연도별 출원 동향
	@ResponseBody
	@PostMapping("/project/step5/getchart13/{id}")
	public SimpleChartResult chart13(HttpServletRequest req, @PathVariable("id") Integer id) {
		String tableName = service.getTableName(id);
		
		List<ChartMixedInfo2> infos = new ArrayList<>();
		
		// 기술별로 별도 차트
		// 기술별로 국가 스택, 증감율.
		Map<Pair<Integer, String>, List<String>> clssList = new HashMap<>();
		Map<Pair<Integer, String>, List<String>> natList = new HashMap<>();
		List<Dataset> ipt51ClssList = service.getIpt51ClssInfo(tableName);
		for (Dataset dt : ipt51ClssList) {
			Pair<Integer, String> key = new Pair<>();
			key.setV1(dt.getCnt());
			key.setV2(dt.getClmnKey());
			
			if (!clssList.containsKey(key)) {
				clssList.put(key, new ArrayList<>());
			}
			if (!natList.containsKey(key)) {
				natList.put(key, new ArrayList<>());
			}
			natList.get(key).add(dt.getRwKey());
		}
		
		Iterator<Pair<Integer, String>> keys = clssList.keySet().iterator();
		while( keys.hasNext() ){
			Pair<Integer, String> clssId = keys.next();
			Pair<Integer, Integer> dtrlInfo = service.getPatentYearInfoByClssId(tableName, clssId.getV1());
			if (dtrlInfo==null || dtrlInfo.isEmpty()) {
				continue;
			}
			for(Integer i=dtrlInfo.getV1(); i<=dtrlInfo.getV2(); i++) {
				clssList.get(clssId).add(i.toString());
			}
		}
		
		// 기술별 국가별 건수 및 증감율.
		keys = natList.keySet().iterator();
		while( keys.hasNext() ){
			Pair<Integer, String> key = keys.next();
			ChartMixedInfo2 info = new ChartMixedInfo2();
			info.setLabel(clssList.get(key));
			Map<String, Pair<List<Integer>, List<Integer>>> dataSet = new HashMap<>();
			info.getDataSet().put(key, dataSet);
			
			int index = -1;
			List<String> nats = natList.get(key);
			for (String nat : nats) {
				Pair<List<Integer>, List<Integer>> natDetail = new Pair<>();
				natDetail.setV1(initListForInteger(info.getLabel().size()));
				natDetail.setV2(initListForInteger(info.getLabel().size()));
				
				dataSet.put(nat, natDetail);
				
				List<Dataset> tmpList = service.getIpt52(tableName, key.getV1(), nat);
				for (Dataset tmp : tmpList) {
					if ((index=info.getLabel().indexOf(tmp.getClmnKey())) != -1) {
						natDetail.getV1().set(index, tmp.getCnt());
//						natDetail.getV2().set(index, tmp.getCnt2().toString());
						natDetail.getV2().set(index, 0);
					}
				}
				
				
				Integer prvValue = 0;
				for (int i=0; i<natDetail.getV1().size(); i++) {
					Integer value = natDetail.getV1().get(i);
					if (i!=0) {
						natDetail.getV2().set(i, value - prvValue);
					}
					prvValue = value;
				}
			}
			
			info.setName(key.getV2());
			infos.add(info);
		}
		
		return new SimpleChartResult().infos(infos);
	}
	
//	시간 구간별 세부 기술 분류 점유율 비율
	@ResponseBody
	@PostMapping("/project/step5/getchart14/{id}")
	public SimpleChartResult chart14(HttpServletRequest req, @PathVariable("id") Integer id) {
		List<ChartPieInfo> infos = new ArrayList<>();

		// 구간 기간 계산.
		Map<Integer, Pair<String, String>> dateList = new HashMap<>();
		ProjectStep5 ps5 = service.getProjectInfo5(id);
		DateTime dt = new DateTime().withMonthOfYear(1).withDayOfMonth(1).withTime(0, 0, 0, 0).plusYears(-1);
		for (int i=ps5.getIpt5().intValue(); i> 0; i--) {
			Pair<String, String> pair = new Pair<>();
			pair.setV2(dt.toString("yyyy"));
			if (ps5.getIpt31().intValue() == 0 && i==ps5.getIpt5().intValue()) {
				pair.setV2(dt.plusYears(1).toString("yyyy"));
			}
			
			dt = dt.plusYears(ps5.getIpt6().intValue()*-1+1);
			pair.setV1(dt.toString("yyyy"));
			
			dateList.put(i, pair);
			
			dt = dt.plusYears(-1);
		}
		
		
		for (int i=ps5.getIpt5().intValue(); i> 0; i--) {
			ChartPieInfo info = new ChartPieInfo();
			Pair<String, String> pair = dateList.get(i);
			
			List<Dataset> tmpList = service.getIpt61(ps5.getTableName(), pair.getV1(), pair.getV2());
			if (tmpList.size() > 0) {
				for(Dataset tmp : tmpList) {
					info.getLabel().add(tmp.getClmnKey());
					info.getDataSet().add(tmp.getCnt().toString());
				}
				
				info.setName(String.format("%s~%s 세부 기술 분류 점유율 비율", pair.getV1(), pair.getV2()));
				infos.add(info);
			}
		}
		
		return new SimpleChartResult().infos(infos);
	}
	
//	시간 구간별 세부 기술 분류 점유율 동향
	@ResponseBody
	@PostMapping("/project/step5/getchart15/{id}")
	public SimpleChartResult chart15(HttpServletRequest req, @PathVariable("id") Integer id) {
		ChartBarInfo info = new ChartBarInfo();

		// 구간 기간 계산.
		Map<Integer, Pair<String, String>> dateList = new HashMap<>();
		ProjectStep5 ps5 = service.getProjectInfo5(id);
		DateTime dt = new DateTime().withMonthOfYear(1).withDayOfMonth(1).withTime(0, 0, 0, 0).plusYears(-1);
		for (int i=ps5.getIpt5().intValue(); i> 0; i--) {
			Pair<String, String> pair = new Pair<>();
			pair.setV2(dt.toString("yyyy"));
			if (ps5.getIpt31().intValue() == 0 && i==ps5.getIpt5().intValue()) {
				pair.setV2(dt.plusYears(1).toString("yyyy"));
			}
			
			dt = dt.plusYears(ps5.getIpt6().intValue()*-1+1);
			pair.setV1(dt.toString("yyyy"));
			
			dateList.put(i, pair);
			
			dt = dt.plusYears(-1);

			info.getLabel().add(String.format("%S~%s", pair.getV1(), pair.getV2()));
		}
		Collections.reverse(info.getLabel()); 
		
		int index = -1;
		for (int i=ps5.getIpt5().intValue(); i> 0; i--) {
			Pair<String, String> pair = dateList.get(i);
			List<Dataset> tmpList = service.getIpt62(ps5.getTableName(), pair.getV1(), pair.getV2());
			for(Dataset tmp : tmpList) {
				if (!info.getDataSet().containsKey(tmp.getClmnKey())) {
					info.getDataSet().put(tmp.getClmnKey(), initList(info.getLabel().size()));
				}
				if ((index=info.getLabel().indexOf(String.format("%S~%s", pair.getV1(), pair.getV2()))) != -1) {
					info.getDataSet().get(tmp.getClmnKey()).set(index, tmp.getCnt().toString());
				}
			}
		}
		
		info.setName("시간 구간별 세부 기술 분류 점유율 동향");
		return new SimpleChartResult().info(info);
	}
	
//	세부 기술별 주요 출원국 구성 비율
	@ResponseBody
	@PostMapping("/project/step5/getchart16/{id}")
	public SimpleChartResult chart16(HttpServletRequest req, @PathVariable("id") Integer id) {
		String talbeName = service.getTableName(id);
		
		List<ChartPieInfo> infos = new ArrayList<>();
		
		List<Dataset> clssList = service.getClssInfo(talbeName);
		for(Dataset clssId : clssList) {
			ChartPieInfo info = new ChartPieInfo();
			
			List<Dataset> tmpList = service.getIpt71(talbeName, clssId.getCnt());
			for(Dataset tmp : tmpList) {
				info.getLabel().add(tmp.getClmnKey());
				info.getDataSet().add(tmp.getCnt().toString());
			}
			
			info.setName(clssId.getClmnKey());
			infos.add(info);
			
		}
		return new SimpleChartResult().infos(infos);
	}
	
//	주요 출원인의 국가별 출원 통계 및 주력 기술 분야
//	@ResponseBody
//	@PostMapping("/project/step5/getchart17")
//	public SimpleChartResult chart17(HttpServletRequest req) {
//		return new SimpleChartResult();
//	}
	
//	주요 출원인 출원 건수
	@ResponseBody
	@PostMapping("/project/step5/getchart18/{id}")
	public SimpleChartResult chart18(HttpServletRequest req, @PathVariable("id") Integer id) {
		ChartPieInfo info = new ChartPieInfo();
		
		ProjectStep5 ps5 = service.getProjectInfo5(id);
		List<Dataset> tmpList = service.getIpt91(ps5.getTableName(), ps5.getIpt8());
		for (Dataset dt : tmpList) {
			info.getLabel().add(dt.getRwKey());
			info.getDataSet().add(dt.getCnt().toString());
		}
		
		info.setName("주요 출원인 출원 건수");
		return new SimpleChartResult().info(info);
	}

	final String key1 = "삼극";
	final String key2 = "비삼극";
	
//	주요 출원인 삼극 특허 보유 현황
	@ResponseBody
	@PostMapping("/project/step5/getchart19/{id}")
	public SimpleChartResult chart19(HttpServletRequest req, @PathVariable("id") Integer id) {
		ChartBarInfo info = new ChartBarInfo();
		
		ProjectStep5 ps5 = service.getProjectInfo5(id);
		List<Dataset> tmpList = service.getIpt92(ps5.getTableName(), ps5.getIpt8());
		for (int i=0;i<tmpList.size(); i++) {
			info.getLabel().add(tmpList.get(i).getRwKey());
		}
		info.getDataSet().put(key1, initList(info.getLabel().size()));
		info.getDataSet().put(key2, initList(info.getLabel().size()));
		
		for (int i=0;i<tmpList.size(); i++) {
			Dataset dt = tmpList.get(i);
			info.getDataSet().get(key1).set(i, dt.getCnt().toString());
			info.getDataSet().get(key2).set(i, dt.getCnt2().toString());
		}
		
		info.setName("주요 출원인 삼극 특허 보유 현황");
		return new SimpleChartResult().info(info);
	}
	
//	국가별 주요 출원인 출원 건수
	@ResponseBody
	@PostMapping("/project/step5/getchart20/{id}")
	public SimpleChartResult chart20(HttpServletRequest req, @PathVariable("id") Integer id) {
		List<ChartPieInfo> infos = new ArrayList<>();

		ProjectStep5 ps5 = service.getProjectInfo5(id);
		List<String> nats = service.getIpt13NatList(ps5.getTableName());
		
		for (String nat : nats) {
			List<Dataset> tmpList = service.getIpt101(ps5.getTableName(), nat, ps5.getIpt9());
			ChartPieInfo info = new ChartPieInfo();
			boolean isValid = false;
			for (Dataset dt : tmpList) {
				info.getLabel().add(dt.getRwKey());
				info.getDataSet().add(dt.getCnt().toString());
				isValid=true;
			}
			
			info.setName(nat);
			if (isValid) infos.add(info);
		}
		
		return new SimpleChartResult().infos(infos);
	}
	
//	세부 길술별 주요 출원인 출원 건수
	@ResponseBody
	@PostMapping("/project/step5/getchart21/{id}")
	public SimpleChartResult chart21(HttpServletRequest req, @PathVariable("id") Integer id) {
		List<ChartPieInfo> infos = new ArrayList<>();

		ProjectStep5 ps5 = service.getProjectInfo5(id);
		List<Dataset> clssList = service.getClssInfo(ps5.getTableName());
		
		for(Dataset clssId : clssList) {
			List<Dataset> tmpList = service.getIpt102(ps5.getTableName(), clssId.getCnt(), ps5.getIpt9());
			ChartPieInfo info = new ChartPieInfo();
			boolean isValid = false;
			for (Dataset dt : tmpList) {
				info.getLabel().add(dt.getRwKey());
				info.getDataSet().add(dt.getCnt().toString());
				isValid=true;
			}
			
			info.setName(clssId.getClmnKey());
			if (isValid) infos.add(info);
		}
		
		return new SimpleChartResult().infos(infos);
	}

	@Value("${tmpfilepath}") String filepath;
	@ResponseBody
	@PostMapping("/project/step5/updateImage")
	public SimpleResult updateImage(HttpServletRequest req) {
		MultipartHttpServletRequest mtprt = (MultipartHttpServletRequest) req;
		Integer id = PatentUtiles.getNumberParameter(mtprt, "id");
		
		ProjectStep5 ps5 = service.getProjectInfo5(id);
		if (!PatentUtiles.isEmpty(ps5.getSaveName())) {
			File ori = new File(String.format("%s/%d/%s", filepath, ps5.getId(), ps5.getSaveName()));
			if (ori.exists()) {
				PoiChart poiChart = new PoiChart();
				try {
					File mody = new File(String.format("%s/%d/%s", filepath, ps5.getId(), ps5.getFnlName()));
					if (mody.exists()) {
						mody.delete();
					}
					
					FileOutputStream out = new FileOutputStream(mody);
					XWPFDocument doc = new XWPFDocument(OPCPackage.open(ori));
					List<XWPFParagraph> xwpfParas = doc.getParagraphs();
					
					for(int i=0;i<xwpfParas.size();i++){   
				    	XWPFParagraph paragraph = xwpfParas.get(i);
				    	if (paragraph != null) {
				    		paragraphRun2(doc, paragraph, poiChart, id, mtprt);
				    	}
				    }
					for (XWPFTable xwpfTable : doc.getTables()) {
						List<XWPFTableRow> row = xwpfTable.getRows();
						for (XWPFTableRow xwpfTableRow : row) {
							List<XWPFTableCell> cells = xwpfTableRow.getTableCells();
							for (XWPFTableCell cell : cells) {
								if (cell != null) {
									for (XWPFParagraph paragraph : cell.getParagraphs()) {
										paragraphRun2(doc, paragraph, poiChart, id, mtprt);
									}
									
									if (cell.getTables().size() > 0) {
										tableRun(cell, doc, poiChart, id, mtprt);
									}
								}
							}
						}
					}
					
					doc.write(out);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
				}
			}
			
		}
		
		return new SimpleResult();
	}
	
	void tableRun(XWPFTableCell pcell, XWPFDocument doc, PoiChart poiChart, Integer id, MultipartHttpServletRequest mtprt) throws Exception {
		if (pcell.getTables().size() > 0) {
			for (XWPFTable xwpfiTable : pcell.getTables()) {
				for (XWPFTableRow xwpfiTableRow : xwpfiTable.getRows()) {
					for (XWPFTableCell cell : xwpfiTableRow.getTableCells()) {
						if (cell != null) {
							for (XWPFParagraph p:cell.getParagraphs()) {
								paragraphRun2(doc, p, poiChart, id, mtprt);
							}
							
							if (cell.getTables().size() > 0) {
								tableRun(pcell, doc, poiChart, id, mtprt);
							}
						}
					}
				}
			}
		}
	}
	@SuppressWarnings("unused")
	void paragraphRun2(XWPFDocument doc, XWPFParagraph p, PoiChart poiChart, Integer id, MultipartHttpServletRequest mtprt) throws Exception {
		List<XWPFRun> runs = p.getRuns();
		if (runs != null) {
			for (int i = 0; i < runs.size(); i++) {
				XWPFRun r = runs.get(i);
				poiChart.checkStart(i, r.getText(0));
				EnChart enChart = EnChart.find(poiChart.getTmp().toString());
				if (enChart != null) {
					List<Integer> runList = poiChart.getRun();
					if (runList.size() > 0) {
						int rr = poiChart.getRun().get(0);
						for(int dr : poiChart.getRun()) {
							p.removeRun(rr);
						}
					}
					
					if (enChart == EnChart.CHART17) {
						try {
							addTableSet17(doc, p, id, enChart.title);
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						try {
							String[] params = mtprt.getParameterValues(enChart.param);
							addImage(p, params, enChart.title);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					poiChart.clear();
				}
			}
		}
	}
	
//	주요 출원인의 국가별 출원 통계 및 주력 기술 분야
	void addTableSet17(XWPFDocument doc, XWPFParagraph p, Integer id, String title) throws Exception {
		ProjectStep5 ps5 = service.getProjectInfo5(id);
		if (ps5.getIpt7() == null || ps5.getIpt7() <= 0) {
			return;
		}
		
		List<String> natList = service.getIpt13NatList(ps5.getTableName());
		
		Tableset set = new Tableset();
		set.setTableName(ps5.getTableName());
		StringBuffer param = new StringBuffer();
		for (int i=0; i<natList.size(); i++) {
			param.append(String.format(", count(*) filter (where dt.nat='%s') as td%d", natList.get(i), (i+1)));
		}
		set.setParam(param.toString());
		
		List<Tableset> list = service.getIpt81(ps5.getTableName(), param.toString(), ps5.getIpt7());
		if (list.size() == 0) {
			return;
		}
//		XWPFParagraph ttp = doc.createParagraph();
		p.createRun().setText(title);
		p.setAlignment(ParagraphAlignment.LEFT);

		XWPFParagraph tbp = doc.createParagraph();
		XmlCursor cursor= tbp.getCTP().newCursor();
		XWPFTable table = tbp.getBody().insertNewTbl(cursor);
		table.setTableAlignment(TableRowAlign.LEFT);
		
		XWPFTableRow tableRowOne = table.getRow(0);
		tableRowOne.addNewTableCell();
		tableRowOne.addNewTableCell();
		tableRowOne.addNewTableCell();
		for (int i=0; i<natList.size(); i++) {
			tableRowOne.addNewTableCell();	
		}
		XWPFTableRow tableRowTwo = table.createRow();
		
		
		tableRowOne.getCell(0).setText("출원인");
	    tableRowOne.getCell(1).setText("출원인 국적");
	    XWPFRun cpr = tableRowOne.getCell(2).getParagraphs().get(0).createRun();
	    cpr.setText("주요IP");
		cpr.addBreak();
		cpr.setText("출원국(건수,%)");
		tableRowOne.getCell(3+natList.size()).setText("주력 기술분야");
		
	    for (int i=0; i<natList.size(); i++) {
	    	tableRowTwo.getCell(i+2).setText(natList.get(i));
        }
	    tableRowTwo.getCell(natList.size()+2).setText("IP 출원국 종합");
	    
	    Class<Tableset> clssTableset = Tableset.class;
	    
	    int cellId = 0;
	    for (Tableset row : list) {
	    	XWPFTableRow tableRow = table.createRow();
	    	cellId = 0;
	    	tableRow.getCell(cellId++).setText(row.getAplct());
	    	tableRow.getCell(cellId++).setText(row.getAplctnat());

	    	Pair<Integer, String> tmp = new Pair<>();
			for (int i=0; i<natList.size(); i++) {
	        	Field field = clssTableset.getDeclaredField(String.format("td%d", i+1));
        		field.setAccessible(true);
        		Integer cnt = (Integer) field.get(row);
        		if (i == 0) {
	        		tmp.setV1(cnt);
	        		tmp.setV2(natList.get(i));
        		} else if (cnt.intValue() > tmp.getV1().intValue()) {
        			tmp.setV1(cnt);
	        		tmp.setV2(natList.get(i));
        		}

        		cpr = tableRow.getCell(cellId++).getParagraphs().get(0).createRun();
        		cpr.setText(String.format("%d", cnt));
//        		cpr.addBreak();
        		cpr.setText(String.format(" (%.2f)", cnt.doubleValue()/row.getCnt().doubleValue()*100));
	        }
			tableRow.getCell(cellId++).setText(tmp.getV2());
			tableRow.getCell(cellId).setText(row.getClss());
	    }
		
		mergeCellVertically(table, 0, 0, 1);
		mergeCellVertically(table, 1, 0, 1);
		mergeCellVertically(table, 3+natList.size(), 0, 1);
		mergeCellHorizontally(table, 0, 2, 2+natList.size());
		
		CTTblWidth width = table.getCTTbl().addNewTblPr().addNewTblW();
		width.setType(STTblWidth.DXA);
//		width.setW(BigInteger.valueOf(20000));
//		width.setW(BigInteger.valueOf(9072));
		width.setW(BigInteger.valueOf(8500));
	}
	void mergeCellVertically(XWPFTable table, int col, int fromRow, int toRow) {
		for (int rowIndex = fromRow; rowIndex <= toRow; rowIndex++) {
			XWPFTableCell cell = table.getRow(rowIndex).getCell(col);
			CTVMerge vmerge = CTVMerge.Factory.newInstance();
			if (rowIndex == fromRow) {
				vmerge.setVal(STMerge.RESTART);
			} else {
				vmerge.setVal(STMerge.CONTINUE);
				for (int i = cell.getParagraphs().size(); i > 0; i--) {
					cell.removeParagraph(0);
				}
				cell.addParagraph();
			}
			CTTcPr tcPr = cell.getCTTc().getTcPr();
			if (tcPr == null) 
				tcPr = cell.getCTTc().addNewTcPr();
			tcPr.setVMerge(vmerge);
		}
	}
	void mergeCellHorizontally(XWPFTable table, int row, int fromCol, int toCol) {
		XWPFTableCell cell = table.getRow(row).getCell(fromCol);
		CTTcPr tcPr = cell.getCTTc().getTcPr();
		if (tcPr == null) {
			tcPr = cell.getCTTc().addNewTcPr();
		}
		if (tcPr.isSetGridSpan()) {
			tcPr.getGridSpan().setVal(BigInteger.valueOf(toCol - fromCol + 1));
		} else {
			tcPr.addNewGridSpan().setVal(BigInteger.valueOf(toCol - fromCol + 1));
		}
		for (int colIndex = toCol; colIndex > fromCol; colIndex--) {
			table.getRow(row).getCtRow().removeTc(colIndex);
		}
	}
	
//	void addImage(XWPFRun run, String[] datas) throws Exception {
	void addImage(XWPFParagraph p, String[] datas, String title) throws Exception {
		if (datas == null) {
			return;
		}
		
		for (String data : datas) {
			if (data == null) continue;
			
			String[]base64Image = data.split(",");
			if (base64Image.length == 2) {
				InputStream imageStream = new ByteArrayInputStream(DatatypeConverter.parseBase64Binary(base64Image[1]));
	//			run.addPicture(imageStream, Document.PICTURE_TYPE_PNG, "", Units.toEMU(1000), Units.toEMU(300));
				p.createRun().addPicture(imageStream, Document.PICTURE_TYPE_PNG, "", Units.toEMU(425), Units.toEMU(150));
				
				p.createRun().addBreak();
				p.createRun().setText(title);
				p.setAlignment(ParagraphAlignment.LEFT);
				p.createRun().addBreak();
			}
		}
	}
	
	List<String> initList(Integer size) {
		return new ArrayList<String>() {
			private static final long serialVersionUID = 1L;
			{
				for(int i=0;i<size;i++) {
					add("0");
				}
			}
		};
	}
	
	List<Integer> initListForInteger(Integer size) {
		return new ArrayList<Integer>() {
			private static final long serialVersionUID = 1L;
			{
				for(int i=0;i<size;i++) {
					add(0);
				}
			}
		};
	}
}
