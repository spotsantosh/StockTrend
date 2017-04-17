package com.santosh.quoteapi;

import java.util.Date;
import java.util.List;

public interface IQuoteApi {
	/**
	 * source for providing day quotes from startDate to endDate.
	 * @param symbol
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	List<DayQuote> getDayQuotes(String symbol, Date startDate, Date endDate);
	/**
	 * Provides day quotes for last six months from current date
	 * @param symbol
	 * @return
	 */
	List<DayQuote> getLastSixMonthsDayQuotes(String symbol);
	/**
	 * Provides day quotes for last year from current date
	 * @param symbol
	 * @return
	 */
	List<DayQuote> getLastYearsDayQuotes(String symbol);
}
