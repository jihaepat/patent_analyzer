package com.patent.web.project;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.Document;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.xmlbeans.XmlCursor;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.DefaultXYZDataset;
import org.joda.time.DateTime;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTVMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.patent.web.chart.Dataset;
import com.patent.web.chart.PtntChartFactory;
import com.patent.web.chart.Tableset;
import com.patent.web.domain.PageFactoryService;
import com.patent.web.domain.Pair;
import com.patent.web.domain.SimpleResult;
import com.patent.web.project.domain.PoiChart;
import com.patent.web.project.domain.ProjectStep1;
import com.patent.web.project.domain.ProjectStep5;
import com.patent.web.project.domain.ProjectStep5Chart;
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
public class ProjectStep5Controller {
	
	@Autowired PageFactoryService pageFactory;
	@Autowired ProjectService service;
	@Autowired MessageSource message;
	@Value("${tmpfilepath}") String filepath;
	
	
	@PostMapping("/project/step5")
	public String step1(ModelMap map, @RequestParam("id") Integer id) {
		ProjectStep1 ps1 = service.getProjectInfo(id);
		
		map.addAttribute("menuProject", Boolean.TRUE);
		map.addAttribute("state", 5);
		map.addAttribute("projectId", id);
		map.addAttribute("projectinfo", service.getProjectInfo5(id));
		map.addAttribute("dtinfo", service.getPatentDateInfo(ps1.getTableName()));
		
		return "project/step5";
	}
	
	@ResponseBody
	@PostMapping("/project/updateStep5")
	public SimpleResult updateStep5(HttpServletRequest request) {
		ProjectStep5 ps5 = new ProjectStep5();
		File folder = null;
		
		try {
			MultipartHttpServletRequest mtprt = (MultipartHttpServletRequest) request;
			
			ps5.setId(PatentUtiles.getNumberParameter(mtprt, "projectId"));
			ps5.setIpt1(PatentUtiles.getNumberParameter(mtprt, "ipt1"));
			ps5.setIpt2(PatentUtiles.getNumberParameter(mtprt, "ipt2"));
			ps5.setIpt3(PatentUtiles.getNumberParameter(mtprt, "ipt3"));
			ps5.setIpt31(PatentUtiles.getNumberParameter(mtprt, "ipt31"));
			ps5.setIpt4(PatentUtiles.getNumberParameter(mtprt, "ipt4"));
			ps5.setIpt5(PatentUtiles.getNumberParameter(mtprt, "ipt5"));
			ps5.setIpt6(PatentUtiles.getNumberParameter(mtprt, "ipt6"));
			ps5.setIpt61(PatentUtiles.getNumberParameter(mtprt, "ipt61"));
			ps5.setIpt7(PatentUtiles.getNumberParameter(mtprt, "ipt7"));
			ps5.setIpt8(PatentUtiles.getNumberParameter(mtprt, "ipt8"));
			ps5.setIpt9(PatentUtiles.getNumberParameter(mtprt, "ipt9"));
			ps5.setIpt10(PatentUtiles.getNumberParameter(mtprt, "ipt10"));
			
			folder = new File(String.format("%s/%d", filepath, ps5.getId()));
			if (!folder.exists()) {
				folder.mkdirs();
			}
			folder = new File(String.format("%s/%d/charts", filepath, ps5.getId()));
			if (!folder.exists()) {
				folder.mkdirs();
			} else {
				File[] fileList = folder.listFiles(); 
				for (File chart : fileList) {
					chart.delete();
				}
			}

			org.springframework.web.multipart.MultipartFile multipartFile = mtprt.getFile("tmpFl");
			if(multipartFile != null && multipartFile.isEmpty() == false) {
				folder = new File(String.format("%s/%d", filepath, ps5.getId()));
				File[] fileList = folder.listFiles(); 
				for (File chart : fileList) {
					if (!chart.isDirectory())  {
						chart.delete();
					}
				}
				
				ps5.setOrgName(multipartFile.getOriginalFilename());
				ps5.setSaveName(UUID.randomUUID().toString());
				ps5.setFnlName(UUID.randomUUID().toString());
				
				multipartFile.transferTo(new File(String.format("%s/%d/%s", filepath, ps5.getId(), ps5.getSaveName())));
				
				service.updateProjectStep5File(ps5);
			}
			
			service.updateProjectStep5(ps5);
		} catch (Exception e) {
			e.printStackTrace();
			return new SimpleResult().failure();
		}

//		ps5 = service.getProjectInfo5(ps5.getId());
//		if (!PatentUtiles.isEmpty(ps5.getSaveName())) {
//			File ori = new File(String.format("%s/%d/%s", filepath, ps5.getId(), ps5.getSaveName()));
//			if (ori.exists()) {
//				try {
//					ProjectStep1 ps1 = service.getProjectInfo(ps5.getId());
//					PtntChartFactory cf = new PtntChartFactory(String.format("%s/%d", filepath, ps5.getId()));
//					
//					File file = new File(String.format("%s/%d/charts", filepath, ps5.getId()));
//					for (File f : file.listFiles()) {
//						f.delete();
//					}
//					List<String> natList = service.getIptNatList(ps1.getTableName());
//					List<Dataset> clssList = service.getClssInfo(ps1.getTableName());
//					
//					File mody = new File(String.format("%s/%d/%s", filepath, ps5.getId(), ps5.getFnlName()));
//					if (mody.exists()) {
//						mody.delete();
//					}
//					
//					FileOutputStream out = new FileOutputStream(mody);
//					XWPFDocument doc = new XWPFDocument(OPCPackage.open(ori));
//					List<XWPFParagraph> xwpfParas = doc.getParagraphs(); 
//					
//					Map<Integer, Pair<String, String>> map3 = null;
//					Map<Integer, Pair<String, String>> map6 = null;
//
//					PoiChart pc = new PoiChart();
//					for(int i=0;i<xwpfParas.size();i++){   
//				    	XWPFParagraph paragraph = xwpfParas.get(i);
//				    	if (paragraph != null) {
//				    		paragraphRun2(doc, paragraph, pc, ps1.getTableName(), ps5, cf, natList, map3, map6, clssList);
//				    	}
//				    }
//					
//					for (XWPFTable xwpfTable : doc.getTables()) {
//						List<XWPFTableRow> row = xwpfTable.getRows();
//						for (XWPFTableRow xwpfTableRow : row) {
//							List<XWPFTableCell> cells = xwpfTableRow.getTableCells();
//							for (XWPFTableCell cell : cells) {
//								if (cell != null) {
//									for (XWPFParagraph paragraph : cell.getParagraphs()) {
//										paragraphRun2(doc, paragraph, pc, ps1.getTableName(), ps5, cf, natList, map3, map6, clssList);
//									}
//									
//									if (cell.getTables().size() > 0) {
//										tableRun(cell, doc, pc, ps1.getTableName(), ps5, cf, natList, map3, map6, clssList);
//									}
//								}
//							}
//						}
//					}
//					
//					doc.write(out);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		} else {
//			try {
//				Thread.sleep(500);
//			} catch (InterruptedException e) {
//			}
//		}
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
		}
		return new SimpleResult().message(ps5.getOrgName());
	}
	
//	void tableRun(XWPFTableCell pcell, XWPFDocument doc, PoiChart pc, String tableName, ProjectStep5 ps5, PtntChartFactory cf, List<String> natList,
//			Map<Integer, Pair<String, String>> map3, Map<Integer, Pair<String, String>> map6, List<Dataset> clssList) throws Exception {
//		if (pcell.getTables().size() > 0) {
//			for (XWPFTable xwpfiTable : pcell.getTables()) {
//				for (XWPFTableRow xwpfiTableRow : xwpfiTable.getRows()) {
//					for (XWPFTableCell cell : xwpfiTableRow.getTableCells()) {
//						if (cell != null) {
//							for (XWPFParagraph p:cell.getParagraphs()) {
//								paragraphRun2(doc, p, pc, tableName, ps5, cf, natList, map3, map6, clssList);
//							}
//							
//							if (cell.getTables().size() > 0) {
//								tableRun(pcell, doc, pc, tableName, ps5, cf, natList, map3, map6, clssList);
//							}
//						}
//					}
//				}
//			}
//		}
//	}
//	@SuppressWarnings("unused")
//	void paragraphRun2(XWPFDocument doc, XWPFParagraph p, PoiChart pc, String tableName, ProjectStep5 ps5, PtntChartFactory cf, List<String> natList,
//			Map<Integer, Pair<String, String>> map3, Map<Integer, Pair<String, String>> map6, List<Dataset> clssList) throws Exception {
//		List<XWPFRun> runs = p.getRuns();
//		if (runs != null) {
//			for (int i = 0; i < runs.size(); i++) {
//				XWPFRun r = runs.get(i);
//				pc.checkStart(i, r.getText(0));
//				EnChart tmp = EnChart.find(pc.getTmp().toString());
//				if (tmp != null) {
//					int rr = pc.getRun().get(0);
//					for(int dr : pc.getRun()) {
//						p.removeRun(rr);
//					}
//					
//					if (tmp == EnChart.CHART17) {
//						XmlCursor cursor= p.getCTP().newCursor();
//						XWPFTable table = p.getBody().insertNewTbl(cursor); 
//						getChart17(table, tmp, tableName, ps5, cf, natList);
//						doc.removeBodyElement(doc.getPosOfParagraph(p));
//					} else if (tmp == EnChart.CHART8) {
//						if (map3 == null) {
//							map3 = calcDate3List(map3, tableName, ps5);
//						}
//						getChart8(p.createRun(), tmp, tableName, ps5, cf, natList, map3);
//					} else if (tmp == EnChart.CHART9) {
//						if (map3 == null) {
//							map3 = calcDate3List(map3, tableName, ps5);
//						}
//						getChart9(p.createRun(), tmp, tableName, ps5, cf, natList, map3);
//					} else if (tmp == EnChart.CHART14) {
//						if (map6 == null) {
//							map6 = calcDate6List(map6, tableName, ps5);
//						}
//						getChart14(p.createRun(), tmp, tableName, ps5, cf, map6);
//					} else if (tmp == EnChart.CHART15) {
//						if (map6 == null) {
//							map6 = calcDate6List(map6, tableName, ps5);
//						}
//						getChart15(p.createRun(), tmp, tableName, ps5, cf, map6);
//					} else {
//						addChartImage(p.createRun(), tmp, tableName, ps5, cf, natList, clssList);
//					}
//					
//					pc.clear();
//				}
//			}
//		}
//	}
//	
//	void addChartImage(XWPFRun chrtRun, EnChart tmp, String tableName, ProjectStep5 ps5, PtntChartFactory cf, List<String> natList, List<Dataset> clssList) throws Exception {
//		if (tmp == EnChart.CHART1) {
//			getChart1(chrtRun, tmp, tableName, ps5, cf);
//		} else if (tmp == EnChart.CHART2) {
//			getChart2(chrtRun, tmp, tableName, ps5, cf);
//		} else if (tmp == EnChart.CHART3) {
//			getChart3(chrtRun, tmp, tableName, ps5, cf);
//		} else if (tmp == EnChart.CHART4) {
//			getChart4(chrtRun, tmp, tableName, ps5, cf);
//		} else if (tmp == EnChart.CHART5) {
//			getChart5(chrtRun, tmp, tableName, ps5, cf, natList);
//		} else if (tmp == EnChart.CHART6) {
//			getChart6(chrtRun, tmp, tableName, ps5, cf, natList);
//		} else if (tmp == EnChart.CHART7) {
//			getChart7(chrtRun, tmp, tableName, ps5, cf, natList);
//		} else if (tmp == EnChart.CHART10) {
//			getChart10(chrtRun, tmp, tableName, ps5, cf);
//		} else if (tmp == EnChart.CHART11) {
//			getChart11(chrtRun, tmp, tableName, ps5, cf, natList);
//		} else if (tmp == EnChart.CHART12) {
//			getChart12(chrtRun, tmp, tableName, ps5, cf);
//		} else if (tmp == EnChart.CHART13) {
//			getChart13(chrtRun, tmp, tableName, ps5, cf);
//		} else if (tmp == EnChart.CHART14) {
//		} else if (tmp == EnChart.CHART15) {
//		} else if (tmp == EnChart.CHART16) {
//			getChart16(chrtRun, tmp, tableName, ps5, cf, clssList);
//		} else if (tmp == EnChart.CHART17) {
//		} else if (tmp == EnChart.CHART18) {
//			getChart18(chrtRun, tmp, tableName, ps5, cf);
//		} else if (tmp == EnChart.CHART19) {
//			getChart19(chrtRun, tmp, tableName, ps5, cf);
//		} else if (tmp == EnChart.CHART20) {
//			getChart20(chrtRun, tmp, tableName, ps5, cf, natList);
//		} else if (tmp == EnChart.CHART21) {
//			getChart21(chrtRun, tmp, tableName, ps5, cf, clssList);
//		}
//		
//	}
//	
////	연도별 주요 출원국 출원 건수 stack bar / stacked area
//	void getChart1(XWPFRun chrtRun, EnChart tmp, String tableName, ProjectStep5 ps5, PtntChartFactory cf) throws Exception {
//		if (ps5.getIpt1().intValue() == 0) {
//			insertChart(chrtRun, tmp, ps5.getId(), cf.calcStack(cf.getCategoryDataset(service.getIpt11(tableName))).saveImageFile());	
//		} else {
//			insertChart(chrtRun, tmp, ps5.getId(), cf.calcArea(cf.getCategoryDataset(service.getIpt11(tableName))).saveImageFile());	
//		}
//	}
//	
////	주요 출원국 비율
//	void getChart2(XWPFRun chrtRun, EnChart tmp, String tableName, ProjectStep5 ps5, PtntChartFactory cf) throws Exception {
//		insertChart(chrtRun, tmp, ps5.getId(), cf.calcPie(cf.getPieDataset(service.getIpt12(tableName))).saveImageFile());
//	}
//	
////주요 출원국 연도별 특허 동향
//	void getChart3(XWPFRun chrtRun, EnChart tmp, String tableName, ProjectStep5 ps5, PtntChartFactory cf) throws Exception {
//		List<String> ipt13NatList = service.getIpt13NatList(tableName);
//		for(String nat : ipt13NatList) {
//			Pair<DefaultCategoryDataset, DefaultCategoryDataset> pair = cf.getBarAndLineDataset(service.getIpt13(tableName, nat));
//			insertChart(chrtRun, tmp, ps5.getId(), cf.calcBarAndLine(pair.getV2(), pair.getV1(), nat).saveImageFile());
//		}	
//	}
//	
////	출원국 주요 출원국 비율
//	void getChart4(XWPFRun chrtRun, EnChart tmp, String tableName, ProjectStep5 ps5, PtntChartFactory cf) throws Exception {
//		insertChart(chrtRun, tmp, ps5.getId(), cf.calcPie(cf.getPieDataset(service.getIpt21(tableName))).saveImageFile());
//	}
//
////	주요 출원국 내/외국인 출원 비율
//	void getChart5(XWPFRun chrtRun, EnChart tmp, String tableName, ProjectStep5 ps5, PtntChartFactory cf, List<String> natList) throws Exception {
//		for(String nat : natList) {
//			Dataset dt = service.getIpt22(tableName, nat);
//			
//			if (dt.getCnt().intValue() != 0 && dt.getCnt2().intValue() != 0) {
//				List<Dataset> pieDataset = new ArrayList<>();
//				pieDataset.add(new Dataset().clmnkey("내국").cnt(dt.getCnt()));
//				pieDataset.add(new Dataset().clmnkey("외국").cnt(dt.getCnt2()));
//				
//				insertChart(chrtRun, tmp, ps5.getId(), cf.calcPie(cf.getPieDataset(pieDataset), nat).saveImageFile());
//			}
//		}
//	}
//	
////	주요 출원국 외국인 국적 비율
//	void getChart6(XWPFRun chrtRun, EnChart tmp, String tableName, ProjectStep5 ps5, PtntChartFactory cf, List<String> natList) throws Exception {
//		for(String nat : natList) {
//			List<Dataset> list = service.getIpt23(tableName, nat);
//			if (list.size() < 0) {
//				insertChart(chrtRun, tmp, ps5.getId(), cf.calcPie(cf.getPieDataset(list), nat).saveImageFile());
//			}
//		}
//	}
//	
////	주요 출원국 연도별 내/외국인 출원 동황
//	void getChart7(XWPFRun chrtRun, EnChart tmp, String tableName, ProjectStep5 ps5, PtntChartFactory cf, List<String> natList) throws Exception {
//		for(String nat : natList) {
//			List<Dataset> list = service.getIpt24(tableName, nat);
//			if (list.size() > 0) {
//				Pair<DefaultCategoryDataset, DefaultCategoryDataset> pair = cf.getBarAndLineDataset(list, "내국", "외국");
//				insertChart(chrtRun, tmp, ps5.getId(), cf.calcBarAndLine(pair.getV2(), pair.getV1(), nat).saveImageFile());
//			}
//		}
//	}
//	
//	Map<Integer, Pair<String, String>> calcDate3List(Map<Integer, Pair<String, String>> map, String tableName, ProjectStep5 ps5) {
//		map = new HashMap<>();
//		DateTime dt = new DateTime().withMonthOfYear(1).withDayOfMonth(1).withTime(0, 0, 0, 0).plusYears(-1);
//		for (int i=ps5.getIpt2().intValue(); i> 0; i--) {
//			Pair<String, String> pair = new Pair<>();
////			pair.setV2(dt.toString("yyyy")+"1231");
//			pair.setV2(dt.toString("yyyy"));	
//			if (ps5.getIpt31().intValue() == 0 && i==ps5.getIpt2().intValue()) {
////				pair.setV2(dt.plusYears(1).toString("yyyy")+"1231");
//				pair.setV2(dt.plusYears(1).toString("yyyy"));
//			}
//			
//			dt = dt.plusYears(ps5.getIpt3().intValue()*-1);
////			pair.setV1(dt.toString("yyyy")+"0101");
//			pair.setV1(dt.toString("yyyy"));
//			
//			map.put(i, pair);
//			
//			dt = dt.plusYears(-1);				
//		}
//		
//		return map;
//	}
//	
////	전체 주요 출원국의 기술 성장 단계
//	void getChart8(XWPFRun chrtRun, EnChart enChart, String tableName, ProjectStep5 ps5, PtntChartFactory cf, List<String> natList, Map<Integer, Pair<String, String>> dateList) throws Exception {
//		Map<Integer, Dataset> list = new HashMap<>();
//		int maxX=0, maxY=0, maxV=0;
//		DefaultXYZDataset dataset = new DefaultXYZDataset();
//		for (int i=ps5.getIpt2().intValue(); i>0; i--) {
//			Pair<String, String> pair = dateList.get(i);
//			
//			Dataset tmp = service.getIpt31(tableName, pair.getV1(), pair.getV2());
//			if (tmp.getCnt().intValue() > maxX) maxX = tmp.getCnt().intValue();
//			if (tmp.getCnt2().intValue() > maxY) maxY = tmp.getCnt2().intValue();
//			if (tmp.getCnt().intValue()+tmp.getCnt2().intValue() > maxV) maxV = tmp.getCnt().intValue()+tmp.getCnt2().intValue();
//
//			if (tmp.getCnt().intValue() > 0 && tmp.getCnt2().intValue() > 0) {
//				list.put(i, tmp);
//			}
//		}
//		
//		int tmpValue = maxX>maxY?maxX:maxY;
//		for (int i=ps5.getIpt2().intValue(); i>0; i--) {
//			Dataset tmp = list.get(i);
//			if (tmp != null) {
//				double value = (tmp.getCnt().doubleValue() + tmp.getCnt2().doubleValue()) / (maxX + maxY);
//				dataset.addSeries(Integer.toString(i), new double[][] { { tmp.getCnt().doubleValue() },{ tmp.getCnt2().doubleValue() }, { value*tmpValue  } });	
//			}
//		}
//		
//		if (maxX*maxY>0) {
//			insertChart(chrtRun, enChart, ps5.getId(), cf.calcBubble(dataset, maxX, maxY).saveImageFile());
//		}
//	}
//	
////	주요 출원국 기술 성장 단계
//	void getChart9(XWPFRun chrtRun, EnChart enChart, String tableName, ProjectStep5 ps5, PtntChartFactory cf, List<String> natList, Map<Integer, Pair<String, String>> dateList) throws Exception {
//		Map<Integer, Dataset> list = new HashMap<>();
//		for(String nat : natList) {
//			int maxX=0, maxY=0, maxV=0;
//			DefaultXYZDataset dataset = new DefaultXYZDataset();
//			
//			for (int i=ps5.getIpt2().intValue(); i>0; i--) {
//				Pair<String, String> pair = dateList.get(i);
//				Dataset tmp = service.getIpt32(tableName, nat, pair.getV1(), pair.getV2());
//				if (tmp.getCnt().intValue() > maxX) maxX = tmp.getCnt().intValue();
//				if (tmp.getCnt2().intValue() > maxY) maxY = tmp.getCnt2().intValue();
//				if (tmp.getCnt().intValue()+tmp.getCnt2().intValue() > maxV) maxV = tmp.getCnt().intValue()+tmp.getCnt2().intValue();
//				
//				if (tmp.getCnt().intValue()>0 && tmp.getCnt2().intValue()>0) {
//					list.put(i, tmp);
//				}
//			}
//		
//			int tmpValue = maxX>maxY?maxX:maxY;
//			for (int i=ps5.getIpt2().intValue(); i>0; i--) {
//				Dataset tmp = list.get(i);
//				if (tmp != null) {
//					double value = (tmp.getCnt().doubleValue() + tmp.getCnt2().doubleValue()) / (maxX + maxY);
//					dataset.addSeries(Integer.toString(i), new double[][] { { tmp.getCnt().doubleValue() },{ tmp.getCnt2().doubleValue() }, { value*tmpValue  } });
//				}
//			}
//			
//			if (maxX*maxY>0) {
//				insertChart(chrtRun, enChart, ps5.getId(), cf.calcBubble(dataset, nat, "출원인수", "출원건수", maxX, maxY).saveImageFile());
//			}
//		}
//	}
//	
////	전체 주요 출원국의 세부 기술 출원 비율
//	void getChart10(XWPFRun chrtRun, EnChart enChart, String tableName, ProjectStep5 ps5, PtntChartFactory cf) throws Exception {
//		insertChart(chrtRun, enChart, ps5.getId(), cf.calcPie(cf.getPieDataset(service.getIpt41(tableName, null))).saveImageFile());
//	}
//
////	주요 출원국 세부 기술 출원 비율
//	void getChart11(XWPFRun chrtRun, EnChart enChart, String tableName, ProjectStep5 ps5, PtntChartFactory cf, List<String> natList) throws Exception {
//		for(String nat : natList) {
//			insertChart(chrtRun, enChart, ps5.getId(), cf.calcPie(cf.getPieDataset(service.getIpt41(tableName, nat))).saveImageFile());
//		}
//	}
//	
////	전체 기술 분류의 연도별 출원 동향
//	void getChart12(XWPFRun chrtRun, EnChart enChart, String tableName, ProjectStep5 ps5, PtntChartFactory cf) throws Exception {
//		if (ps5.getIpt4() == 0) {
//			insertChart(chrtRun, enChart, ps5.getId(), cf.calcStack(cf.getCategoryDataset(service.getIpt51(tableName))).saveImageFile());
//		} else {
//			insertChart(chrtRun, enChart, ps5.getId(), cf.calcArea(cf.getCategoryDataset(service.getIpt51(tableName))).saveImageFile());	
//		}
//	}
//	
////	세부 기술 분류의 연도별 출원 동향
//	void getChart13(XWPFRun chrtRun, EnChart enChart, String tableName, ProjectStep5 ps5, PtntChartFactory cf) throws Exception {
//		List<Dataset> ipt51ClssList = service.getIpt51ClssInfo(tableName);
//		for(Dataset dataset : ipt51ClssList) {
//			Pair<DefaultCategoryDataset, DefaultCategoryDataset> data = cf.getBarAndLineDataset(service.getIpt52(tableName, dataset.getCnt(), dataset.getRwKey()));
//			insertChart(chrtRun, enChart, ps5.getId(), cf.calcBarAndLine(data.getV2(), data.getV1(), String.format("%s(%s)", dataset.getRwKey(), dataset.getClmnKey())).saveImageFile());
//		}
//	}
//	
//	Map<Integer, Pair<String, String>> calcDate6List(Map<Integer, Pair<String, String>> map, String tableName, ProjectStep5 ps5) {
//		DateTime dt = new DateTime().withMonthOfYear(1).withDayOfMonth(1).withTime(0, 0, 0, 0).plusYears(-1);
//		map = new HashMap<>();
//		for (int i=ps5.getIpt5().intValue(); i>0; i--) {
//			Pair<String, String> pair = new Pair<>();
////			pair.setV2(dt.toString("yyyy")+"1231");
//			pair.setV2(dt.toString("yyyy"));
//			if (ps5.getIpt61().intValue() == 0 && i==ps5.getIpt5().intValue()) {
////				pair.setV2(dt.plusYears(1).toString("yyyy")+"1231");
//				pair.setV2(dt.plusYears(1).toString("yyyy"));
//			}
//			
//			dt = dt.plusYears(ps5.getIpt6().intValue()*-1);
////			pair.setV1(dt.toString("yyyy")+"0101");
//			pair.setV1(dt.toString("yyyy"));
//			
//			map.put(i, pair);
//			
//			dt = dt.plusYears(-1);				
//		}
//		
//		return map;
//	}
//	
////	시간 구간별 세부 기술 분류 점유율 비율
//	void getChart14(XWPFRun chrtRun, EnChart enChart, String tableName, ProjectStep5 ps5, PtntChartFactory cf, Map<Integer, Pair<String, String>> map) throws Exception {
//		for (int i=ps5.getIpt5().intValue(); i> 0; i--) {
//			Pair<String, String> pair = map.get(i);
//			List<Dataset> list = service.getIpt61(tableName, pair.getV1(), pair.getV2());
//			if (list.size()>0) {
//				insertChart(chrtRun, enChart, ps5.getId(), cf.calcPie(cf.getPieDataset(list)).saveImageFile());
//			}
//		}
//	}
//	
////	시간 구간별 세부 기술 분류 점유율 동향
//	void getChart15(XWPFRun chrtRun, EnChart enChart, String tableName, ProjectStep5 ps5, PtntChartFactory cf, Map<Integer, Pair<String, String>> map) throws Exception {
//		DefaultCategoryDataset dataset2 = new DefaultCategoryDataset();
//		for (int i=ps5.getIpt5().intValue(); i> 0; i--) {
//			Pair<String, String> pair = map.get(i);
//			List<Dataset> list2 = service.getIpt62(tableName, pair.getV1(), pair.getV2());
//			for (Dataset data : list2) {
//				dataset2.addValue(data.getCnt(), data.getClmnKey(), Integer.toString(i));
//			}
//		}
//		insertChart(chrtRun, enChart, ps5.getId(), cf.calcArea(dataset2).saveImageFile());
//	}
//
//// 세부 기술별 주요 출원국 구성 비율
//	void getChart16(XWPFRun chrtRun, EnChart enChart, String tableName, ProjectStep5 ps5, PtntChartFactory cf, List<Dataset> clssList) throws Exception {
//		for(Dataset clssId : clssList) {
//			insertChart(chrtRun, enChart, ps5.getId(), cf.calcPie(cf.getPieDataset(service.getIpt71(tableName, clssId.getCnt()))).saveImageFile());
//		}		
//	}
////----- 다시 확인 필요.
////	주요 출원인의 국가별 출원 통계 및 주력 기술 분야
//	void getChart17(XWPFTable table, EnChart enChart, String tableName, ProjectStep5 ps5, PtntChartFactory cf, List<String> natList) throws Exception {
//		Tableset set = new Tableset();
//		set.setTableName(tableName);
//		StringBuffer param = new StringBuffer();
//		for (int i=0; i<natList.size(); i++) {
//			param.append(String.format(", count(*) filter (where tmp.nat='%s') as td%d", natList.get(i), (i+1)));
//		}
//		set.setParam(param.toString());
//		
//		List<Tableset> list = service.getIpt81(set);
//		
//		XWPFTableRow tableRowOne = table.getRow(0);
//		tableRowOne.addNewTableCell();
//		tableRowOne.addNewTableCell();
//		tableRowOne.addNewTableCell();
//		for (int i=0; i<natList.size(); i++) {
//			tableRowOne.addNewTableCell();	
//		}
//		XWPFTableRow tableRowTwo = table.createRow();
//		
//		
//		tableRowOne.getCell(0).setText("출원인");
//	    tableRowOne.getCell(1).setText("출원인 국적");
//	    XWPFRun cpr = tableRowOne.getCell(2).getParagraphs().get(0).createRun();
//	    cpr.setText("주요IP");
//		cpr.addBreak();
//		cpr.setText("출원국(건수,%)");
//		tableRowOne.getCell(3+natList.size()).setText("주력 기술분야");
//		
//	    for (int i=0; i<natList.size(); i++) {
//	    	tableRowTwo.getCell(i+2).setText(natList.get(i));
//        }
//	    tableRowTwo.getCell(natList.size()+2).setText("IP 출원국 종합");
//	    
//	    Class<Tableset> clssTableset = Tableset.class;
//	    
//	    int cellId = 0;
//	    for (Tableset row : list) {
//	    	XWPFTableRow tableRow = table.createRow();
//	    	cellId = 0;
//	    	tableRow.getCell(cellId++).setText(row.getAplct());
//	    	
//	    	tableRow.getCell(cellId++).setText("");
//	        Pair<Integer, String> tmp = new Pair<>();
//			for (int i=0; i<natList.size(); i++) {
//	        	Field field = clssTableset.getDeclaredField(String.format("td%d", i+1));
//        		field.setAccessible(true);
//        		Integer cnt = (Integer) field.get(row);
//        		if (i == 0) {
//	        		tmp.setV1(cnt);
//	        		tmp.setV2(natList.get(i));
//        		} else if (cnt.intValue() > tmp.getV1().intValue()) {
//        			tmp.setV1(cnt);
//	        		tmp.setV2(natList.get(i));
//        		}
//
//        		cpr = tableRow.getCell(cellId++).getParagraphs().get(0).createRun();
//        		cpr.setText(String.format("%d", cnt));
//        		cpr.addBreak();
//        		cpr.setText(String.format("(%.2f)", cnt.doubleValue()/row.getCnt().doubleValue()*100));
//	        }
//			tableRow.getCell(cellId++).setText(tmp.getV2());
//			tableRow.getCell(cellId).setText(row.getClss());
//	    }
//		
//		mergeCellVertically(table, 0, 0, 1);
//		mergeCellVertically(table, 1, 0, 1);
//		mergeCellVertically(table, 3+natList.size(), 0, 1);
//		mergeCellHorizontally(table, 0, 2, 2+natList.size());
//	}
//
//	void mergeCellVertically(XWPFTable table, int col, int fromRow, int toRow) {
//		for (int rowIndex = fromRow; rowIndex <= toRow; rowIndex++) {
//			XWPFTableCell cell = table.getRow(rowIndex).getCell(col);
//			CTVMerge vmerge = CTVMerge.Factory.newInstance();
//			if (rowIndex == fromRow) {
//				vmerge.setVal(STMerge.RESTART);
//			} else {
//				vmerge.setVal(STMerge.CONTINUE);
//				for (int i = cell.getParagraphs().size(); i > 0; i--) {
//					cell.removeParagraph(0);
//				}
//				cell.addParagraph();
//			}
//			CTTcPr tcPr = cell.getCTTc().getTcPr();
//			if (tcPr == null)
//				tcPr = cell.getCTTc().addNewTcPr();
//			tcPr.setVMerge(vmerge);
//		}
//	}
//
//	void mergeCellHorizontally(XWPFTable table, int row, int fromCol, int toCol) {
//		XWPFTableCell cell = table.getRow(row).getCell(fromCol);
//		CTTcPr tcPr = cell.getCTTc().getTcPr();
//		if (tcPr == null) {
//			tcPr = cell.getCTTc().addNewTcPr();
//		}
//		if (tcPr.isSetGridSpan()) {
//			tcPr.getGridSpan().setVal(BigInteger.valueOf(toCol - fromCol + 1));
//		} else {
//			tcPr.addNewGridSpan().setVal(BigInteger.valueOf(toCol - fromCol + 1));
//		}
//		for (int colIndex = toCol; colIndex > fromCol; colIndex--) {
//			table.getRow(row).getCtRow().removeTc(colIndex);
//		}
//	}
//	
//	
////	주요 출원인 출원 건수
//	void getChart18(XWPFRun chrtRun, EnChart enChart, String tableName, ProjectStep5 ps5, PtntChartFactory cf) throws Exception {
//		List<Dataset> list = service.getIpt91(tableName, ps5.getIpt8());
//		if (list.size() > 0) {
//			insertChart(chrtRun, enChart, ps5.getId(), cf.calcBar(cf.getCategoryDataset(list), null, null, null).saveImageFile());
//		}
//	}
//	
////	주요 출원인 삼극 특허 보유 현황
//	void getChart19(XWPFRun chrtRun, EnChart enChart, String tableName, ProjectStep5 ps5, PtntChartFactory cf) throws Exception {
//		List<Dataset> list = service.getIpt92(tableName, ps5.getIpt8());
//		if (list.size() > 0) {
//			insertChart(chrtRun, enChart, ps5.getId(), cf.calcStack(cf.getIpt91Dataset(list), PlotOrientation.HORIZONTAL).saveImageFile());
//		}
//	}
//	
////	국가별 주요 출원인 출원 건수
//	void getChart20(XWPFRun chrtRun, EnChart enChart, String tableName, ProjectStep5 ps5, PtntChartFactory cf, List<String> natList) throws Exception {
//		for (String nat : natList) {
//			List<Dataset> list = service.getIpt101(tableName, nat, ps5.getIpt9());
//			if (list.size() > 0) {
//				insertChart(chrtRun, enChart, ps5.getId(), cf.calcBar(cf.getCategoryDataset(list), nat).saveImageFile());
//			}
//		}
//	}
//	
////	세부 기술별 주요 출원인 출원 건수
//	void getChart21(XWPFRun chrtRun, EnChart enChart, String tableName, ProjectStep5 ps5, PtntChartFactory cf, List<Dataset> clssList) throws Exception {
//		for(Dataset clssId : clssList) {
//			List<Dataset> list = service.getIpt102(tableName, clssId.getCnt(), ps5.getIpt9());
//			if (list.size() > 0) {
//				insertChart(chrtRun, enChart, ps5.getId(), cf.calcBar(cf.getCategoryDataset(list), clssId.getClmnKey()).saveImageFile());
//			}
//		}
//	}
//	
//	void insertChart(XWPFRun chrtRun, EnChart tmp, Integer id, String img) throws Exception {
//		chrtRun.addPicture(new BufferedInputStream(new FileInputStream(String.format("%s/%d/charts/%s.jpeg", filepath, id, img, tmp.name))), Document.PICTURE_TYPE_JPEG, tmp.name, Units.toEMU(1000), Units.toEMU(300));
//	}
//	
//	String getChartName(ProjectStep5Chart chart, String fldName) throws Exception {
//	Class<ProjectStep5Chart> cls = ProjectStep5Chart.class;
//	String result = "";
//	
//	Field field = cls.getDeclaredField(fldName);
//	if (field != null) {
//		field.setAccessible(true);
//		result = (String) field.get(chart);
//	}
//	return result;
//}
	
	@ResponseBody
	@PostMapping("/project/updateStep5/download/{id}")
    public void downloadEquipMeasurement(HttpServletRequest req, HttpServletResponse res, @PathVariable Integer id) {
		ProjectStep5 ps5 = service.getProjectInfo5(id);
		try {
			PtntChartFactory.downloadExcel(req, res, String.format("%s/%d/%s", filepath, id, ps5.getFnlName()), ps5.getOrgName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	enum EnChart {
		CHART1("$$$$IPT1$$$", "ipt1", "image1", "연도별 주요 출원국 출원 건수"),
		CHART2("$$$$IPT2$$$", "ipt2", "image2", "주요 출원국 비율"),
		CHART3("$$$$IPT3$$$", "ipt3", "image3", "주요 출원국 연도별 특허 동향"),
		CHART4("$$$$IPT4$$$", "ipt4", "image4", "출원국 주요 출원국 비율"),
		CHART5("$$$$IPT5$$$", "ipt5", "image5", "주요 출원국 내/외국인 출원 비율"),
		CHART6("$$$$IPT6$$$", "ipt6", "image6", "주요 출원국 외국인 국적 비율"),
		CHART7("$$$$IPT7$$$", "ipt7", "image7", "주요 출원국 연도별 내/외국인 출원 동황"),
		CHART8("$$$$IPT8$$$", "ipt8", "image8", "전체 주요 출원국의 기술 성장 단계"),
		CHART9("$$$$IPT9$$$", "ipt9", "image9", "주요 출원국 기술 성장 단계"),
		CHART10("$$$$IPT10$$$", "ipt10", "image10", "전체 주요 출원국의 세부 기술 출원 비율"),
		CHART11("$$$$IPT11$$$", "ipt11", "image11", "주요 출원국 세부 기술 출원 비율"),
		CHART12("$$$$IPT12$$$", "ipt12", "image12", "전체 세부 기술의 연도별 출원 동향"),
		CHART13("$$$$IPT13$$$", "ipt13", "image13", "세부 기술 분류의 연도별 출원 동향"),
		CHART14("$$$$IPT14$$$", "ipt14", "image14", "시간 구간별 세부 기술 분류 점유율 비율"),
		CHART15("$$$$IPT15$$$", "ipt15", "image15", "시간 구간별 세부 기술 분류 점유율 동향"),
		CHART16("$$$$IPT16$$$", "ipt16", "image16", "세부 기술별 주요 출원국 구성 비율"),
		CHART17("$$$$IPT17$$$", "ipt17", "image17", "주요 출원인의 국가별 출원 통계 및 주력 기술 분야"),
		CHART18("$$$$IPT18$$$", "ipt18", "image18", "주요 출원인 출원 건수"),
		CHART19("$$$$IPT19$$$", "ipt19", "image19", "주요 출원인 삼극 특허 보유 현황"),
		CHART20("$$$$IPT20$$$", "ipt20", "image20", "국가별 주요 출원인 출원 건수"),
		CHART21("$$$$IPT21$$$", "ipt21", "image21", "세부 길술별 주요 출원인 출원 건수");
		
		EnChart(String tag, String name, String param, String title) {
			this.tag = tag;
			this.name = name;
			this.param = param;
			this.title = title;
		}
		
		String tag;
		String name;
		String param;
		String title;
		
		public static EnChart find(String tag) {
			for (EnChart ec : values()) {
				if (tag.equals(ec.tag)) {
					return ec;
				}
			}
			
			return null;
		}
	}
}