package com.patent.web.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.patent.web.project.domain.CLMN;
import com.patent.web.project.domain.Patent;

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

public class ExcelWriteUtiles {
	List<Patent> dataList;
	
	Integer clss;
	List<CLMN> clmnList;
	Class<Patent> clsPatent = Patent.class;
	
	public ExcelWriteUtiles(Integer clss, List<Patent> dataList, List<CLMN> clmnList) {
		this.clss = clss;
		this.dataList = dataList;
		this.clmnList = clmnList;
	}
	
	public String makeFile(String tmpfilepath) throws Exception {
		Object obj = null;
		Workbook workbook = new XSSFWorkbook();
		try {
			Sheet sheet = workbook.createSheet("data");
			
			Row row = null;
            Cell cell = null;
            int rowCount = 0;
            int cellCount = 0;
			
            Font headFont = workbook.createFont();
            headFont.setFontHeightInPoints((short) 11);
            headFont.setBold(true);
            
            CellStyle headStyle = workbook.createCellStyle();
            headStyle.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.index);
            headStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headStyle.setAlignment(HorizontalAlignment.CENTER);
            headStyle.setFont(headFont);
            headStyle.setBorderBottom(BorderStyle.THIN);
            headStyle.setBorderLeft(BorderStyle.THIN);
            headStyle.setBorderRight(BorderStyle.THIN);
            headStyle.setBorderTop(BorderStyle.THIN);
            
            Font bodyFont = workbook.createFont();
            bodyFont.setFontHeightInPoints((short) 9);

            CellStyle bodyStyle = workbook.createCellStyle();
            bodyStyle.setFont(bodyFont);
            bodyStyle.setBorderBottom(BorderStyle.THIN);
            bodyStyle.setBorderLeft(BorderStyle.THIN);
            bodyStyle.setBorderRight(BorderStyle.THIN);
            bodyStyle.setBorderTop(BorderStyle.THIN);

            row = sheet.createRow(rowCount++);
            if (clss.intValue() > 1) {
            	sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, clss.intValue()-1));
            }
            cell = row.createCell(cellCount++);
            cell.setCellStyle(headStyle);
        	cell.setCellValue("기술분류");
        	cellCount += clss.intValue()-1;
        	
            for (CLMN clmn : clmnList) {
            	cell = row.createCell(cellCount++);
            	cell.setCellStyle(headStyle);
            	cell.setCellValue(clmn.getName());
            }
            
            if (dataList != null && dataList.size() > 0) {
	            for (Patent pt : dataList) {
	            	cellCount = 0;
	            	row = sheet.createRow(rowCount++);
	            	
	            	cell = row.createCell(cellCount++);
	            	cell.setCellStyle(bodyStyle);
	            	cell.setCellValue(pt.getStrclss1());
	            	
	            	if (clss.intValue() > 1) {
		            	cell = row.createCell(cellCount++);
		            	cell.setCellStyle(bodyStyle);
		            	cell.setCellValue(pt.getStrclss2());
	            	}
	            	
	            	if (clss.intValue() > 2) {
		            	cell = row.createCell(cellCount++);
		            	cell.setCellStyle(bodyStyle);
		            	cell.setCellValue(pt.getStrclss3());
	            	}
	            	
	            	for (CLMN clmn : clmnList) {
	            		cell = row.createCell(cellCount++);
	            		cell.setCellStyle(bodyStyle);
	            		Field field = clsPatent.getDeclaredField(clmn.getMthd());
	            		field.setAccessible(true);
	            		obj = field.get(pt);
	            		
	            		if (obj != null) {
		            		if (String.class.isAssignableFrom(field.getType())) {
		            			cell.setCellValue((String) obj);
		            		} else if (Integer.class.isAssignableFrom(field.getType())) {
	            				cell.setCellValue((Integer) obj);
		            		}
	            		}
	            	}
	            }
            }
            
            for (int i=0; i<clmnList.size(); i++) {
            	sheet.setColumnWidth(i, 6528);
            }
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("엑셀파일 데이터 생생 중 오류가 발생했습니다.");
		}
		
		String filefullpath = String.format("%s/%s", tmpfilepath, UUID.randomUUID().toString());
		try (FileOutputStream fileoutputstream = new FileOutputStream(filefullpath)) {
			if (workbook != null) {
				workbook.write(fileoutputstream);
			}
		} catch (Exception e) {
			filefullpath = null;
			e.printStackTrace();
			throw new Exception("엑셀파일 생생 중 오류가 발생했습니다.");
		} finally {
			try {
				workbook.close();
			} catch (IOException e) {
			}
		}
		return filefullpath;
	}
}
