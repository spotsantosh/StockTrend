package com.santosh.quoteapi;

import java.io.Serializable;
import java.util.Date;

public class DayQuote implements Serializable{
	private static final long serialVersionUID = 1L;
	
/**
 * Represents daily closing price of a symbol
 */
	
//	  1 Date,Open,High,Low,Close,Volume,Adj Close                                   
//	  2 2017-03-03,138.779999,139.830002,138.589996,139.779999,21108100,139.779999
//	  3 2017-03-02,140.00,140.279999,138.759995,138.960007,26153300,138.960007
	private String qSymbol;
	private Date qDate;
	private Double qOpen;
	private Double qHigh;
	private Double qLow;
	private Double qClose;
	private Long qVolume;
	
	public DayQuote(String symbol, Date date, Double open, Double high,
			Double low, Double close, Long volume) {
		super();
		this.qSymbol = symbol;
		this.qDate = date;
		this.qOpen = open;
		this.qHigh = high;
		this.qLow = low;
		this.qClose = close;
		this.qVolume = volume;
	}
	
	

	public static long getSerialversionuid() {
		return serialVersionUID;
	}



	public String getqSymbol() {
		return qSymbol;
	}



	public Date getqDate() {
		return qDate;
	}



	public Double getqOpen() {
		return qOpen;
	}



	public Double getqHigh() {
		return qHigh;
	}



	public Double getqLow() {
		return qLow;
	}



	public Double getqClose() {
		return qClose;
	}



	public Long getqVolume() {
		return qVolume;
	}



	@Override
	public String toString() {
		return "DayQuote [qSymbol=" + qSymbol + ", qDate=" + qDate + ", qOpen="
				+ qOpen + ", qHigh=" + qHigh + ", qLow=" + qLow + ", qClose="
				+ qClose + ", qVolume=" + qVolume + "]";
	}

	
	

}
