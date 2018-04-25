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
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import static java.util.Arrays.copyOfRange;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.ScheduledService;
import javafx.scene.Scene;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
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
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
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
    private MenuItem storeMark;    
        
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
    
    @FXML
    private TextField markStart;

    @FXML
    private TextField markEnd;

    @FXML
    private TextField markTitle;

    @FXML
    private Button saveMark;
    
    /********************************************************/
        
    int numColumns;
    int numRows;
    
    Vector<DataViewer> dataViewerVector = new Vector<>();
    Vector<PlotData> plotVector = new Vector<>();
    
    ArrayList<Double[]> dataSets = new ArrayList<Double[]>();
    ArrayList<Double[]> timeValues = new ArrayList<Double[]>();
    ArrayList<String> graphTitles = new ArrayList();
    
    Map<String, Integer[]> markedValues = new HashMap<String, Integer[]>();
    
    Double  time =0.0;
    SimpleDoubleProperty masterTime = new SimpleDoubleProperty(0.0);
    
    boolean playing = false;
    double freq = 128.0;
    
    private ScheduledService<Void> timerThread;
    
    private ScheduledService<Void> graphThread;
    
    Double timeArray[] = {0.0 ,0.0};
    
    GraphingController grapher = new GraphingController();
    
    TranslateTransition timeTransition;
    
    Double collectStart;
    Double collectEnd;
    
    
    //regex for making sure fields are digits
    String numberChecker = "\\d+(\\.\\d+)?";
    
    NumberFormat formatter = new DecimalFormat("#0.00");
    
    
    
        
    
    /********************************************************/
    
    @FXML
    void start(ActionEvent event) {
        
        
        
        if (startButton.getText().equals("Start")) {
            handleStart();

        } else {
            handlePause();
        }
        
         
    }
    
    void handleStart(){
        if(timerThread != null)
            timerThread.cancel(); 
        
        timerThread = new ScheduledService<Void>() {
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    protected Void call() {
                        Platform.runLater(() -> {
                            if (playing && (masterTime.get() <= time)) {
                                masterTime.set(masterTime.get() + .075);

                            }

                        });
                        return null;
                    }
                };
            }
        };
        
        graphThread = new ScheduledService<Void>() {
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    protected Void call() {
                        Platform.runLater(() -> {

                            if (playing) {

                            }

                        });
                        return null;
                    }
                };
            }
        };     
             
        timerThread.setPeriod(Duration.millis(75));
        if(masterTime.get() != 0.0){
            timerThread.setDelay(Duration.millis(0));
        }else{
            timerThread.setDelay(Duration.millis(0));
        }
        timerThread.start(); 
        playing = true;
        startButton.setText("Pause");
        
        
        if (thermalMedia == null && media == null && audioPlayer == null) {
            return;
        }

        
        if (media != null) {

            new Service<Void>() {
                protected Task<Void> createTask() {
                    return new Task<Void>() {
                        protected Void call() {
                            Platform.runLater(() -> {
                                try {
                                    Thread.sleep(0);
                                } catch (InterruptedException ex) {
                                    Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                media.play();
                            });
                            return null;
                        }
                    };
                }
            }.start();

        }
        if (thermalMedia != null) {

            new Service<Void>() {
                protected Task<Void> createTask() {
                    return new Task<Void>() {
                        protected Void call() {
                            Platform.runLater(() -> {
                                try {
                                    Thread.sleep(0);
                                } catch (InterruptedException ex) {
                                    Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                thermalMedia.play();
                            });
                            return null;
                        }
                    };
                }
            }.start();

        }
        if (audioPlayer != null) {

            new Service<Void>() {
                protected Task<Void> createTask() {
                    return new Task<Void>() {
                        protected Void call() {
                            Platform.runLater(() -> {
                                try {
                                    Thread.sleep(0);
                                } catch (InterruptedException ex) {
                                    Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                audioPlayer.play();
                            });
                            return null;
                        }
                    };
                }
            }.start();
        }


        
        
        
        
           
    }
    
    void handlePause(){
         playing = false;
            
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
    
    
    @FXML
    void stop(ActionEvent event) {
        handleStop();
    }
    
    void handleStop(){
        collectButton.setText("Start Marking");
        
        playing = false;
        
        timerThread.cancel();
        
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
        
        refreshGraphs();
        
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
        
        
        
        if (!frequency.getText().matches(numberChecker)) {

            Alert alert = new Alert(AlertType.ERROR, "Make sure frequency field is set to a number", ButtonType.OK);
            alert.setHeaderText("");
            
            alert.showAndWait();

            if (alert.getResult() == ButtonType.YES) {
                return ;
            }
            
            return;
        }
        
        freq = Double.parseDouble(frequency.getText());
        
        
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
              
                String title = (String) data.iterator().next().get(i);
                config.setyAxisTitle( title);
                graphTitles.add(title);
                
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
                dataViewerVector.add(i, new DataViewer());
                    
                
                
                config.showLegend(false);
                config.setPlotTitle("");
                config.setMarginBottom(20);
                config.setMarginTop(20);
                config.setMarginLeft(85);
                config.setMarginRight(15);
                
                
                
                
                dataViewerVector.get(i).updateConfiguration(config);
                //dataViewerVector.get(i).setMinWidth(1600.0 );
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
                
        //bind width of the "playing green bar to current time/total time * wifth of the scrollpane    
        timeBox.widthProperty().bind( Bindings.multiply( masterTime.divide(time), scrollPane.widthProperty().subtract(90.0)));
            
        } catch(FileNotFoundException ex ){
            System.out.println(ex);
        } catch(IOException ex){
            System.out.println(ex);
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
     
     public void refreshGraphs(){ 
         
        for (int i = 0; i < numColumns; i++) {   
            
            dataViewerVector.get(i).resetPlot();            
            dataViewerVector.get(i).updatePlot(plotVector.get(i));
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
    
    

    
    @FXML
    void collect(ActionEvent event) {
        
        
        if(collectButton.getText().equals("Start Marking")){
           
             
            
            Service refreshThread = new Service<Void>() {
                protected Task<Void> createTask() {
                    return new Task<Void>() {
                        protected Void call() {
                            Platform.runLater(() -> {                                
                                                               
                                collectStart = masterTime.get();
                                collectButton.setText("End Marking");
                                markStart.setText(formatter.format(collectStart));
                            });
                            return null;
                        }
                    };
                }
            };
            refreshThread.start();
            
        } else if(collectButton.getText().equals("End Marking")){
            collectEnd = masterTime.get();
            collectButton.setText("Start Marking");
            handlePause();
            
            Service collectThread = new Service<Void>() {
                protected Task<Void> createTask() {
                    return new Task<Void>() {
                        protected Void call() {
                            Platform.runLater(() -> {  
                                collectEnd = masterTime.get(); 
                                markEnd.setText(formatter.format(collectEnd));
                                //handleStop();
                                updateCollect();                                    
                                                        
                            });
                            return null;
                        }
                    };
                }
            };
            collectThread.start();
                       
        }
        

    }
    
    public void updateCollect(){
        
        LineTrace<Double> playTrace = new LineTrace<>();
        playTrace.setTraceColour(TraceColour.BLUE);

        for (int i = 0; i < numColumns; i++) {

            playTrace.setxArray(copyOfRange(timeValues.get(i), (int) Math.round(collectStart * freq), (int) Math.round(collectEnd * freq)));
            playTrace.setyArray(copyOfRange(dataSets.get(i), (int) Math.round(collectStart * freq), (int) Math.round(collectEnd * freq)));

            PlotData plot = new PlotData();
            plot.addTrace(playTrace);
            //dataViewerVector.get(i).resetPlot();

            dataViewerVector.get(i).updatePlot(plotVector.get(i));
            dataViewerVector.get(i).updatePlot(plot);

        }

    }
    
    @FXML
    void addMark(ActionEvent event) {
        
        if(dataViewerVector.isEmpty())
            return;

        if (!markStart.getText().matches(numberChecker) && !markStart.getText().matches(numberChecker)) {

            Alert alert = new Alert(AlertType.ERROR, "Make sure mark Start and End times are valid ", ButtonType.OK);
            alert.setHeaderText("");

            alert.showAndWait();

            if (alert.getResult() == ButtonType.OK) {
                return;
            }

            return;
        }
        if (Double.parseDouble(markStart.getText()) > Double.parseDouble(markEnd.getText())) {

            Alert alert = new Alert(AlertType.ERROR, " Mark Start time must be less than mark End time ", ButtonType.OK);
            alert.setHeaderText("");

            alert.showAndWait();

            if (alert.getResult() == ButtonType.OK) {
                return;
            }

            return;


        }
        if (markTitle.getText() == null || markTitle.getText().trim().isEmpty()) {

            

            Alert alert = new Alert(AlertType.ERROR, "Title for Mark cant be empty", ButtonType.OK);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setHeaderText("");

            alert.showAndWait();

            if (alert.getResult() == ButtonType.OK) {
                return;
            }

            return;

        }
        
        
        
        
         
        
        String title = markTitle.getText();
        Double startTime = Double.parseDouble(markStart.getText());
        Double endTime = Double.parseDouble(markEnd.getText());
        
        Integer startIndex =(int) Math.round(startTime * freq) ;
        Integer endIndex =(int) Math.round(endTime * freq) ;
        
        if(markedValues.containsKey(title)){
            
            Integer markArray[] = markedValues.get(title);
            
            for (int i = 0; i < numColumns; i++) {

                
                if( (i >= startIndex ) && ( i <= endIndex) ){
                    markArray[i] = 1;
                } 
                
            }
            
            
            Alert alert = new Alert(AlertType.CONFIRMATION, "Update the following Mark?\n\nMark: " 
                    + title + "\nfrom: " + formatter.format(startTime) + "\nto: "
                    + formatter.format(endTime), ButtonType.NO, ButtonType.YES);
            alert.setHeaderText("Confirm Mark");
            alert.initModality(Modality.APPLICATION_MODAL);

            alert.showAndWait();

            if (alert.getResult() == ButtonType.YES) {
                markedValues.put(title, markArray);
                
            }
            
            
        }else{
            
            Integer markArray[] = new Integer[numRows];
            
            for (int i = 0; i < numRows; i++) {

                
                if( (i >= startIndex ) && ( i <= endIndex) ){
                    markArray[i] = 1;
                } else{
                    markArray[i] = 0;
                }
                
            }
            
            
            Alert alert = new Alert(AlertType.CONFIRMATION, "Save the following Mark?\n\nMark: " 
                    + title + "\nfrom: " + formatter.format(startTime) + "\nto: "
                    + formatter.format(endTime), ButtonType.NO, ButtonType.YES);
            
            
            alert.setHeaderText("Confirm Mark");
            alert.initModality(Modality.APPLICATION_MODAL);

            alert.showAndWait();

            if (alert.getResult() == ButtonType.YES) {
                markedValues.put(title, markArray);
                
                //System.out.println(markArray.toString());
            }
            
            
        }
        
        
    }
    
    @FXML
    void storeMarks(ActionEvent event) throws IOException {
        
        FileWriter fileWriter = null;
        FileChooser csvChooser = new FileChooser();  
        
        //add filter to only get csv files
        FileChooser.ExtensionFilter csvExtension = 
                new FileChooser.ExtensionFilter("CSV Files ( *.csv)"
                , "*.csv");
        
        
        
        
        csvChooser.getExtensionFilters().add(csvExtension);
        
        csvChooser.setTitle("Select where to save ");
        File selected = csvChooser.showSaveDialog(null);
        
        if (selected != null) {
             fileWriter = new FileWriter(selected);
        } else{
            return;
        }
        
        
       
        
        CSVPrinter csvData = new CSVPrinter(fileWriter, CSVFormat.DEFAULT);
        
        List titleRow = new ArrayList();
        
        //first we add all of our titles
        titleRow.add("time");
        
        for(String title: graphTitles){
            titleRow.add(title);
            
        }
        for(Map.Entry<String, Integer[]> entry: markedValues.entrySet()){
            
            titleRow.add(entry.getKey());
        }
        csvData.printRecord(titleRow);
        
        
        Double[] times = timeValues.get(0);
        //add all data next
        for(int row = 0; row < numRows; row++){
            List rowData = new ArrayList();

            rowData.add(times[row]);

            for (Double[] set : dataSets) {
                rowData.add(set[row]);
            }
            for (Map.Entry<String, Integer[]> entry : markedValues.entrySet()) {

                rowData.add(entry.getValue()[row]);
            }
            
            csvData.printRecord(rowData);
            
        }
        
        
        
        fileWriter.close();
        
        
        
        
        
    }
    
    
    
   
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        
        
        timeArea.textProperty().bind(masterTime.asString("%.2f"));
        
        scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
         
        GraphVBox.maxWidthProperty().bind(scrollPane.widthProperty().subtract(10));
        GraphVBox.minWidthProperty().bind(scrollPane.widthProperty().subtract(10));
        
        //VideoPlayer.fitWidthProperty().bind(videoPane.widthProperty());
        //VideoPlayer.fitHeightProperty().bind(videoPane.heightProperty());
        
       // ThermalVideo.fitWidthProperty().bind(thermalPane.widthProperty());
       // ThermalVideo.fitHeightProperty().bind(thermalPane.heightProperty());
        
        
        
        
        //set the ratio for the split pane to stay constant 
        //and cursor over divider to transparent
        //leftAnchor.maxWidthProperty().bind(splitPane.widthProperty().multiply(0.35));
        //leftAnchor.minWidthProperty().bind(splitPane.widthProperty().multiply(0.35));
        
        
        
        
        splitPane.lookupAll(".split-pane-divider").stream()
            .forEach(div ->  div.setMouseTransparent(true) );
        
        
        
        
    }    
}
