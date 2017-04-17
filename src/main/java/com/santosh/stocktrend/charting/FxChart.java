package com.santosh.stocktrend.charting;

import java.awt.image.RenderedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import javax.imageio.ImageIO;

import com.santosh.quoteapi.DayQuote;
import com.santosh.stocktrend.Utils;
import com.santosh.stocktrend.analysis.Signal;

public class FxChart extends Application {
	private ChartData chartData;
	
	public FxChart(){
		chartData = JavaFXChartUtil.chartData;
	}
	
	@Override
	public void start(Stage stage) throws Exception {

	    
	    FlowPane root = new FlowPane(Orientation.VERTICAL);
        root.setPrefWrapLength(chartData.getQuoteMap().size() * 600);
        root.setPadding(new Insets(20));
        root.setHgap(20);
        root.setVgap(20);
        for(LineChart chart : getCharts()){
            root.getChildren().add(chart);
        }
	    
        Scene scene  = new Scene(new ScrollPane(root),800,1000);
        
        stage.setTitle("StockTrend Analysis");
        stage.setScene(scene);
        stage.show();
        
        
		File chartFile = new File(chartData.getChartsFolderPath() + File.separator + "Report.png");
		chartFile.createNewFile();

		RenderedImage renderedImage = SwingFXUtils.fromFXImage(scene.snapshot(null), null);
        ImageIO.write(
                renderedImage, 
                "png",
                chartFile);
        
    }
	
	private ArrayList<LineChart> getCharts(){
		ArrayList<LineChart> chartList = new ArrayList<LineChart>();
		
		for(String symbol : chartData.getSignalMap().keySet()){
			List<Signal> signalList = chartData.getSignalMap().get(symbol);
			List<DayQuote> quoteList = chartData.getQuoteMap().get(symbol);

		    final CategoryAxis xAxis = new CategoryAxis();
		    final NumberAxis yAxis = new NumberAxis();
		
		    ObservableList<XYChart.Data<String,Double>> aList = FXCollections.observableArrayList();
		    
	        for(DayQuote next : quoteList){
	    	    aList.add(new XYChart.Data<String,Double>(Utils.INSTANCE.getDateYYYYMMDD(next.getqDate()), next.getqOpen()));
	        }
	        
	        Series<String, Double> stockSeries = new LineChart.Series<String,Double>(symbol, aList);
	        
	        ObservableList<XYChart.Series<String,Double>> lineChartData =
	        		FXCollections.observableArrayList(stockSeries);
		
		    
		    for(Signal nextS : signalList){
			    final Series<String,Double> signalSeries = new Series<String,Double>();
			    double per2 = nextS.getPrice() * 4 / 100;
			    signalSeries.getData().add(
			    		new XYChart.Data<String,Double>(
			    				Utils.INSTANCE.getDateYYYYMMDD(nextS.getSignalDate()),
			    				nextS.getPrice() - per2)
			    				);
			    signalSeries.getData().add(
			    		new XYChart.Data<String,Double>(
			    				Utils.INSTANCE.getDateYYYYMMDD(nextS.getSignalDate()),
			    				nextS.getPrice() + per2)
			    				);
			    signalSeries.setName(nextS.getDescription());
			    
			    lineChartData.add(signalSeries);
		    }
		    
		    //set name to the line
		    //add the new line to the graph
		    final LineChart chart = new LineChart(xAxis, yAxis, lineChartData);
	        chart.setCreateSymbols(false);	// prevents creating a circle on line
	        chart.setPrefWidth(700);
		
		    //set title to the figure
		    chart.setTitle(symbol + " Analysis");
		    chartList.add(chart);
			
		}
		
	    return chartList;
	}
	
	public static void main() {
		FxChart app = new FxChart();
		app.launch();
	}


}
