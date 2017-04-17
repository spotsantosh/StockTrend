package com.santosh.stocktrend.charting;

/**
 * Creates charts using JFreeChart api. 
 * 
 * @author santosh
 *
 */

public class JavaFXChartUtil implements IChartUtil {
	static ChartData chartData;
	
	@Override
	public void createChart(ChartData chartData) {
		try{
			JavaFXChartUtil.chartData = chartData;
			
			FxChart.main();
	        
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		
	}
	
}
