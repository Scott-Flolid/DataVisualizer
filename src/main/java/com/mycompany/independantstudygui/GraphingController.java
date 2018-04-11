/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.independantstudygui;

import charts.dataviewer.DataViewer;
import charts.dataviewer.api.config.DataViewerConfiguration;
import charts.dataviewer.api.data.PlotData;
import charts.dataviewer.api.trace.GenericTrace;
import charts.dataviewer.api.trace.LineTrace;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Vector;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import javafx.concurrent.ScheduledService;


/**
 *
 * @author Scott Flold
 */
public class GraphingController {
    
    private int numColumns;
    private int numRows;
    private Vector<DataViewer> graphVector = new Vector<>();
    private  Vector<PlotData> plotVector = new Vector<>();
    
    private Double  time;
    boolean playing = false;
    double freq = 128.0;
    
    Double timeArray[] = {0.0 ,0.0};
    
    public Vector<DataViewer> graphFileTo(File csv, VBox  GraphVBox){      
              
        
        try{
            
            //first clear out the VBox of any previous graphs
            GraphVBox.getChildren().removeAll(GraphVBox.getChildren());
            
            
            Reader input = new FileReader(csv);
            Iterable<CSVRecord> data = CSVFormat.DEFAULT.parse(input);

            //get number of columns from the first element in the CSV file
            CSVRecord firstRow = data.iterator().next();
            numColumns = firstRow.size();

            //getting length of csv file
            numRows = 0;
            for(CSVRecord row: data){
                numRows++;
            }
            //reset input 
            input = new FileReader(csv);
            data = CSVFormat.DEFAULT.parse(input);    
                
                
            XYChart.Series<Double, Double> series[] = new XYChart.Series[numColumns]; 
            
            LineChart graphs[] = new LineChart[numColumns] ;
            
            for(int i = 0; i < numColumns;i ++){
                series[i] = new XYChart.Series();               
            }
            
               
            // i will be used to keep track of our column number    
            for(int i = 0; i < numColumns;i ++){
                      
                Double time = 0.0;
                
                
                PlotData plotData = new PlotData();
                GenericTrace<Double> trace = new LineTrace<>();
                
                Double valArray[] = new Double[numRows];
                Double timeArray[] = new Double[numRows];
                int count = 0;
                
                
                NumberAxis xAxis = new NumberAxis();
                xAxis.setLabel("");
                NumberAxis yAxis = new NumberAxis();
                
                
                DataViewerConfiguration config = new DataViewerConfiguration();
                config.setxAxisTitle("");
                // Y axis title
                config.setyAxisTitle( (String) data.iterator().next().get(i));
                
                //fill arrays with the values to be passed to the graph
                //from the CSV file   
                for(CSVRecord row: data){
                    double value = Double.parseDouble(row.get(i));
                    
                    series[i].getData().add(new XYChart.Data(time , value));                    
                    //increment time based on frequency
                    time += (1.0 / freq);
                    
                    valArray[count] = value;
                    timeArray[count] = time;
                    
                    if( count < numRows - 1)
                        count++;
                }
                
            
                LineChart<Double,Double> graph = new LineChart(xAxis, yAxis );
                graph.getData().add(series[i]);
                graph.setCreateSymbols(false);
                graph.setLegendVisible(false);                
                
                
                //add the graph to our local array of graphs
                graphs[i] = graph;
                
                VBox.setVgrow(graph, Priority.ALWAYS);
                
                
                
                trace.setxArray(timeArray);
                trace.setyArray(valArray);                
                
                DataViewer dataviewer = new DataViewer();
                // Create dataviewer configuration
                
                    
                
                
                config.showLegend(false);
                config.setPlotTitle("");
                config.setMarginBottom(20);
                config.setMarginTop(20);
                
                
                
                dataviewer.updateConfiguration(config);
                dataviewer.setMinWidth(1600.0 );
                dataviewer.setMaxHeight(250.0);
                plotData.addTrace(trace);
                dataviewer.updatePlot(plotData);
                
                graphVector.add(dataviewer);
                GraphVBox.getChildren().add(dataviewer);
                
                //have to reset the data and input so it will iterate again 
                //otherwise just reads one column
                input = new FileReader(csv);
                data = CSVFormat.DEFAULT.parse(input);
                
        }  //close for loop
                
            
        
            
        } catch(FileNotFoundException ex ){
            System.out.println(ex);
        } catch(IOException ex){
            System.out.println(ex);
        }
        
        return graphVector;
    }
    
}
