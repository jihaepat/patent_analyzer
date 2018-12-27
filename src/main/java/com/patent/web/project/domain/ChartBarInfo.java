package com.patent.web.project.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

public class ChartBarInfo extends ChartInfo {
	@Getter @Setter List<String> label;
	@Getter @Setter Map<String, List<String>> dataSet;
	
	public ChartBarInfo() {
		name = "";
		label = new ArrayList<>();
		dataSet = new HashMap<>();
	}
}