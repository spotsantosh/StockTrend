package com.santosh.stocktrend;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * singleton implemented as an enum, provides utility functions.
 * @author santosh
 *
 */
public enum Utils {
	INSTANCE;
	public String getDateYYYYMMDD(Date date){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		return sdf.format(date);
	}
	public Date parseDateYYYY_MM_DD(String dateStr) throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyy-MM-dd");
		return sdf.parse(dateStr);
	}
}

