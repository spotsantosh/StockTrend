package com.santosh.quoteapi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.santosh.stocktrend.Utils;
/**
 * Provides quotes from ichart.yahoo.com 
 * @author santosh
 *
 */

public class YahooQuoteApi implements IQuoteApi{
	private Logger logger;
	
	public YahooQuoteApi() {
		logger = Logger.getLogger(YahooQuoteApi.class.getSimpleName());
	}
	

	@Override
	public List<DayQuote> getDayQuotes(String symbol, Date startDate, Date endDate) {
		List<DayQuote> list = new ArrayList<DayQuote>();
		try {
//			http://ichart.yahoo.com/table.csv?s=AAPL&a=0&b=1&c=2017&d=1&e=31&f=2017&g=d
//		  1 Date,Open,High,Low,Close,Volume,Adj Close                                   
//		  2 2017-03-03,138.779999,139.830002,138.589996,139.779999,21108100,139.779999
//		  3 2017-03-02,140.00,140.279999,138.759995,138.960007,26153300,138.960007
				
			URL yahooFin = new URL(buildUrl(symbol, startDate, endDate));
			URLConnection yc = yahooFin.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
			String inputLine;
			int count = 0;
			Utils utils = Utils.INSTANCE;
			while ((inputLine = in.readLine()) != null) {
				if(++count == 1)	continue;	// skip header
				String[] cols = inputLine.split(",");
				DayQuote dq = new DayQuote(
						symbol,
						utils.parseDateYYYY_MM_DD(cols[0]),
						Double.parseDouble(cols[1]),	// open,
						Double.parseDouble(cols[2]),	// high,
						Double.parseDouble(cols[3]),	// low,
						Double.parseDouble(cols[4]),	// close,
						Long.parseLong(cols[5])			// volume
						);
				
				list.add(dq);
				
			}
			in.close();
		} catch (Exception ex) {
			logger.error("Unable to get stockinfo for: " + symbol, ex);
		}
		
		// reverse the list so that latest date is placed at the end 
		Collections.reverse(list);
		return list;
	}

	@Override
	public List<DayQuote> getLastSixMonthsDayQuotes(String symbol) {
		Calendar startCalendar = Calendar.getInstance();
		startCalendar.add(Calendar.MONTH, -6);
		return getDayQuotes(symbol, startCalendar.getTime(), new Date());
	}
	
	@Override
	public List<DayQuote> getLastYearsDayQuotes(String symbol) {
		Calendar startCalendar = Calendar.getInstance();
		startCalendar.add(Calendar.YEAR, -1);
		return getDayQuotes(symbol, startCalendar.getTime(), new Date());
	}

	private String buildUrl(String symbol, Date startDate, Date endDate){
		
		Calendar startCalendar = Calendar.getInstance();
		startCalendar.setTime(startDate);
		
		Calendar endCalendar = Calendar.getInstance();
		endCalendar.setTime(endDate);

		//http://ichart.yahoo.com/table.csv?s=AAPL&a=0&b=1&c=2017&d=1&e=31&f=2017&g=d
		// s is symbol
		// a is start month (0 to 11)
		// b is start day
		// c is start year
		// d is end end month (0 to 11)
		// e is end day
		// f is end year
		// g is time interval d-Daily, w-Weekly, m-Monthly
		StringBuilder url = new StringBuilder("http://ichart.yahoo.com/table.csv?");
		url
		.append("s=").append(symbol).append("&")
		.append("a=").append(startCalendar.get(Calendar.MONTH)).append("&")
		.append("b=").append(startCalendar.get(Calendar.DAY_OF_MONTH)).append("&")
		.append("c=").append(startCalendar.get(Calendar.YEAR)).append("&")
		.append("d=").append(endCalendar.get(Calendar.MONTH)).append("&")
		.append("e=").append(endCalendar.get(Calendar.DAY_OF_MONTH)).append("&")
		.append("f=").append(endCalendar.get(Calendar.YEAR)).append("&")
		.append("g=").append("d")
		;
		return url.toString();
	}
	
	public static void main(String...strings){
		YahooQuoteApi api = new YahooQuoteApi();
		System.out.println(api.buildUrl("IBM", new Date(), new Date()));
		List<DayQuote> quotes = api.getLastYearsDayQuotes("AAPL");
		for(DayQuote next : quotes){
			System.out.println(next);
		}
		
	}


}
