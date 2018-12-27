package com.patent.web.project.domain;

import lombok.Getter;
import lombok.Setter;

public class ChartInfo {
	@Getter @Setter String name;
	
	public ChartInfo name(String name) {
		this.name = name;
		return this;
	}
}
