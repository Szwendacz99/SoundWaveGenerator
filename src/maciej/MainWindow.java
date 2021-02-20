package maciej;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sound.sampled.Mixer;


public class MainWindow extends Application {

    GeneratorThreadManager generatorThreadManager = new GeneratorThreadManager();
    SoundOutputsManager soundOutputsManager = new SoundOutputsManager();
    SoundParameters soundParameters = new SoundParameters();
    static final Logger logger = LogManager.getLogger(MainWindow.class);

    Slider slider1;
    Slider slider2;
    ChoiceBox<Mixer.Info> audioOutputs = new ChoiceBox<Mixer.Info>();
    RadioButton sinusWave;
    RadioButton squareWave;
    RadioButton triangularWave;
    RadioButton sawtoothWave;
    RadioButton whiteNoiseWave;
    Text audioOutputInfoDisplay;
    Text statusText;

    @Override
    public void start(Stage primaryStage) {

        BorderPane mainPane = new BorderPane();

        mainPane.setTop(statusText());
        mainPane.setRight(audioOutputInfoPanel());
        mainPane.setCenter(mainSlidersPane());
        mainPane.setBottom(controlsHBox());


        primaryStage.setOnCloseRequest((WindowEvent e) -> {
                Platform.exit();
                System.exit(0);
            });

        Scene scene = new Scene(mainPane, 600, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }

    private void startPlaying(){
        WaveShape shape = WaveShape.SIN;
        if (squareWave.isSelected())
            shape = WaveShape.SQUARE;
        else if (triangularWave.isSelected())
            shape = WaveShape.TRIANGULAR;
        else if (sawtoothWave.isSelected())
            shape = WaveShape.SAWTOOTH;
        else if (whiteNoiseWave.isSelected())
            shape = WaveShape.WHITE_NOISE;

        try {
            soundParameters.setParameters(soundOutputsManager.getOutputLine(audioOutputs.getValue()),
                    (int)slider1.getValue(), shape, (short)slider2.getValue());
            updateStatus("Currently playing on " + audioOutputs.getValue(), Color.GREEN);
            generatorThreadManager.startNewThread(soundParameters);
        } catch (Exception e) {
            updateStatus("Could not start playing on " + audioOutputs.getValue(), Color.RED);
            e.printStackTrace();
        }

    }

    private VBox radioWaveShape(){
        VBox radioWaveShapeVbox = new VBox();
        radioWaveShapeVbox.setSpacing(20);
        radioWaveShapeVbox.setAlignment(Pos.CENTER_LEFT);
        ToggleGroup waveShape = new ToggleGroup();

        sinusWave = new RadioButton("Sine Wave");
        sinusWave.setToggleGroup(waveShape);
        sinusWave.setOnAction((event -> soundParameters.setWaveShape(WaveShape.SIN)));
        squareWave = new RadioButton("Square Wave");
        squareWave.setToggleGroup(waveShape);
        squareWave.setOnAction((event -> soundParameters.setWaveShape(WaveShape.SQUARE)));
        triangularWave = new RadioButton("Triangular Wave");
        triangularWave.setToggleGroup(waveShape);
        triangularWave.setOnAction((event -> soundParameters.setWaveShape(WaveShape.TRIANGULAR)));
        sawtoothWave = new RadioButton("Sawtooth Wave");
        sawtoothWave.setToggleGroup(waveShape);
        sawtoothWave.setOnAction((event -> soundParameters.setWaveShape(WaveShape.SAWTOOTH)));
        whiteNoiseWave = new RadioButton("White Noise Wave");
        whiteNoiseWave.setToggleGroup(waveShape);
        whiteNoiseWave.setOnAction((event -> soundParameters.setWaveShape(WaveShape.WHITE_NOISE)));

        sinusWave.setSelected(true);
        radioWaveShapeVbox.getChildren().addAll(sinusWave, squareWave, triangularWave, sawtoothWave, whiteNoiseWave);
        return radioWaveShapeVbox;
    }

    private GridPane mainSlidersPane(){
        GridPane slidersPane = new GridPane();
//        slidersPane.setStyle("-fx-background-color: aquamarine;");
        slidersPane.setAlignment(Pos.CENTER);
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(50);
        column1.setHalignment(HPos.CENTER);
        ColumnConstraints column2 = new ColumnConstraints();
        column2.setPercentWidth(50);
        column2.setHalignment(HPos.CENTER);
        ColumnConstraints column3 = new ColumnConstraints();
        column3.setPercentWidth(50);
        column3.setHalignment(HPos.CENTER);
        slidersPane.getColumnConstraints().addAll(column1, column2, column3);
        RowConstraints row1 = new RowConstraints();
        RowConstraints row2 = new RowConstraints();
        row2.setVgrow(Priority.ALWAYS);
        slidersPane.getRowConstraints().addAll(row1, row2);
        slidersPane.setHgap(10);
        slidersPane.setVgap(10);
        slidersPane.setPadding(new Insets(20, 20, 20, 20));

        Text frequencyText = new Text("Frequency");
        slidersPane.add(frequencyText, 0, 0);

        slider1 = new Slider(0,30000,50);
        slider1.setOrientation(Orientation.VERTICAL);
        slider1.setShowTickLabels(true);
        slider1.setShowTickMarks(true);
        slider1.setBlockIncrement(1);
        slider1.setMajorTickUnit(5000);
        slider1.setMinorTickCount(2500);
        slider1.adjustValue(5000);
        slidersPane.add(slider1,0,1);

        Label slider1Value = new Label(String.valueOf((int)slider1.getValue()));
        slidersPane.add(slider1Value,0,2);

        slider1.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            slider1.setValue(newValue.intValue());
            slider1Value.setText(String.valueOf(newValue.intValue()));
            soundParameters.setFrequency(newValue.intValue());
            logger.info("changing frequency to "+ newValue.intValue());
        });

        Text VolumeText = new Text("Volume");
        slidersPane.add(VolumeText, 1, 0);

        slider2 = new Slider(0,100,1);
        slider2.setOrientation(Orientation.VERTICAL);
        slider2.setShowTickLabels(true);
        slider2.setShowTickMarks(true);
        slider2.adjustValue(50);
        slidersPane.add(slider2,1,1);

        Label slider2Value = new Label(String.valueOf((int)slider2.getValue()));
        slidersPane.add(slider2Value,1,2);

        slider2.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            slider2.setValue(newValue.shortValue());
            slider2Value.setText(String.valueOf(newValue.shortValue()));
            soundParameters.setVolume(newValue.shortValue());
            logger.info("changing volume to "+ newValue.intValue());
        });

        slider2.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            slider2.setValue(newValue.intValue());
            slider2Value.setText(String.valueOf(newValue.intValue()));
        });

        slidersPane.add(radioWaveShape(),2,1);

        return slidersPane;
    }

    private HBox controlsHBox(){
        HBox controlsHbox = new HBox();
        controlsHbox.setAlignment(Pos.CENTER_LEFT);
        controlsHbox.setSpacing(8);
        controlsHbox.setPadding(new Insets(10, 10, 10, 10));

        Button startButton = new Button("Start");
        Button stopButton = new Button("Stop");

        Text outputChooseText = new Text("Audio output:");

        audioOutputs.getItems().addAll(soundOutputsManager.getAllOutputsInfos());
        for (Mixer.Info item:audioOutputs.getItems()){
            if (item.toString().toLowerCase().contains("default"))
            {
                audioOutputs.setValue(item);
                updateOutputInfo(item);
                break;
            }
        }
        audioOutputs.onActionProperty().setValue((ActionEvent e) -> {
            updateOutputInfo(audioOutputs.getValue());
        });

        startButton.setOnAction((ActionEvent e) -> {
            startPlaying();
        });
        stopButton.setOnAction((ActionEvent e) -> stopPlaying());

        controlsHbox.getChildren().addAll(startButton, stopButton, outputChooseText, audioOutputs);
        return controlsHbox;
    }

    private void stopPlaying(){
        generatorThreadManager.stopAllThreads();
        updateStatus("Stopped playing", Color.GRAY);
    }

    private VBox audioOutputInfoPanel(){

        VBox controlsHbox = new VBox();
        controlsHbox.setAlignment(Pos.TOP_LEFT);
        controlsHbox.setSpacing(8);
        controlsHbox.setPadding(new Insets(20, 10, 10, 10));

        Text title = new Text("Output info:");
        title.setFont(Font.font(12));

        audioOutputInfoDisplay = new Text();
        audioOutputInfoDisplay.setFill(Color.GRAY);
        audioOutputInfoDisplay.setText("");
        audioOutputInfoDisplay.setWrappingWidth(150);

        controlsHbox.getChildren().addAll(title, audioOutputInfoDisplay);
        return controlsHbox;
    }

     private void updateOutputInfo(Mixer.Info info){
        StringBuffer buff = new StringBuffer();

        buff.append("Name: ");
        buff.append(info.getName()).append("\n\n");

         buff.append("Description: ");
         buff.append(info.getDescription()).append("\n\n");

         buff.append("Version: ");
         buff.append(info.getVersion()).append("\n\n");

         buff.append("Vendor: ");
         buff.append(info.getVendor()).append("\n\n");

         audioOutputInfoDisplay.setText(buff.toString());
    }

    private HBox statusText(){
        HBox controlsHbox = new HBox();
        controlsHbox.setAlignment(Pos.CENTER_LEFT);
        controlsHbox.setSpacing(8);
        controlsHbox.setPadding(new Insets(10, 10, 10, 10));

        statusText = new Text("Program loaded successfully");
        statusText.setFont(new Font(14));
        controlsHbox.getChildren().add(statusText);

        return controlsHbox;
    }

    private void updateStatus(String status, Color color){
        statusText.setText(status);
        statusText.setFill(color);
    }
}