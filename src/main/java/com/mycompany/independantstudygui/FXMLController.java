package com.mycompany.independantstudygui;

import charts.dataviewer.DataViewer;
import charts.dataviewer.api.config.DataViewerConfiguration;
import charts.dataviewer.api.data.PlotData;
import charts.dataviewer.api.trace.GenericTrace;
import charts.dataviewer.api.trace.LineTrace;
import charts.dataviewer.utils.TraceColour;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import static java.util.Arrays.copyOfRange;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.ScheduledService;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;




public class FXMLController implements Initializable {
    
    /********************************************************/
    @FXML
    private MenuItem thermalSelector;

    @FXML
    private MenuItem videoSelector;

    @FXML
    private MenuItem CSVSelector;  
    
    @FXML
    private SplitPane splitPane;

    @FXML
    private Button startButton;

    @FXML
    private TextField vOffset;

    @FXML
    private TextField tOffset;
    
    @FXML
    private TextField audioOffset;

    @FXML
    private TextField dataOffset;

    @FXML
    private TextField frequency;

    @FXML
    private Button stopButton;

    @FXML
    private Button collectButton;

    @FXML
    private MediaView VideoPlayer;

    @FXML
    private MediaView ThermalVideo;
    
    @FXML
    private MediaPlayer audioPlayer;
    
    @FXML
    private MediaPlayer media;
    
    @FXML    
    private MediaPlayer thermalMedia;

    @FXML
    private VBox GraphVBox;
    
    @FXML
    private AnchorPane leftAnchor;
    
    @FXML
    private Pane videoPane;

    @FXML
    private Pane thermalPane;
    
    @FXML
    private AnchorPane scrollAnchor;
    
    @FXML
    private HBox dataHbox;
    
    @FXML
    private Label timeArea;
    
    @FXML
    private MediaView mp3Media;
    
    @FXML
    private MenuItem audioSelector;
    
    @FXML
    private ScrollPane scrollPane;
    
    @FXML
    private Rectangle timeBox;
    
    /********************************************************/
        
    int numColumns;
    int numRows;
    Vector<DataViewer> dataViewerVector = new Vector<>();
    Vector<PlotData> plotVector = new Vector<>();
    
    ArrayList<Double[]> dataSets = new ArrayList<Double[]>();
    ArrayList<Double[]> timeValues = new ArrayList<Double[]>();
    
    Double  time =0.0;
    SimpleDoubleProperty masterTime = new SimpleDoubleProperty(0.0);
    
    boolean playing = false;
    double freq = 128.0;
    
    private ScheduledService<Void> timerThread;
    private ScheduledService<Void> graphThread;
    
    Double timeArray[] = {0.0 ,0.0};
    
    GraphingController grapher = new GraphingController();
    
    TranslateTransition timeTransition;
    
    
    
    
    
    
        
    
    /********************************************************/
    
    @FXML
    void start(ActionEvent event) {
        
        if(timerThread != null)
            timerThread.cancel();
        
        timeTransition = new TranslateTransition(Duration.millis(time * 1000), timeBox);
        timeTransition.setByX(scrollPane.widthProperty().get() - 100);
        
        timeTransition.play();
        
        
        if (thermalMedia == null && media == null && audioPlayer == null)
            return;
        
        if (startButton.getText().equals("Start")) {
            playing = true;
            
            startButton.setText("Pause");
            if (media != null) {
                media.play();
            }
            if (thermalMedia != null) {
                thermalMedia.play();
            }
            if (audioPlayer != null) {
                audioPlayer.play();
            }
            

        } else {
            playing = false;
            //timeTransition.pause();
            startButton.setText("Start");
            if (media != null) {
                media.pause();
            }
            if (thermalMedia != null) {
                thermalMedia.pause();
            }
            if (audioPlayer != null) {
                audioPlayer.pause();
            }
        }
        
        
        
        
        
        
        timerThread = new ScheduledService<Void>() {
                protected Task<Void> createTask() {
                    return new Task<Void>() {
                        protected Void call() {
                            Platform.runLater(() -> {                                
                                if(playing){
                                    masterTime.set(masterTime.get() + .05);    
                                    
                                }                                      
                                                        
                            });
                            return null;
                        }
                    };
                }
            };
        
        timerThread.setPeriod(Duration.millis(50));
        timerThread.start();
       
        graphThread = new ScheduledService<Void>() {
                protected Task<Void> createTask() {
                    return new Task<Void>() {
                        protected Void call() {
                            Platform.runLater(() -> {
                                
                                if(playing){                                    
                                    //update();                                      
                                }                                       
                                                        
                            });
                            return null;
                        }
                    };
                }
            };
        
        graphThread.setPeriod(Duration.millis(200));
        graphThread.start();
        
        
        
         
        
        
        
        
        
        
    }

    @FXML
    void stop(ActionEvent event) {
        
        
        
        
        playing = false;
        time = 0.0;
         masterTime.set(0.00);
        if (media != null) {
            media.stop();
            media.pause();
        }
        if (thermalMedia != null) {
            thermalMedia.stop();
            thermalMedia.pause();
        }
        if (audioPlayer != null) {
            audioPlayer.stop();
            audioPlayer.pause();
        }

        startButton.setText("Start");
        
        
    }
    
    @FXML
    void getMp3(ActionEvent event) {
        FileChooser mp3Chooser = new FileChooser();        
        //add filter to only get mp4 files
        FileChooser.ExtensionFilter mp3Extension = 
                new FileChooser.ExtensionFilter("MP4 Files ( *.mp3"
                , "*.mp3");
        
        mp3Chooser.getExtensionFilters().add(mp3Extension);
        
        mp3Chooser.setTitle("Select MP3 File");
        File selected = mp3Chooser.showOpenDialog(null);
        
        if(selected != null) {
            //reset button and add new audio
            
            startButton.setText("Start");
            audioPlayer = new MediaPlayer(new Media(selected.toURI().toString()));
            mp3Media.setMediaPlayer(audioPlayer);
        }

    }
    
    @FXML
    void getCSV(ActionEvent event) throws FileNotFoundException, IOException {
        FileChooser csvChooser = new FileChooser();        
        //add filter to only get csv files
        FileChooser.ExtensionFilter csvExtension = 
                new FileChooser.ExtensionFilter("CSV Files ( *.csv"
                , "*.csv");
        
        
        
        
        csvChooser.getExtensionFilters().add(csvExtension);
        
        csvChooser.setTitle("Select CSV data file");
        File selected = csvChooser.showOpenDialog(null);
        
        if(selected == null) 
            return;
        
        
        try{
            
            grapher.graphFileTo(selected, GraphVBox);
            
            //first clear out the VBox of any previous graphs
            GraphVBox.getChildren().removeAll(GraphVBox.getChildren());
            
            
            Reader input = new FileReader(selected);
            Iterable<CSVRecord> data = CSVFormat.DEFAULT.parse(input);

            //get number of columns from the first element in the CSV file
            CSVRecord firstRow = data.iterator().next();
            numColumns = firstRow.size();

            //getting length of csv file
            numRows = 0;
            for(CSVRecord row: data){
                numRows++;
            }
            time = numRows / freq;
            //reset input 
            input = new FileReader(selected);
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
                
                trace.setTraceColour(TraceColour.RED);
                
                trace.setxArray(timeArray);
                timeValues.add(timeArray);
                trace.setyArray(valArray); 
                dataSets.add(valArray);
                
                //DataViewer dataviewer = new DataViewer();
                // Create dataviewer configuration
                dataViewerVector.add(new DataViewer());
                    
                
                
                config.showLegend(false);
                config.setPlotTitle("");
                config.setMarginBottom(20);
                config.setMarginTop(20);
                config.setMarginLeft(85);
                
                
                
                
                dataViewerVector.get(i).updateConfiguration(config);
                dataViewerVector.get(i).setMinWidth(1600.0 );
                dataViewerVector.get(i).setMaxHeight(250.0);
                plotData.addTrace(trace);
                plotVector.add(plotData);
                
                
                
                dataViewerVector.get(i).updatePlot(plotData);   
                
                dataViewerVector.get(i).maxWidthProperty().bind(scrollPane.widthProperty());
                dataViewerVector.get(i).minWidthProperty().bind(scrollPane.widthProperty());        
                
                GraphVBox.getChildren().add(dataViewerVector.get(i));
                
                //have to reset the data and input so it will iterate again 
                //otherwise just reads one column
                input = new FileReader(selected);
                data = CSVFormat.DEFAULT.parse(input);
                
        }  //close for loop
                
            
        //addTime(dataViewerVector);
            
        } catch(FileNotFoundException ex ){
            System.out.println(ex);
        } catch(IOException ex){
            System.out.println(ex);
        }
        
    }
    
    
    public void update(){
        
        LineTrace<Double> playTrace = new LineTrace<>();
        playTrace.setTraceColour(TraceColour.GREEN);
        
        for (int i = 0; i < 1; i++) {
             
            
            
            playTrace.setxArray(copyOfRange( timeValues.get(i) ,0 ,(int) (Math.round(masterTime.get()) * freq)  ));
            playTrace.setyArray(copyOfRange( dataSets.get(i) ,0 ,(int) (Math.round(masterTime.get()) * freq) ));
            
            PlotData plot = new PlotData();
            plot.addTrace(playTrace);
            dataViewerVector.get(i).resetPlot();
            
            dataViewerVector.get(i).updatePlot(plotVector.get(i));
            dataViewerVector.get(i).updatePlot(plot);

        }
    }
    
    @FXML
    void clock( Double  millis)
    {
       timeArray[0] += millis /1000.0;
       timeArray[1] += millis /1000.0; 
       time += millis /1000.0; 
       
       //addTime(graphVector);
    }
    
    void addTime( Vector<DataViewer> graphs){
        
        GenericTrace<Double> trace = new LineTrace<>();
        PlotData plotData = new PlotData();
        
        
        Double verticalArray[] = new Double[2];
        
        
        
        verticalArray[0] = 100.0;
        verticalArray[1] = -100.0;
                
        trace.setxArray(timeArray);
        trace.setyArray(verticalArray);
        trace.setTraceColour(TraceColour.GREEN);
        
        
        plotData.addTrace(trace);
        
        for(DataViewer graph : graphs){
            
            
            
            graph.updatePlot(plotData);
            
        }
        
        
    }
    
    @FXML
    void getMp4(ActionEvent event) {
        FileChooser mp4Chooser = new FileChooser();        
        //add filter to only get mp4 files
        FileChooser.ExtensionFilter mp4Extension = 
                new FileChooser.ExtensionFilter("MP4 Files ( *.mp4"
                , "*.mp4");
        
        mp4Chooser.getExtensionFilters().add(mp4Extension);
        
        mp4Chooser.setTitle("Select MP4 Video");
        File selected = mp4Chooser.showOpenDialog(null);
        
        if(selected != null) {
            //reset button and add new videos
            
            startButton.setText("Start");
            media = new MediaPlayer(new Media(selected.toURI().toString()));
            VideoPlayer.setMediaPlayer(media);
        }
    }

    @FXML
    void getThermal(ActionEvent event) {
        FileChooser thermalChooser = new FileChooser();        
        //add filter to only get mp4 files
        FileChooser.ExtensionFilter mp4Extension = 
                new FileChooser.ExtensionFilter("MP4 Files ( *.mp4"
                , "*.mp4");
        
        thermalChooser.getExtensionFilters().add(mp4Extension);
        
        thermalChooser.setTitle("Select Thermal Video");
        File selected = thermalChooser.showOpenDialog(null);
        
        if(selected != null) {
            //reset button and add new videos
            startButton.setText("Start");
            thermalMedia = new MediaPlayer(new Media(selected.toURI().toString()));
            ThermalVideo.setMediaPlayer(thermalMedia);
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        
        
        timeArea.textProperty().bind(masterTime.asString("%.2f"));
        
         
        GraphVBox.maxWidthProperty().bind(scrollPane.widthProperty());
        GraphVBox.minWidthProperty().bind(scrollPane.widthProperty());
        
        VideoPlayer.fitWidthProperty().bind(videoPane.widthProperty());
        VideoPlayer.fitHeightProperty().bind(videoPane.heightProperty());
        
        ThermalVideo.fitWidthProperty().bind(thermalPane.widthProperty());
        ThermalVideo.fitHeightProperty().bind(thermalPane.heightProperty());
        
        
        
        
        //set the ratio for the split pane to stay constant 
        //and cursor over divider to transparent
        //leftAnchor.maxWidthProperty().bind(splitPane.widthProperty().multiply(0.35));
        //leftAnchor.minWidthProperty().bind(splitPane.widthProperty().multiply(0.35));
        
        
        
        
        splitPane.lookupAll(".split-pane-divider").stream()
            .forEach(div ->  div.setMouseTransparent(true) );
        
        
        
        
        
    }    
}