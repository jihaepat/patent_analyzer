package com.patent.web.project.domain;

import com.patent.web.utils.PatentUtiles;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


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

@ToString
@Getter
@Setter
public class Patent {
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
	        return false;
	    }
	    if (!Patent.class.isAssignableFrom(obj.getClass())) {
	        return false;
	    }
	    final Patent other = (Patent) obj;
	    if (this.DCT.equals(other.getDCT())) {
	    	return true;
	    }
	    return false;
	}
	@Override
	public int hashCode() {
	    int hash = 159 + (this.DCT != null ? this.DCT.hashCode() : 0);
	    return hash;
	}
	
	public void setInfo(Patent obj) {
		this.id = obj.id;
		this.DCT = obj.DCT;
	}
	
	public String getStrClss() {
		if (!PatentUtiles.isEmpty(strclss3)) {
			return String.format("%s-%s-%s", strclss1, strclss2, strclss3);
		} else if (!PatentUtiles.isEmpty(strclss2)) {
			return String.format("%s-%s", strclss1, strclss2);
		} else {
			return strclss1;
		}
	}
	
	protected String classification;
	protected Integer projectId;
	protected Integer parentId;
	protected Integer fileId;
	protected Integer clssId;
	protected Integer id;
	protected Integer status;

	protected Float scr;
	protected Float idxrght;
	protected Float idxcntnrght;
	protected Float idxprrty;
	protected Float idxqtd;
	protected Float idxfmly;
	protected Float idxclm;
	
	protected String strclss1;
	protected String strclss2;
	protected String strclss3;
	
	protected String tableName;
	
//	국가코드
	protected String NAT;
	
//	DB종류
	protected String DBT;
	
//	특허/실용 구분
	protected String PTT;
	
//	문헌종류 코드
	protected String DCT;
	
//	발명의 명칭
	protected String NMITN;
	
//	발명의 명칭(제2언어)
	protected String NMITN2;
	
//	요약
	protected String RMKS;
	
//	요약(제2언어)
	protected String RMKS2;
	
//	대표청구항
	protected String RTVCLS;
	
//	대표청구항(제2언어)
	protected String RTVCLS2;
	
//	청구항 수
	Integer CLNM;
	
//	문헌 메모
	protected String MEMO;
	
//	출원번호
	protected String APPNM;
	protected String APPNMURL;
	
//	출원일
	protected String DTRL;
	
//	번역문제출일
	protected String TRNSDTSB;
	
//	공개번호/공표/재공표
	protected String PUBNM;
	
//	공개일
	protected String PUBDT;
	
//	공고번호
	protected String ANMTNM;
	
//	공고일
	protected String ANMTDT;
	
//	등록번호
	protected String RGSTNM;
	
//	등록일
	protected String RGSTDT;
	
//	발행일
	protected String PBLSDT;
	
//	출원인
	protected String APLCT;
	protected String oriAPLCT;
	protected Integer modyCntAPLCT;
	
//	출원인(제2언어)
	protected String APLCT2;
	
//	출원인 국적
	protected String APLCTNAT;
	
//	출원인 수
	protected Integer APLCTNM;
	
//	출원인 대표명화 코드
	protected String CRPA;
	
//	출원인 대표명화 명칭(영문)
//	출원인 대표명화 영문명
	protected String CRPAEN;
	
//	출원인 대표명화 명칭(국문)
//	출원인 대표명화 국문명
	protected String CRPAKO;
	
//	원문상 출원인
	protected String ORGAPLCT;
	
//	출원인 식별기호
	protected String APLCTIDNT;
	
//	발명자/고안자
	protected String INV;
	
//	발명자(제2언어)
	protected String INV2;
	
//	발명자/고안자 국적
	protected String INVNAT;
	
//	발명자 수
	protected Integer INVNM;
	
//	대리인
	protected String AGNT;
	
//	우선권 번호
	protected String PRTNM;
	
//	우선권 국가
	protected String PRTNAT;
	
//	우선권 주장일
	protected String PRTDT;
	
//	최우선출원번호
	protected String HGSTPRAPPNM;
	
//	최우선출원국가
	protected String HGSTPRNAT;
	
//	최우선출원일
	protected String HGSTPRDTRL;
	
//	국제 출원번호
	protected String INTAPPNM;
	
//	국제 출원일
	protected String INTDTRL;
	
//	국제 공개번호
	protected String INTPUBNM;
	
//	국제 공개일
	protected String INTPUBDT;
	
//	지정국 코드
	protected String DSNGNATCD;
	
//	EPC지정국
	protected String EPCDSNGNATCD;
	
//	Original CPC Main
	protected String ORGCPCMN;
	
//	Original CPC All
	protected String ORGCPCALL;
	
//	Original IPC Main
	protected String ORGIPCMN;
	
//	Original IPC All
	protected String ORIIPCALL;
	
//	Original US Class Main
	protected String ORGUSCSMN;
	
//	Original US Class All
	protected String ORGUSCSALL;
	
//	Original FI
	protected String ORGFI;
	
//	Original F-term
	protected String FTRM;
	
//	Original Theme Code
	protected String ORGTHMCD;
	
//	Current CPC Main
	protected String CRRTCPCMN;
	
//	Current CPC All
	protected String CRRTCPCALL;
	
//	Current IPC Main
	protected String CRRTIPCMN;
	
//	Current IPC All
	protected String CRRTIPCALL;
	
//	Current US Class Main
	protected String CRRTUSCSMN;
	
//	Current US Class All
	protected String CRRTUSCSALL;
	
//	Currnet FI
	protected String CRRTFI;
	
//	Current F-term
	protected String CRRTFTRM;
	
//	인용 문헌 수(B1)
	protected Integer QTNNMB1;
	
//	인용 문헌번호 (B1) + 심사관(E) 인용
	protected String QTNNMEXM;
	
//	심사관인용문헌(B1)
	protected String QTNEXMB1;
	
//	비 특허 참고문헌(B1)
	protected String NSPCRFS;
	
//	인용 문헌 수(F1)
	Integer QTNNMF1;
	
//	인용 문헌번호 (F1) + 심사관(E) 인용
	protected String QTNNMFE;
	
//	심사관인용문헌(F1)
	protected String QTNEXMF1;
	
//	EPO심플패밀리 문헌번호
//	EPO심플패밀리 문헌번호 (출원기준)
	protected String EPOAPPNM;
	
//	EPO심플패밀리 개별국문헌수 (출원기준)
//	EPO심플패밀리 개별국 문헌 수
	protected String EPOFMLYDOCNM;
	
//	패밀리 Basic Patent 문헌번호
	protected String FMLYBPAPPNM;
	
//	WIPS패밀리 문헌번호(출원기준)
//	패밀리 문헌번호 (출원기준)
	protected String FMLYAPPNM;
	
//	WIPS패밀리 문헌 수(출원기준)
//	패밀리 문헌 수 (출원기준)
	protected String FMLYNM;
	
	
//	WIPS패밀리 개별국 문헌 수(출원기준)
//	패밀리 개별국 문헌 수(출원기준)
	protected String FMLYNATDOCNM;
	
//	WIPS패밀리 국가 수 (출원기준)
//	패밀리 국가 수(출원기준)
	protected String FMLYNATNM;
	
//	상태정보(최종처분)
//	상태정보
	protected String STS;
	
//	상태정보[US등록문헌]
	protected String USSTS;
	
//	존속기간(예정)만료일
//	존속기간(예상)만료일
	protected String DSED;
	
//	현재권리자
	protected String CRTATH;
	
//	현재권리자(제2언어)
	protected String CRTATH2;
	
//	현재권리자 대표명화 코드
	protected String CARFC;
	
//	현재 권리자 대표명화 명칭(영문)
//	현재권리자 대표명화 영문명
	protected String NCRRNEN;
	
//	현재 권리자 대표명화 명칭(국문)
//	현재권리자 대표명화 국문명
	protected String NCRRNKR;
	
//	DOCDB 법적상태
	protected String DOCDB;
	
//	원문링크
	protected String ORGLK;
	
//	번역문 링크
//	번역원문 링크
	protected String LKORGTXT;
	
	
//	상세보기 링크
//	상세보기 링크(비로그인)
	protected String VWLK;
	
//	상세보기 링크(로그인)
	protected String VWLKLGN;
	
//	대표도면
	protected String REPDRW;
	
//	개별도면 수
	protected String DRWNM;
	
//	사용자 분류 태그
	protected String USRCLSFTAG;
	
//	내/외국인 출원여부
	protected String ISAPPNM;
	
//	정정공보 존재 유무
	protected String ISBLLTN;
	
//	Wintelips key
	protected String WNSKY;
	


	
//	인용 문헌번호 (B1) + 심사관(BE) 인용
	protected String QTNEXMBEXM;
//	인용 문헌번호 (F1) + 심사관(FE) 인용
	protected String QTNNMFFE;
//	심사청구여부
	protected String WHRQRVW;

}
