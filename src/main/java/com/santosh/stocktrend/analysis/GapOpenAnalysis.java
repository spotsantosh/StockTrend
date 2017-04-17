package com.santosh.stocktrend.analysis;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.santosh.quoteapi.DayQuote;
import com.santosh.stocktrend.Utils;

/**
 * Gaps occur when a price opens much higher (gap higher) or lower (gap lower)
 * than the previous day’s close. Once a gap occurs, the new price represents
 * an important price level. Gaps higher create support that should allow the
 * stock to move higher and gaps lower create resistance that should pressure
 * the stock lower. Until the gap is violated, we should assume the trend will
 * continue in the gap’s direction.
 * 
 * https://www.stocktrader.com/2009/05/18/best-stock-chart-patterns-investing-technical-analysis/
 * 
 * @author santosh
 *
 */

public class GapOpenAnalysis implements ITechAnalysis{
	private Logger logger = Logger.getLogger(GapOpenAnalysis.class.getSimpleName());
	private String name = "GapOpenAnalysis";
	private Map<String, Double> lastCloseMap;
	private Double gapPercent = 2D;
	private ISignalListener signalListener;
	
	@Override
	public String getName() {
		return name;
	}

	public GapOpenAnalysis() {
		this.lastCloseMap = new HashMap<String, Double>();
	}

	public GapOpenAnalysis(Double gapPercent) {
		this();
		this.gapPercent = gapPercent;
	}
	
	@Override
	public void init(EPServiceProvider epService, ISignalListener signalListener) {
    	String expression = "select * from com.santosh.quoteapi.DayQuote";
    	EPStatement statement = epService.getEPAdministrator().createEPL(expression);
    	statement.addListener(this);
    	this.signalListener = signalListener;
	}

	@Override
	public void update(EventBean[] newEvents, EventBean[] oldEvents) {
		EventBean event = newEvents[0];
		DayQuote dQuote = (DayQuote) event.getUnderlying();
		String symbol = dQuote.getqSymbol();
		Double lastClose = lastCloseMap.get(symbol);
		if(lastClose != null && dQuote.getqOpen() > lastClose * (1 + gapPercent/100)){
			logger.debug("2% heigher opening, lastClose:" + lastClose + " todaysOpen:" + dQuote.getqOpen() + " todaysDate:" + dQuote.getqDate());
			Signal signal = new Signal(
					SignalType.BUY,
					this.name,
					"BUY Gap Open",
					dQuote.getqDate(),
					dQuote.getqSymbol(),
					dQuote.getqOpen(),
					String.format(
							"%s Gap open at %.2f, which is > %.2f %% higher than previous close of %.2f",
							Utils.INSTANCE.getDateYYYYMMDD(dQuote.getqDate()),
							dQuote.getqOpen(),
							this.gapPercent,
							lastClose)
					);
			this.signalListener.addSignal(signal);
			
		}
		lastCloseMap.put(symbol, dQuote.getqClose());
    }

//	SignalType type,
//	String analysisName,
//	String chartTag,
//	Date signalDate,
//	String symbol,
//	Double price,
//	String description
	

}
