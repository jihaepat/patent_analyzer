package com.patent.web.chart;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.BubbleXYItemLabelGenerator;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYBubbleRenderer;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.XYZDataset;
import org.springframework.util.FileCopyUtils;

import com.patent.web.domain.Pair;


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

public class PtntChartFactory {
	JFreeChart chart;
	String filepath;
	int width = 1024;
	int height = 480;
	
	public PtntChartFactory(String filepath) {
		this.filepath = filepath;
	}
	
	public PtntChartFactory(String filepath, int width, int height) {
		this.filepath = filepath;
		this.width = width;
		this.height = height;
	}
	
	public PtntChartFactory calcBar(DefaultCategoryDataset dataset) {
		return calcBar(dataset, null, null, null);
	}
	public PtntChartFactory calcBar(DefaultCategoryDataset dataset, String title) {
		return calcBar(dataset, title, null, null);
	}
	public PtntChartFactory calcBar(DefaultCategoryDataset dataset, String title, String axisX, String axisY) {
		chart = ChartFactory.createBarChart(title, axisX, axisY, dataset, PlotOrientation.HORIZONTAL, false, false, false);
		chart.setBackgroundPaint(Color.white);
//		
		final CategoryPlot plot = (CategoryPlot) chart.getPlot();
		final CategoryAxis domainAxis = plot.getDomainAxis();
		domainAxis.setLowerMargin(0.0);
		domainAxis.setUpperMargin(0.0);
		final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		
		setAsix();
		
		return this;
	}
	
	public PtntChartFactory calcPie(DefaultPieDataset dataset) {
		return calcPie(dataset, null);
	}
	public PtntChartFactory calcPie(DefaultPieDataset dataset, String title) {
		chart = ChartFactory.createPieChart(title, dataset, true, true, false);
		chart.setBackgroundPaint(Color.white);
		chart.getLegend().setPosition(RectangleEdge.RIGHT);

		final PiePlot plot = (PiePlot) chart.getPlot();
		plot.setBackgroundPaint(Color.white);
		plot.setSimpleLabels(true);
		plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}: {1} ({2})", new DecimalFormat("0"), new DecimalFormat("0%")));

		plot.setDrawingSupplier(new ChartDrawingSupplier());
		
		return this;
	}
	
	public DefaultPieDataset getPieDataset(List<Dataset> list) {
		DefaultPieDataset dataset = new DefaultPieDataset();
		for (Dataset data : list) {
			dataset.setValue(data.getClmnKey(), Double.valueOf(data.getCnt()));
		}
		return dataset;
	}

	public PtntChartFactory calcBubble(XYZDataset dataset, int maxX, int maxY) {
		return calcBubble(dataset, null, "출원인수", "출원건수", maxX, maxY);
	}
	public PtntChartFactory calcBubble(XYZDataset dataset, String title, String xAsix, String yAsix, int maxX, int maxY) {
		chart = ChartFactory.createBubbleChart(title, xAsix, yAsix, dataset);
		chart.setBackgroundPaint(Color.white);
		chart.getLegend().setPosition(RectangleEdge.RIGHT);
		
		final XYPlot plot = chart.getXYPlot();
		plot.setBackgroundPaint(Color.white);
		
		XYBubbleRenderer renderer = (XYBubbleRenderer) plot.getRenderer();
	    BubbleXYItemLabelGenerator generator = new BubbleXYItemLabelGenerator(" {0}:({1},{2},{3}) ", new DecimalFormat("0"), new DecimalFormat("0"), new DecimalFormat("0"));
	    renderer.setDefaultItemLabelGenerator(generator);
	    renderer.setDefaultItemLabelsVisible(true);
	    renderer.setDefaultPositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE11, TextAnchor.BASELINE_CENTER));
		
	    plot.setRenderer(renderer);
		
	    NumberAxis domain = (NumberAxis) plot.getDomainAxis();
	    NumberAxis range = (NumberAxis) plot.getRangeAxis();
	    if (maxX>maxY) {
	    	domain.setRange(0, maxX+(maxX/2));
	    	range.setRange(0, maxY+maxX);
	    } else {
	    	domain.setRange(0, maxX+maxY);
	    	range.setRange(0, maxY+(maxY/2));
	    }
		
		return this;
	}

	public PtntChartFactory calcStack(DefaultCategoryDataset dataset) {
		return calcStack(dataset, PlotOrientation.VERTICAL);
	}
	public PtntChartFactory calcStack(DefaultCategoryDataset dataset, PlotOrientation plotOrientation) {
		chart = ChartFactory.createStackedBarChart(null, "Category", "Value", dataset, plotOrientation, true, true, false);
		chart.setBackgroundPaint(Color.white);
		chart.getLegend().setPosition(RectangleEdge.RIGHT);

		setAsix();
		return this;
	}
	
	public DefaultCategoryDataset getIpt91Dataset(List<Dataset> list) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for (Dataset data : list) {
			dataset.addValue(data.getCnt(), "삼극", data.getRwKey());
			dataset.addValue(data.getCnt2(), "비삼극", data.getRwKey());
		}
		return dataset;
	}
	
	public DefaultCategoryDataset getCategoryDataset(List<Dataset> list) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for (Dataset data : list) {
			dataset.addValue(data.getCnt(), data.getClmnKey(), data.getRwKey());
		}
		return dataset;
	}

	public PtntChartFactory calcArea(DefaultCategoryDataset dataset) {
		return calcArea(dataset, null, null, PlotOrientation.VERTICAL);
	}
	public PtntChartFactory calcArea(DefaultCategoryDataset dataset, String xAsix, String yAsix, PlotOrientation orientation) {
		chart = ChartFactory.createStackedAreaChart(null, null, null, dataset, PlotOrientation.VERTICAL, true, true, false);
		chart.setBackgroundPaint(Color.white);
		chart.getLegend().setPosition(RectangleEdge.RIGHT);
		
		final CategoryPlot plot = (CategoryPlot) chart.getPlot();
		final CategoryAxis domainAxis = plot.getDomainAxis();
		domainAxis.setLowerMargin(0.0);
		domainAxis.setUpperMargin(0.0);
		final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		
		setAsix();
		
		return this;
	}

	public PtntChartFactory calcBarAndLine(DefaultCategoryDataset lineDataset, DefaultCategoryDataset barDataset) {
		return calcBarAndLine(lineDataset, barDataset, null, "출원년도", "연도별 출원건수");
	}
	public PtntChartFactory calcBarAndLine(DefaultCategoryDataset lineDataset, DefaultCategoryDataset barDataset, String title) {
		return calcBarAndLine(lineDataset, barDataset, title, "출원년도", "연도별 출원건수");
	}
	public PtntChartFactory calcBarAndLine(DefaultCategoryDataset lineDataset, DefaultCategoryDataset barDataset, String title, String xAsix, String yAsix) {
		CategoryPlot plot = new CategoryPlot();
		
		CategoryItemRenderer lineRenderer = new LineAndShapeRenderer();
		plot.setDataset(0, lineDataset);
		plot.setRenderer(0, lineRenderer);
		
		CategoryItemRenderer baRenderer = new BarRenderer();
		plot.setDataset(1, barDataset);
		plot.setRenderer(1, baRenderer);
		
		plot.setDomainAxis(new CategoryAxis(xAsix));
		plot.setRangeAxis(0, new NumberAxis(yAsix));
		plot.setRangeAxis(1, new NumberAxis(""));
		
		plot.mapDatasetToRangeAxis(0, 0);
		plot.mapDatasetToRangeAxis(1, 1);
		
		chart = new JFreeChart(plot);
		chart.setTitle(title);
		chart.setBackgroundPaint(Color.white);
		chart.getLegend().setPosition(RectangleEdge.RIGHT);
		
		setAsix();
		
		return this;
	}
	public Pair<DefaultCategoryDataset, DefaultCategoryDataset> getBarAndLineDataset(List<Dataset> list) {
		return getBarAndLineDataset(list, "증감율", "건수");
	}
	public Pair<DefaultCategoryDataset, DefaultCategoryDataset> getBarAndLineDataset(List<Dataset> list, String strBar, String strLine) {
		DefaultCategoryDataset bar = new DefaultCategoryDataset();
		DefaultCategoryDataset line = new DefaultCategoryDataset();
		
		for (Dataset data : list) {
			bar.addValue(data.getCnt2(), strBar, data.getClmnKey());
			line.addValue(data.getCnt(), strLine, data.getClmnKey());
		}
		
		Pair<DefaultCategoryDataset, DefaultCategoryDataset> pair = new Pair<>();
		pair.setV1(bar);
		pair.setV2(line);
		return pair;
	}
	
	private void setAsix() {
		final CategoryPlot plot = chart.getCategoryPlot();
		plot.setBackgroundPaint(Color.white);
		plot.setDomainGridlinesVisible(true);
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.white);
		
		plot.setDrawingSupplier(new ChartDrawingSupplier());
		
		final CategoryAxis domainAxis = plot.getDomainAxis();
		domainAxis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_45);
		domainAxis.setLowerMargin(0.0);
		domainAxis.setUpperMargin(0.0);
	}

	
	public String saveImageFile() {
		return saveImageFile(width, height);
	}
	public String saveImageFile(int width, int height) {
		String file_name = UUID.randomUUID().toString();
		
		File jFreeChart = new File(String.format("%s/charts/%s.jpeg", filepath, file_name));
		try {
			ChartUtils.saveChartAsJPEG(jFreeChart, chart, width, height);
		} catch (IOException e) {
			e.printStackTrace();
			
			return null;
		}
		
		return file_name;
	}
	
	
    public static void downloadExcel(HttpServletRequest req, HttpServletResponse res, String fileName, String saveName) {
    	File targetFile = new File(fileName);
        if (targetFile.exists()) {
            res.setContentLength((int) targetFile.length());
            String browser = req.getHeader("User-Agent");
            if (browser.contains("MSIE") || browser.contains("Trident") || browser.contains("Chrome")) {
                try {
					saveName = URLEncoder.encode(saveName, "UTF-8").replaceAll("\\+", "%20");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
            } else {
                try {
					saveName = new String(saveName.getBytes("UTF-8"), "ISO-8859-1");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
            }
            res.setHeader("Content-Disposition", "attachment;fileName=" + saveName + ";");
            res.setHeader("Content-Transfer-Encoding", "binary");
            res.setHeader("Set-Cookie", "fileDownload=true; path=/");
            res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            
            OutputStream down = null;
            FileInputStream filestream = null;
            try {
            	down = res.getOutputStream();
                filestream = new FileInputStream(targetFile);
                
                FileCopyUtils.copy(filestream, down);
            } catch (Exception e) {
            	e.printStackTrace();
            } finally {
            	if (filestream != null) {
            		try {
            			filestream.close();
            		} catch (Exception e) {
            		}
            	}
            	
            	if (down != null) {
            		try {
						down.flush();
					} catch (IOException e) {
					}
            	}
            }
        } else {
        	downErrorAlert(res, "파일이 존재하지 않습니다. 보고서 템플릿을 업로드 하십시오.");
        }
    }
    
    public static void downErrorAlert(HttpServletResponse res, String message) {
    	try {
    		Thread.sleep(1000);
    	} catch (Exception e) {
    	}
    	
    	
    	res.reset();
    	res.setHeader("Set-Cookie", "fileDownload=false; path=/");
        res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    	res.setCharacterEncoding("UTF-8");
    	
		PrintWriter printwriter = null;
		try {
			printwriter = res.getWriter();
			printwriter.println("<html>");
			printwriter.println("<body>");
			printwriter.println(message);
			printwriter.println("</body>");
			printwriter.println("</html>");
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			printwriter.flush();
			printwriter.close();
		}
    }
}
