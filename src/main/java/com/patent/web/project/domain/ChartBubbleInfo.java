package com.patent.web.project.domain;

import java.util.HashMap;
import java.util.Map;

import com.patent.web.domain.Pair;

import lombok.Getter;
import lombok.Setter;

public class ChartBubbleInfo extends ChartInfo {
	@Getter @Setter Map<String, Pair<String, String>> dataSet;
	
	public ChartBubbleInfo() {
		name = "";
		dataSet = new HashMap<>();
	}
}
