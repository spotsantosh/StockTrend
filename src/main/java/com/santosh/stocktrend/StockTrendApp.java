package com.santosh.stocktrend;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.santosh.quoteapi.DayQuote;
import com.santosh.quoteapi.IQuoteApi;
import com.santosh.quoteapi.YahooQuoteApi;
import com.santosh.stocktrend.analysis.GapOpenAnalysis;
import com.santosh.stocktrend.analysis.ISignalListener;
import com.santosh.stocktrend.analysis.ITechAnalysis;
import com.santosh.stocktrend.analysis.Signal;
import com.santosh.stocktrend.charting.ChartData;
import com.santosh.stocktrend.charting.IChartUtil;
import com.santosh.stocktrend.charting.JavaFXChartUtil;

/**
 *
 */
public class StockTrendApp implements ISignalListener{
	private Logger logger = Logger.getLogger(StockTrendApp.class.getSimpleName());
	private EPServiceProvider epService;
	private IChartUtil chartUtil;
	private Set<String> watchSymbolSet;
	private List<ITechAnalysis> techAnalysisList;
	private Map<String, List<Signal>> signalMap;
	private IQuoteApi quoteApi;
	private String quotesFolder;
	private String chartsFolder;
	
    public StockTrendApp() {
    	this.watchSymbolSet = new HashSet<String>();
    	this.techAnalysisList = new ArrayList<ITechAnalysis>();
    	this.signalMap = new HashMap<String, List<Signal>>();
		String tmpDir = System.getProperty("java.io.tmpdir");
		quotesFolder = tmpDir + File.separator + "StockTrend" + File.separator + "Quotes";
		chartsFolder = tmpDir + File.separator + "StockTrend" + File.separator + "Charts";
	}
    
    public void addWatchSymbol(String symbol){
    	this.watchSymbolSet.add(symbol);
    }
    
    public void addWatchSymbols(List<String> symbols){
    	this.watchSymbolSet.addAll(symbols);
    }

    public Set<String> getWatchSymbolSet(){
    	return Collections.unmodifiableSet(this.watchSymbolSet);
    }

    public void addTechAnalysis(ITechAnalysis techAnalysis) {
    	techAnalysisList.add(techAnalysis);
	}
    public void addTechAnalysis(List<ITechAnalysis> techAnalysisList) {
    	this.techAnalysisList.addAll(techAnalysisList);
	}
    
    public void setQuoteApi(IQuoteApi quoteApi){
    	this.quoteApi = quoteApi;
    }
    
    public void setChartUtil(IChartUtil chartUtil){
    	this.chartUtil = chartUtil;
    }
    
	private void init(){
		
		// create folder structure
		new File(quotesFolder).mkdirs();
		new File(chartsFolder).mkdirs();
		
//    	Configuration config = new Configuration();
//    	config.addEventTypeAutoName("com.santosh.stocktrend.L1MarketData");
//    	EPServiceProvider epService = EPServiceProviderManager.getDefaultProvider(config);
    	
    	
    	epService = EPServiceProviderManager.getDefaultProvider();
//    	String expression = "select id, last, avg(last), tradeTime from com.santosh.stocktrend.L1MarketData.win:time_batch(3 sec)";
//    	String expression = "select bid from com.santosh.stocktrend.L1MarketData where bid > 200";
//    	String expression = "select avg(qClose) from com.santosh.quoteapi.DayQuote.win:length_batch(15)";
//    	EPStatement statement = epService.getEPAdministrator().createEPL(expression);
//    	statement.addListener(new EventListener());
    	
    	// set up tech analysis
    	for(ITechAnalysis nextT : techAnalysisList){
        	nextT.init(epService, this);
    	}
    	
    	// analyze signals
    	for(String key : signalMap.keySet()){
    		List<Signal> list = signalMap.get(key);
    		for(Signal nextS : list){
    			System.out.println(nextS.getDescription());
    		}
    	}
    }

    private void pushEvent(Object event){
    	this.epService.getEPRuntime().sendEvent(event);
    }

    @Override
	public void addSignal(Signal signal) {
		List<Signal> list = signalMap.get(signal.getSymbol());
		if(list == null){
			list = new ArrayList<Signal>();
			signalMap.put(signal.getSymbol(), list);
		}
		list.add(signal);
	}
	
	/**
	 * Save quotes temporarily in to a local tmp file
	 * @param list
	 * @throws IOException 
	 */
	private void saveQuotes(String symbol, List<DayQuote> list){
		try{
			File folder = new File(quotesFolder);
			folder.mkdirs();
			File quoteFile = new File(folder.getAbsolutePath() + File.separator + symbol + ".ser");
			quoteFile.createNewFile();
			
			ObjectOutputStream oout = new ObjectOutputStream(new FileOutputStream(quoteFile));
			oout.writeObject((ArrayList<DayQuote>) list);
			logger.debug( String.format("saved %d quotes to file %s", list.size(), quoteFile));
			oout.close();
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}

	private ArrayList<DayQuote> readQuotes(String symbol){
		ArrayList<DayQuote> list = new ArrayList<DayQuote>();
		try{
			File quoteFile = new File(quotesFolder + File.separator + symbol + ".ser");
			if(quoteFile.exists()){
				ObjectInputStream oin = new ObjectInputStream(new BufferedInputStream(new FileInputStream(quoteFile)));
				list = (ArrayList<DayQuote>) oin.readObject();
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		return list;
	}
	
	public void startAnalysis(){
        init();

        // push quote events
    	for(String nextS : this.getWatchSymbolSet()){
        	List<DayQuote> quoteList = quoteApi.getLastYearsDayQuotes(nextS);
        	int i = quoteList.size(); 
        	for(DayQuote nextQ : quoteList){
        		pushEvent(nextQ);
        	}
        	// save quotes on local file for report generation
        	saveQuotes(nextS, quoteList);
    	}
    	
    	ChartData chartData = new ChartData();
    	chartData.setChartsFolderPath(chartsFolder);
    	
    	// gather signal data
    	for(String symbol: signalMap.keySet()){
    		List<Signal> sigList = signalMap.get(symbol);
    		for(Signal nextS : sigList){
        		logger.info(nextS.getDescription());
    		}
    		ArrayList<DayQuote> quoteList = readQuotes(symbol);
    		chartData.addQuote(symbol, quoteList);
    		chartData.addSignal(symbol, sigList);
    		
    	}
		chartUtil.createChart(chartData);
	}


	public static void main_old( String[] args ){

		StockTrendApp app = new StockTrendApp();
        // add watch symbols
        app.addWatchSymbol("SWKS");
        app.addWatchSymbol("AAPL");
        app.addWatchSymbol("MSFT");
        app.addWatchSymbol("ADBE");
        app.addWatchSymbol("AMZN");
        
		// conventional way of wiring dependencies
    	app.chartUtil = new JavaFXChartUtil();
        app.addTechAnalysis(new GapOpenAnalysis());
        app.quoteApi = new YahooQuoteApi();
        app.startAnalysis();
    }
	
	public static void main( String[] args ){

		ApplicationContext context = new ClassPathXmlApplicationContext(
				"Spring-Module.xml");
	}
}
