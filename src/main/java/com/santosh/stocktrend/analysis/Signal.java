package com.santosh.stocktrend.analysis;

import java.util.Date;

public class Signal {
	private SignalType type;
	private Date signalDate;
	private String symbol;
	private Double price;
	private String analysisName;
	private String description;
	private String chartTag;
	
	public Signal(
			SignalType type,
			String analysisName,
			String chartTag,
			Date signalDate,
			String symbol,
			Double price,
			String description) {
		super();
		this.type = type;
		this.price = price;
		this.analysisName = analysisName;
		this.chartTag = chartTag;
		this.signalDate = signalDate;
		this.symbol = symbol;
		this.description = description;
	}

	public SignalType getType() {
		return type;
	}

	public Date getSignalDate() {
		return signalDate;
	}

	public String getSymbol() {
		return symbol;
	}

	public Double getPrice() {
		return price;
	}

	public String getAnalysisName() {
		return analysisName;
	}

	public String getDescription() {
		return description;
	}

	public String getChartTag() {
		return chartTag;
	}

	@Override
	public String toString() {
		return "Signal [type=" + type + ", signalDate=" + signalDate
				+ ", symbol=" + symbol + ", price=" + price + ", analysisName="
				+ analysisName + ", description=" + description + ", chartTag="
				+ chartTag + "]";
	}


}
