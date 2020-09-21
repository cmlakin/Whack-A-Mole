import java.io.*;
import java.nio.Buffer;
import java.util.Random;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class Main extends Application {

    private Random rand=new Random();
    private final int ROWS = 10;
    private final int COLUMNS = 10;
    private Button moleButton;
    private Button [][] buttons = new Button[ROWS][COLUMNS];
    private Image moleImage = new Image("molePic.png");
    private Timeline timeline;
    private int score=0;
    private int hits=0;
    private int misses=0;
    private int highScore=0;
    private Label scoreLabel = new Label("Score: 0");
    private Label hitLabel = new Label("Hit: 0");
    private Label missLabel = new Label("Miss: 0");
    private Label timeLabel = new Label("Time: 0");
    private Label highScoreLabel = new Label("High Score: 0");
    private int x, y;
    private double rate = 1.0;
    private Timeline timeTimeline;
    private int secondsPassed=0;

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // This is to display  running total for score of the current game being played.
        scoreLabel.setContentDisplay(ContentDisplay.TOP);
        scoreLabel.setTextFill(Color.BLUE);
        scoreLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 12));

        // This is to display the time passed until a miss happens
        timeLabel.setContentDisplay(ContentDisplay.TOP);
        timeLabel.setTextFill(Color.BLUE);
        timeLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 12));

        // This is to display all hits acquired during play
        hitLabel.setContentDisplay(ContentDisplay.TOP);
        hitLabel.setTextFill(Color.GREEN);
        hitLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 12));

        // Displays all misses acquired during play
        missLabel.setContentDisplay(ContentDisplay.TOP);
        missLabel.setTextFill(Color.RED);
        missLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 12));

        // Displays high score of the game.
        highScoreLabel.setContentDisplay(ContentDisplay.LEFT);
        highScoreLabel.setTextFill(Color.BLUE);
        highScoreLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 15));

        Button reset = new Button(); // click this buttons to reset/restart the game
        reset.setContentDisplay(ContentDisplay.RIGHT);
        reset.setText(" RESET ");

        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setPadding(new Insets(1.5, 1.5, 1.5, 1.5));
        // add buttons to gridpane
        for (int i = 0; i < ROWS; ++i) {
            for (int j = 0; j < COLUMNS; ++j) {
                buttons[i][j] = new Button();
                buttons[i][j].setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                buttons[i][j].setPadding(Insets.EMPTY);
                GridPane.setFillWidth(buttons[i][j], true);
                GridPane.setFillHeight(buttons[i][j], true);
                GridPane.setHgrow(buttons[i][j], Priority.ALWAYS);
                GridPane.setVgrow(buttons[i][j], Priority.ALWAYS);
                gridPane.add(buttons[i][j], i, j);
                buttons[i][j].setOnAction(new ButtonOnClick(i, j));
            }
        }

        // hbox at top to display game information
        HBox hboxUpperLabels = new HBox(80);
        hboxUpperLabels.setPadding(new Insets(10, 5, 10, 5));
        hboxUpperLabels.getChildren().addAll(scoreLabel, timeLabel, hitLabel, missLabel);

        // hbox at bottom to hold reset button and high score
        HBox hboxLowerLabels = new HBox(100);
        hboxLowerLabels.setPadding(new Insets(20, 0, 20, 40));
        hboxLowerLabels.getChildren().addAll(highScoreLabel, reset);

        //holds top and bottom hbox panes and gridPane in center for playing field.
        BorderPane pane = new BorderPane();
        pane.setTop(hboxUpperLabels);
        pane.setCenter(gridPane);
        pane.setBottom(hboxLowerLabels);

        Scene scene = new Scene(pane, 500, 500);
        scene.getStylesheets().add("stylesheet.css");
        primaryStage.setTitle("Whac - a - Mole");
        primaryStage.setScene(scene);
        primaryStage.show();
        updateMoleButton();
        // this timeline is used to move the mole and increase rate
        timeline = new Timeline(new KeyFrame(new Duration(2000), new UpdateMole()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        // this timeline is used to keep track of time passed until a miss happens
        timeTimeline = new Timeline(new KeyFrame(new Duration(1000), new UpdateTime()));
        timeTimeline.setCycleCount(Timeline.INDEFINITE);
        timeLabel.setText("Time: " + timeTimeline.getCurrentTime());
        timeTimeline.play();


        reset.setOnAction(event -> {
            //stop game and reset all values
            timeline.stop();
            score = 0;
            hits = 0;
            misses = 0;
            secondsPassed = 0;
            rate = 1;
            scoreLabel.setText("Score: " + (score));
            hitLabel.setText("Hits: " + (hits));
            missLabel.setText("Misses: " + (misses));
            // writes highscore to file
            try {
                FileWriter file = new FileWriter("Game_Scores.txt");
                BufferedWriter output = new BufferedWriter(file);
                System.out.println(highScore);
                output.append(String.valueOf(highScore));
                output.newLine();
                output.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //begins playing again
            timeline.play();
        });
    }

    // handling action of moving the mole
    class UpdateMole implements EventHandler<ActionEvent>{

        @Override
        public void handle(ActionEvent event) {
            updateMoleButton();
            if(rate<5){
                rate+=0.03;
            }
            if (timeline!=null){
                timeline.setRate(rate);
            }
        }
    }

    // handling action of game time
    class UpdateTime implements EventHandler<ActionEvent>{

        @Override
        public void handle(ActionEvent event) {
            secondsPassed+=1;
            timeLabel.setText("Time "+(secondsPassed));
        }
    }

    //This is keeps track of what happens when buttons are clicked.
    class ButtonOnClick implements EventHandler<ActionEvent>{

        int xLocal, yLocal;
        // gets indices of button clicked
        ButtonOnClick(int x, int y){
            this.xLocal=x;
            this.yLocal=y;
        }

        @Override
        public void handle(ActionEvent event) {
            // compares indices of button clicked to indices of mole button
            if(this.xLocal==x&&this.yLocal==y){
                updateMoleButton();
                timeline.stop();
                timeline.play();
                score+=5;
                hits+=1;
                rate+=0.03;
                timeline.setRate(rate);
                scoreLabel.setText("Score: "+(score));
                hitLabel.setText("Hits: "+(hits));
                timeLabel.setText("Time: "+(secondsPassed));
                timeTimeline.stop();
                timeTimeline.play();
            } else {
                misses+=1;
                missLabel.setText("Misses: "+(misses));
                secondsPassed=0;  //resets timeTimeLine
            }
            // compares high score with current score
            if(highScore < score){
                highScore = score;
                highScoreLabel.setText("High Score: "+(highScore));
            }
        }
    }

    // method to randomly select new location for moleButton to move
    private void updateMoleButton(){
        if(moleButton!=null){
            moleButton.setGraphic(null);  //if mole is pictured on button, then remove
        }
        x=rand.nextInt(COLUMNS);
        y=rand.nextInt(ROWS);
        moleButton=buttons[x][y];
        ImageView moleIV=new ImageView(moleImage);
        moleButton.setGraphic(moleIV);
        moleIV.setPreserveRatio(true);  //keeps picture aligned with button size
        moleIV.setFitWidth(moleButton.getWidth()/2);

    }
}