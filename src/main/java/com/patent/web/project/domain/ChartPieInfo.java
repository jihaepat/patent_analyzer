package com.patent.web.project.domain;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class ChartPieInfo extends ChartInfo {
	@Getter @Setter List<String> label;
	@Getter @Setter List<String> dataSet;
	
	public ChartPieInfo() {
		name = "";
		label = new ArrayList<>();
		dataSet = new ArrayList<>();
	}
}
