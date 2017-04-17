package com.santosh.stocktrend.charting;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.santosh.quoteapi.DayQuote;
import com.santosh.stocktrend.analysis.Signal;

public class ChartData {
	private String chartsFolderPath;
	private Map<String, List<DayQuote>> quoteMap;
	private Map<String, List<Signal>> signalMap;
	
	

	public ChartData() {
		super();
		this.quoteMap = new HashMap<String, List<DayQuote>>();
		this.signalMap = new HashMap<String, List<Signal>>();
	}
	public String getChartsFolderPath() {
		return chartsFolderPath;
	}
	public void setChartsFolderPath(String chartsFolderPath) {
		this.chartsFolderPath = chartsFolderPath;
	}
	public Map<String, List<DayQuote>> getQuoteMap() {
		return quoteMap;
	}
	public void addQuote(String symbol, List<DayQuote> quoteList) {
		this.quoteMap.put(symbol, quoteList);
	}
	public Map<String, List<Signal>> getSignalMap() {
		return signalMap;
	}
	public void addSignal(String symbol, List<Signal> signalList) {
		this.signalMap.put(symbol, signalList);
	}

}
