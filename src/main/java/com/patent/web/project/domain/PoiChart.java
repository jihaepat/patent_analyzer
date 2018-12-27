package com.patent.web.project.domain;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PoiChart {
	
	public PoiChart() {
		clear();
	}

	StringBuffer tmp;
	List<Integer> run;
	
	public void checkStart(int i, String text) {
		if (text == null) {
			clear();
		} else {
			for (int c=0;c<text.length();c++) {
				char ch = text.charAt(c);
				
	//			$IPT0~9
				if (tmp.length() == 0) {
					if (ch == '$') {
						tmp.append(ch);
						if (!run.contains(i)) run.add(i);
					}
				} else {
					if (ch == '$' || ch == 'I' || ch == 'P' || ch == 'T' || (ch>47 && ch<58)) {
						tmp.append(ch);
						if (!run.contains(i)) run.add(i);
					} else {
						clear();
					}	    						
				}
			}
		}
	}
	
	public void clear() {
		tmp = new StringBuffer();
		run = new ArrayList<>();
	}
}
