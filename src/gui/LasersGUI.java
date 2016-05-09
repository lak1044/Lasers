package gui;

import backtracking.Backtracker;
import backtracking.Configuration;
import backtracking.SafeConfig;
import backtracking.SafeSolver;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;

import jdk.nashorn.internal.runtime.regexp.joni.Config;
import model.*;

/**
 * The main class that implements the JavaFX UI.   This class represents
 * the view/controller portion of the UI.  It is connected to the model
 * and receives updates from it.
 *
 * @author Sean Strout @ RIT CS
 * @author YOUR NAME HERE
 */
public class LasersGUI extends Application implements Observer {
    /** The UI's connection to the model */
    private LasersModel model;

    /** this can be removed - it is used to demonstrates the button toggle */
    private static boolean status = true;

    /** state of borderPane for GUI */
    private BorderPane borderPane;

    /** state of buttons for update() */
    private GridPane buttonGrid;

    /** state of buttons in array for update() */
    private Button[][] buttonArray;

    /** state used for restart method */
    private String FILENAME;

    private int[] invalidCoordinates = new int[2];


    @Override
    public void init() throws Exception {
        // the init method is run before start.  the file name is extracted
        // here and then the model is created.
        try {
            Parameters params = getParameters();
            String filename = params.getRaw().get(0);
            this.model = new LasersModel(filename);
        } catch (FileNotFoundException fnfe) {
            System.out.println(fnfe.getMessage());
            System.exit(-1);
        }
        this.model.addObserver(this);
    }

    /**
     * A private utility function for setting the background of a button to
     * an image in the resources subdirectory.
     *
     * @param button the button control
     * @param bgImgName the name of the image file
     */
    private void setButtonBackground(Button button, String bgImgName) {
        BackgroundImage backgroundImage = new BackgroundImage(
                new Image( getClass().getResource("resources/" + bgImgName).toExternalForm()),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                BackgroundSize.DEFAULT);
        Background background = new Background(backgroundImage);
        button.setBackground(background);
    }

    /**
     * This is a private demo method that shows how to create a button
     * and attach a foreground image with a background image that
     * toggles from yellow to red each time it is pressed.
     *
     * @param stage the stage to add components into
     */
    private void buttonDemo(Stage stage) {
        // this demonstrates how to create a button and attach a foreground and
        // background image to it.
        Button button = new Button();
        Image laserImg = new Image(getClass().getResourceAsStream("resources/laser.png"));
        ImageView laserIcon = new ImageView(laserImg);
        button.setGraphic(laserIcon);
        setButtonBackground(button, "yellow.png");
        button.setOnAction(e -> {
            // toggles background between yellow and red
            if (!status) {
                setButtonBackground(button, "yellow.png");
            } else {
                setButtonBackground(button, "red.png");
            }
            status = !status;
        });

        Scene scene = new Scene(button);
        stage.setScene(scene);
    }


    private GridPane constructSafeGrid(){
        buttonGrid = new GridPane();
        buttonArray = new Button[model.rows][model.cols];
        for (int i=0; i<model.rows; i++) {
            for (int j = 0; j < model.cols; j++) {

                char toCheck = model.GetVal(i,j);

                //construct emptyButton
                if (toCheck == '.'){
                    Button newButton = new Button();
                    Image whiteImg = new Image(getClass().getResourceAsStream("resources/white.png"));
                    ImageView whiteIcon = new ImageView(whiteImg);
                    newButton.setGraphic(whiteIcon);
                    setButtonBackground(newButton, "white.png");
                    int irow= i;
                    int jcol= j;
                    newButton.setOnAction(event -> model.Add(irow,jcol));
                    buttonGrid.add(newButton, j, i);
                    buttonArray[i][j]=newButton;
                }
                //construct Laser
                else if (toCheck == 'L'){
                    Button newButton = new Button();
                    Image laserImg = new Image(getClass().getResourceAsStream("resources/laser.png"));
                    ImageView laserIcon = new ImageView(laserImg);
                    newButton.setGraphic(laserIcon);
                    setButtonBackground(newButton, "yellow.png");
                    int irow= i;
                    int jcol= j;
                    newButton.setOnAction(event -> model.Remove(irow,jcol));
                    buttonGrid.add(newButton, j, i);
                    buttonArray[i][j]=newButton;
                }
                //construct Beam
                else if (toCheck == '*'){
                    Button newButton = new Button();
                    Image beamImg = new Image(getClass().getResourceAsStream("resources/beam.png"));
                    ImageView beamIcon = new ImageView(beamImg);
                    newButton.setGraphic(beamIcon);
                    setButtonBackground(newButton, "yellow.png");
                    buttonGrid.add(newButton, j, i);
                    buttonArray[i][j]=newButton;
                }
                //construct AnyPillar
                else if (toCheck == 'X'){
                    Button newButton = new Button();
                    Image pillarXImg = new Image(getClass().getResourceAsStream("resources/pillarX.png"));
                    ImageView pillarXIcon = new ImageView(pillarXImg);
                    newButton.setGraphic(pillarXIcon);
                    setButtonBackground(newButton, "white.png");
                    buttonGrid.add(newButton, j, i);
                    buttonArray[i][j]=newButton;
                }
                //construct Pillar0
                else if (toCheck == '0'){
                    Button newButton = new Button();
                    Image pillar0Img = new Image(getClass().getResourceAsStream("resources/pillar0.png"));
                    ImageView pillar0Icon = new ImageView(pillar0Img);
                    newButton.setGraphic(pillar0Icon);
                    setButtonBackground(newButton, "white.png");
                    buttonGrid.add(newButton, j, i);
                    buttonArray[i][j]=newButton;
                }
                //construct Pillar1
                else if (toCheck == '1'){
                    Button newButton = new Button();
                    Image pillar1Img = new Image(getClass().getResourceAsStream("resources/pillar1.png"));
                    ImageView pillar1Icon = new ImageView(pillar1Img);
                    newButton.setGraphic(pillar1Icon);
                    setButtonBackground(newButton, "white.png");
                    buttonGrid.add(newButton, j, i);
                    buttonArray[i][j]=newButton;
                }
                //construct Pillar2
                else if (toCheck == '2'){
                    Button newButton = new Button();
                    Image pillar2Img = new Image(getClass().getResourceAsStream("resources/pillar2.png"));
                    ImageView pillar2Icon = new ImageView(pillar2Img);
                    newButton.setGraphic(pillar2Icon);
                    setButtonBackground(newButton, "white.png");
                    buttonGrid.add(newButton, j, i);
                    buttonArray[i][j]=newButton;
                }
                //construct Pillar3
                else if (toCheck == '3'){
                    Button newButton = new Button();
                    Image pillar3Img = new Image(getClass().getResourceAsStream("resources/pillar3.png"));
                    ImageView pillar3Icon = new ImageView(pillar3Img);
                    newButton.setGraphic(pillar3Icon);
                    setButtonBackground(newButton, "white.png");
                    buttonGrid.add(newButton, j, i);
                    buttonArray[i][j]=newButton;
                }
                //construct Pillar4
                else if (toCheck == '4'){
                    Button newButton = new Button();
                    Image pillar4Img = new Image(getClass().getResourceAsStream("resources/pillar4.png"));
                    ImageView pillar4Icon = new ImageView(pillar4Img);
                    newButton.setGraphic(pillar4Icon);
                    setButtonBackground(newButton, "white.png");
                    buttonGrid.add(newButton, j, i);
                    buttonArray[i][j]=newButton;
                }
                /*
                Button newButton = new Button();
                Image whiteImg = new Image(getClass().getResourceAsStream("resources/white.png"));
                ImageView whiteIcon = new ImageView(whiteImg);
                newButton.setGraphic(whiteIcon);
                setButtonBackground(newButton, "yellow.png");
                buttonGrid.add(newButton, j, i);
                */

            }
        }
        return buttonGrid;
    }


    private HBox constructCommandButtons (){
        HBox commandBox = new HBox();
        Button checkButton = new Button("Check");
        checkButton.setOnAction(event -> {
            invalidCoordinates[0]=-1;
            invalidCoordinates[1]=-1;
            model.Verify();
        });// invalidCoordinates = model.invalidCoordinates);
        Button hintButton = new Button("Hint");
        //hintButton.setOnAction(event -> model.FINDMETHOD);
        hintButton.setOnAction(event2 -> {
                Optional sol = new Backtracker(false).solve(new SafeConfig(model));
                if (sol.isPresent()){
                    SafeConfig solution = (SafeConfig)sol.get();
                    for (String s: solution.getLaserHash().keySet()){
                        if (!model.getLaserHash().keySet().contains(s)){
                            model.Add(solution.getLaserHash().get(s).getRow(), solution.getLaserHash().get(s).getCol());
                            break;
                        }
                    }
                }
        });
        Button solveButton = new Button("Solve");
        //solveButton.setOnAction(event -> FIND METHOD);
        solveButton.setOnAction(event1 -> {
            try {
                Optional sol = new Backtracker(false).solve(new SafeConfig(model.fileName));
                if (sol.isPresent()){
                    SafeConfig solution = (SafeConfig)sol.get();
                    this.model.copySafconfig(solution);
                }
            } catch (FileNotFoundException e) {}
        });
        Button restartButton = new Button("Restart");
        restartButton.setOnAction(event -> model.Restart());
        Button loadButton = new Button("Load");
        //loadButton.setOnAction ( event -> load filename)

        commandBox.getChildren().addAll(checkButton,hintButton,solveButton,restartButton,loadButton);

        return commandBox;
    }

    /**
     * The
     * @param stage the stage to add UI components into
     */
    private void init(Stage stage) {
        // TODO

        borderPane = new BorderPane();
        Label statusLabel = new Label(model.fileName + " loaded");
        statusLabel.setAlignment(Pos.CENTER);
        borderPane.setTop(statusLabel);
        borderPane.setCenter(constructSafeGrid());
        borderPane.setBottom(constructCommandButtons());



        Scene scene = new Scene(borderPane);
        stage.setScene(scene);

        /*this.model = new LasersModel();
        this.model.addObserver(this);*/
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // TODO
        init(primaryStage);  // do all your UI initialization here

        primaryStage.setTitle("Lasers");
        primaryStage.show();
    }

    @Override
    public void update(Observable o, Object arg) {
        // TODO

        for (int i=0; i<model.rows; i++) {
            for (int j = 0; j < model.cols; j++) {

                char toCheck = model.GetVal(i,j);
                Button currButton = buttonArray[i][j];

                //construct emptyButton
                if (toCheck == '.'){
                    //Button newButton = new Button();
                    Image whiteImg = new Image(getClass().getResourceAsStream("resources/white.png"));
                    ImageView whiteIcon = new ImageView(whiteImg);
                    currButton.setGraphic(whiteIcon);
                    setButtonBackground(currButton, "white.png");
                    int irow= i;
                    int jcol= j;
                    currButton.setOnAction(event -> model.Add(irow,jcol));
                    //buttonGrid.add(currButton, j, i);
                    //currButton = newButton;
                }
                //construct Laser
                else if (toCheck == 'L'){
                    //Button newButton = new Button();
                    Image laserImg = new Image(getClass().getResourceAsStream("resources/laser.png"));
                    ImageView laserIcon = new ImageView(laserImg);
                    currButton.setGraphic(laserIcon);
                    setButtonBackground(currButton, "yellow.png");
                    int irow= i;
                    int jcol= j;
                    currButton.setOnAction(event -> model.Remove(irow,jcol));
                    //buttonGrid.add(currButton, j, i);
                    //currButton = newButton;
                }
                //construct Beam
                else if (toCheck == '*'){
                    //Button newButton = new Button();
                    Image beamImg = new Image(getClass().getResourceAsStream("resources/beam.png"));
                    ImageView beamIcon = new ImageView(beamImg);
                    currButton.setGraphic(beamIcon);
                    setButtonBackground(currButton, "yellow.png");
                    int irow= i;
                    int jcol= j;
                    currButton.setOnAction(event -> model.Add(irow,jcol));
                    //buttonGrid.add(currButton, j, i);
                    //currButton = newButton;
                }
                else{
                    //Button newButton = new Button();
                    //Image pillarXImg = new Image(getClass().getResourceAsStream("resources/pillarX.png"));
                    //ImageView pillarXIcon = new ImageView(pillarXImg);
                    //newButton.setGraphic(pillarXIcon);
                    //setButtonBackground(newButton, "white.png");
                    //buttonGrid.add(newButton, j, i);
                    //buttonArray[i][j]=newButton;
                    setButtonBackground(currButton,"white.png");
                }
            }
        }


        //Check for invalid positions.
        invalidCoordinates = model.getInvalidCoordinates();
        if (invalidCoordinates[0]>=0 || invalidCoordinates[1]>=0){
            Button currButton = buttonArray[invalidCoordinates[0]][invalidCoordinates[1]];
            if (model.GetVal(invalidCoordinates[0],invalidCoordinates[1])=='.'){
                Image redImg = new Image(getClass().getResourceAsStream("resources/red.png"));
                ImageView redIcon = new ImageView(redImg);
                currButton.setGraphic(redIcon);
            }
            setButtonBackground(currButton,"red.png");
        }
        invalidCoordinates[0]=-1;
        invalidCoordinates[1]=-1;
    }
}
