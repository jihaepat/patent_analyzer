package com.patent.web.chart;

import java.awt.Color;
import java.awt.Paint;

import org.jfree.chart.plot.DefaultDrawingSupplier;



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

public class ChartDrawingSupplier extends DefaultDrawingSupplier {

	private static final long serialVersionUID = 1L;

	public Paint[] sequence;
	public int index;
	public int fillIndex;

	{
		sequence =  new Paint[] {
	        new Color(68, 114, 196),
	        new Color(237, 125, 50),
	        new Color(165, 165, 165),
	        new Color(254, 192, 1),
	        new Color(91, 155, 213),
	        new Color(112, 173, 71),
	        new Color(39, 68, 119),
	        new Color(157, 72, 14),
	        new Color(99, 99, 99),
	        new Color(153, 114, 1),
	        new Color(37, 94, 145),
	        new Color(67, 103, 43),
	        new Color(105, 141, 208),
	        new Color(241, 154, 95),
	        new Color(185, 185, 185),
	        new Color(255, 205, 51),
	        new Color(128, 178, 222),
	        new Color(139, 193, 103),
	        new Color(51, 90, 161),
	        new Color(210, 96, 17),
	        new Color(132, 132, 132)
        };
	}

	@Override
	public Paint getNextPaint() {
		return sequence[index++ % sequence.length];
	}

	@Override
	public Paint getNextFillPaint() {
		return sequence[fillIndex % sequence.length];
	}

}
