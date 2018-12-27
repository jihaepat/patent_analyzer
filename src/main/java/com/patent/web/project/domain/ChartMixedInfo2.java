package com.patent.web.project.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.patent.web.domain.Pair;

import lombok.Getter;
import lombok.Setter;

public class ChartMixedInfo2 extends ChartInfo {
	@Getter @Setter List<String> label;
	@Getter @Setter Map<Pair<Integer, String>, Map<String, Pair<List<Integer>, List<Integer>>>> dataSet;
	
	public ChartMixedInfo2() {
		label = new ArrayList<>();
		dataSet = new HashMap<>();
	}
}
