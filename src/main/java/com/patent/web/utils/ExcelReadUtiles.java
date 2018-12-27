package com.patent.web.utils;

import java.io.File;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.joda.time.DateTime;

import com.patent.web.domain.PatentFile;
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

public class ExcelReadUtiles {
	
	public ExcelReadUtiles(PatentFile file, String filepath, Integer projectId, List<CLMN> totalClmns) {
		list = new ArrayList<>();
		clmns = new ArrayList<>();
		
		this.totalClmns = totalClmns;
		
		try {
			parse(file, filepath, projectId);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}
	
	public List<Patent> getList() {
		return list;
	}
	public List<CLMN> getClmns() {
		return clmns;
	}
	
	List<CLMN> totalClmns = null;
	List<Patent> list = null;
	List<CLMN> clmns = null;
	Class<Patent> clsPatent = Patent.class;
	Workbook workbook = null;
	void parse(PatentFile file, String filepath, Integer projectId) throws Exception {
		int i=0;
		workbook = WorkbookFactory.create(new File(String.format("%s/%s", filepath, file.getSavename())));
		
		if (workbook != null) {
			Sheet sheet = workbook.getSheetAt(0);
			if (sheet != null) {
				boolean isFind = false;
				for (Row row : sheet) {
					if (row.getRowNum() == 0) {
						for (i=0; i<row.getPhysicalNumberOfCells(); i++) {
							isFind = false;
							Cell cell = row.getCell(i);
							String tmp = getCellValueAsString(cell);
							for (int k=totalClmns.size()-1; k>=0; k--) {
								CLMN clmn = totalClmns.get(k);
								if (checkClumnName(tmp, clmn.getName())) {
									isFind = true;
									clmn.setOrder(i);
									clmns.add(clmn);
									break;
								}
							}
							if (!isFind) {
								clmns.add(null);
							}
						}
					} else {
						Patent pt = new Patent();
						for (i=0; i<row.getPhysicalNumberOfCells(); i++) {
							CLMN clmn = clmns.get(i);
							if (clmn != null) {
								Field field = clsPatent.getDeclaredField(clmn.getMthd());
								if (field != null) {
									field.setAccessible(true);
									if ("S".equals(clmn.getType())) {
										field.set(pt, getCellValueAsString(row.getCell(i)));
										
										if (clmn.getId() == 13) {
											pt.setAPPNMURL(getAddress(row.getCell(i)));
										}
									} else if ("I".equals(clmn.getType())) {
										field.set(pt, getCellValue(row.getCell(i)));
									} else if ("A".equals(clmn.getType())) {
										field.set(pt, getAddress(row.getCell(i)));
									}
								}
							}
						}
						
						list.add(pt);
					}
				}
			}
		}
	}
	public static boolean checkClumnName(String clmn, String code) {
		String tmpString = clmn.replaceAll("\\p{Z}", "");
		if (code.length() > tmpString.length()) {
			return false;
		}
//		String rearString = tmpString.substring(code.length()).toUpperCase();
//		String frontString = tmpString.substring(0, code.length()).toUpperCase();

		tmpString = tmpString.substring(0, code.length()).toUpperCase();
		return tmpString.equals(code);
	}
	
	public static enum EnDCT {
		A(2, 6, 0), U(2, 6, 10),
		B(3, 6, 0), Y(3, 6, 10), C(3, 6, 20),
		S(1, 1, 30),
		T(1, 1, 30),
		NONE(1, 1, 100);
		
		int type;
		int rank;
		int weight;
		EnDCT(int t, int r, int w) {
			type = t;
			rank = r;
			weight = w;
		}
		
		public static EnDCT findDCT(String DCT) {
			for (EnDCT en : values()) {
				if (DCT.equals(en.toString())) {
					return en;
				}
			}
			
			return NONE;
		}
	}
	public static enum EnRT {
		T, F, S, NONE;
	}
	public static EnRT checkDCT(int type, String v1, String v2) {
		try {
			EnDCT dct1 = EnDCT.findDCT(v1.substring(0, 1)), dct2 = EnDCT.findDCT(v2.substring(0, 1));
			int version1 = (!PatentUtiles.isEmpty(v1.substring(1)) ? Integer.parseInt(v1.substring(1)) : -1), 
					version2 = (!PatentUtiles.isEmpty(v2.substring(1)) ? Integer.parseInt(v2.substring(1)) : -1);
			
			if (dct1 == EnDCT.NONE && dct2 == EnDCT.NONE) {
				return EnRT.NONE;
			} else if (dct1 != EnDCT.NONE && dct2 == EnDCT.NONE) {
				return EnRT.F;
			} else if (dct1 == EnDCT.NONE && dct2 != EnDCT.NONE) {
				return EnRT.T;
			} else {
				boolean is1=type%dct1.type==0,is2=type%dct2.type==0;
				if (is1 && is2) {
					if (dct1.rank < dct2.rank) {
						return EnRT.T;
					} else if (dct1.rank == dct2.rank) {
						if (dct1.type != dct2.type) {
							return EnRT.S;
						} else if (dct1.weight > dct2.weight) {
							return EnRT.T;
						} else if (dct1.weight == dct2.weight) {
							if (version1 < version2) {
								return EnRT.T;
							}
						}
					}
				} else if (!is1 && is2) {
					return EnRT.T;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return EnRT.F;
	}
	
	void close() {
		try {
			if (workbook != null)	workbook.close();
		} catch (Exception e) {
		}
	}
	
	public static String getCellValueAsString(Cell cell) throws Exception {
		String str = null;
		if (cell != null) {
			if (cell.getCellTypeEnum() == CellType.NUMERIC) {
				if (DateUtil.isCellDateFormatted(cell)) {
            		str = new DateTime(cell.getDateCellValue()).toString("yyyy-MM-dd");
            	} else {
            		Double value = cell.getNumericCellValue();
            		if (checkPoint(value, 0)) {
            			Long longValue = value.longValue();
                        str = new String(longValue.toString());
            		} else {
            			str = value.toString();	
            		}
            	}
			} else if (cell.getCellTypeEnum() == CellType.BOOLEAN) {
				str = String.format("%B", cell.getBooleanCellValue());
			} else if (cell.getCellTypeEnum() == CellType.STRING) {
				str = cell.getStringCellValue();
			} else {
				str = "";
			}
		}
		
		if (!PatentUtiles.isEmpty(str))
			return str.trim();
		
		return str;
	}
	
	public static String getAddress(Cell cell) throws Exception {
		Hyperlink link = cell.getHyperlink();
		if (link != null) {
			return link.getAddress();
		}
		return null;
	}
	
	public static Integer getCellValue(Cell cell) throws Exception {
		if (cell != null) {
			if (cell.getCellTypeEnum() == CellType.NUMERIC) {
				if (DateUtil.isCellDateFormatted(cell)) {
				} else {
					Double value = cell.getNumericCellValue();
					if (checkPoint(value, 0)) {
	        			Long longValue = value.longValue();
	        			return longValue.intValue();
	        		}
				}
			}
		}
		
		return null;
	}
	
	public static String calcESFLN(String value) {
		String[] tmp = value.split("|");
		if (tmp == null || tmp.length == 0) {
			return null;
		}
		
		StringBuffer strBff = new StringBuffer("{");
		for(int i=0; i<tmp.length; i++) {
			strBff.append(tmp[i]);
			if (i != tmp.length-1) {
				strBff.append(",");	
			}
		}
		strBff.append("}");
		
		return strBff.toString();
	}
	
	static boolean checkPoint(Double value, Integer decimalPlaces) {
		BigDecimal decimal = new BigDecimal(value);
		BigDecimal checkDecimal = decimal.movePointRight(decimalPlaces);
		return checkDecimal.scale() == 0;
	}

}
