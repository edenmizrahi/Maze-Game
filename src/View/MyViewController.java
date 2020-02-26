package View;

import ViewModel.MyViewModel;
import algorithms.mazeGenerators.Maze;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;
import java.util.ResourceBundle;
/**
 * Created by Eden Mizrahi and Yarden Schwartz
 * MyViewController class get all the action from the client and tranfer them to MyVieModel class
 */
public class MyViewController implements Observer, IView , Initializable {

    //region *FIELDS*
    @FXML
    private MyViewModel viewModel;
    public MazeDisplayer mazeDisplayer;
    public javafx.scene.control.TextField txtfld_rowsNum;
    public javafx.scene.control.TextField txtfld_columnsNum;
    public javafx.scene.control.Label lbl_rowsNum;
    public javafx.scene.control.Label lbl_columnsNum;
    public javafx.scene.control.Button btn_generateMaze;
    public javafx.scene.control.Button btn_SolveMaze;
    public javafx.scene.control.Button btn_sound;
    public BorderPane board;
    public Pane pane;
    private BackgroundImage background;
    private IntegerProperty height;
    private IntegerProperty width;
    boolean isGenerate;
    double mouseCurrentX;
    double mouseCurrentY;
    private Stage stage;
    private boolean isLoad;
    boolean path=false;
    boolean firtTimeProperties = false;
    public Button Story;
    public Button b_tnHint;
    private String UserName;
    int numOfHint=0 ;
    boolean win=false;
    public MediaPlayer mediaPlayerSound;
    public MediaPlayer mediaPlayerSoundWin;
    private boolean isSolve;


    public StringProperty characterPositionRow = new SimpleStringProperty();
    public StringProperty characterPositionColumn = new SimpleStringProperty();
    //endregion

    //region *CONTROLLER*
    /**
     * initialize a controller after its root element has been
     * completely processed.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  <tt>null</tt> if the location is not known.
     * @param resources The resources used to localize the root object, or <tt>null</tt> if
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //btn_generateMaze.setDisable(true);
        mediaPlayerSound = new MediaPlayer(new Media(getClass().getResource("/Images/MainSong.mp3").toExternalForm()));
        mediaPlayerSoundWin = new MediaPlayer(new Media(getClass().getResource("/Images/winningSong.mp3").toExternalForm()));
        firtTimeProperties=true;
        isLoad = false;
        isGenerate=false;
        /*************for resize ***********************/
        width = new SimpleIntegerProperty(650);
        height = new SimpleIntegerProperty(650);
        width.bind(board.widthProperty());
        height.bind(board.heightProperty());
        backgroundSize();
        pane.prefHeightProperty().bind(board.heightProperty());
        pane.prefWidthProperty().bind(board.widthProperty());
        mazeDisplayer.heightProperty().bind(pane.heightProperty());
        mazeDisplayer.widthProperty().bind(pane.widthProperty());
        mazeDisplayer.heightProperty().addListener((observable, oldValue, newValue) -> {
            if(viewModel.getMaze() != null)
                displayMaze(viewModel.getMaze());
        });
        mazeDisplayer.widthProperty().addListener((observable, oldValue, newValue) -> {
            if(viewModel.getMaze() != null)
                displayMaze(viewModel.getMaze());
        });
        ;
        startMusic();
        mediaPlayerSound.setCycleCount(MediaPlayer.INDEFINITE);
    }


    /**
     * update view after changes at model and view model
     * @param o
     * @param arg
     */
    @Override
    public void update(Observable o, Object arg) {
        if (o == viewModel) {
            /**if the user entered on home we unable the solve button*/
            if(viewModel.isHome()){
                btn_SolveMaze.setDisable(false);
                viewModel.setHome(false);
            }

            if(viewModel.getLoadProblem()){
                this.showAlert("There is a problem with loading, choose another file");
            }
            /**if user load a saved game*/
            if(isLoad) {
                mazeDisplayer.setCharacterPath(viewModel.getCharacterPath());
                isLoad = false;
            }

            displayMaze(viewModel.getMaze());
            btn_generateMaze.setDisable(false);
        }
    }
    //endregion


    //region *RESPONES TO USER*
    /**
     * display the maze on the screen
     * @param maze to display
     */
    @Override
    public void displayMaze(Maze maze) {
        /**update the character position*/
        int characterPositionRow = viewModel.getCharacterPositionRow();
        int characterPositionColumn = viewModel.getCharacterPositionColumn();
        this.characterPositionRow.set(characterPositionRow + "");
        this.characterPositionColumn.set(characterPositionColumn + "");
        mazeDisplayer.setCharacterPosition(characterPositionRow, characterPositionColumn);

        int rowMaze = viewModel.getMaze().rowLimit()+1;
        int colMaze = viewModel.getMaze().colLimit()+1;
        /**optional - for showing the character position to user*/
        txtfld_rowsNum.setText(Integer.toString(colMaze));
        txtfld_columnsNum.setText(Integer.toString(rowMaze));

        /**sending the maze to maze Displayer for drawing*/
        mazeDisplayer.setMaze(maze);
        /**if user is in the goal*/
        if (viewModel.getGoal().getRowIndex() == characterPositionRow && viewModel.getGoal().getColumnIndex() == characterPositionColumn) {
            wining();
            b_tnHint.setDisable(true);
            btn_SolveMaze.setDisable(true);
            win=true;
        }
    }


    /**
     *Called when user is win - opening the wining window and play the wining music
     */
    private void wining(){
        try {
            /**open a new stage**/
            Stage stage = new Stage();
            stage.setTitle("WIN!");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("Win.fxml").openStream());
            winController winer= fxmlLoader.getController();
            winer.setWinnerName(UserName);
            Scene scene = new Scene(root, 400, 350);

            /**wining window**/
            //jon
            if(mazeDisplayer.characterPath.equals("resources/Images/jonS1.jpg")){
                scene.getStylesheets().add(getClass().getResource("jonWin.css").toExternalForm());
            }
            //arya
            if(mazeDisplayer.characterPath.equals("resources/Images/aryaS1.png")){
                scene.getStylesheets().add(getClass().getResource("aryaWin.css").toExternalForm());
            }
            //tryon
            if(mazeDisplayer.characterPath.equals( "resources/Images/tyrionT1.png")){
                scene.getStylesheets().add(getClass().getResource("tiryonWin.css").toExternalForm());
            }//dayneris
            if(mazeDisplayer.characterPath.equals("resources/Images/halisi6.png")){
                scene.getStylesheets().add(getClass().getResource("DynereesWin.css").toExternalForm());
            }
            /**stage size**/
            stage.setMinWidth(400);
            stage.setMaxWidth(400);
            stage.setMinHeight(600);
            stage.setMaxHeight(600);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.setResizable(false);

            stopMusic();
            mediaPlayerSoundWin.play();
            stage.showAndWait();
            mediaPlayerSoundWin.stop();
            startMusic();

        } catch (Exception e) {
        }
    }


    /**
     * show alert with the input string
     * @param alertMessage the alert message
     */
    public void showAlert(String alertMessage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("");
        DialogPane dialogPane = alert.getDialogPane();
        alert.setTitle("Warning");
        alert.setContentText(alertMessage);
        dialogPane.getStylesheets().add(getClass().getResource("alert.css").toExternalForm());
        dialogPane.getStyleClass().add("alert");
        alert.show();
        mazeDisplayer.requestFocus();
    }


    /**
     * on mouse click event - give focus to maze
     * @param mouseEvent
     */
    public void mouseClicked(MouseEvent mouseEvent) {
        this.mazeDisplayer.requestFocus();
    }


    /**
     * close program safely - close the windows and servers
     * @return
     */
    public boolean CloseProgram() {
        if(func()){
            viewModel.stopServers();
            Platform.exit();
            System.exit(0);
            return true;
        }
        return false;
    }


    /**
     * check if user want to exit and save
     * @return true for save and false if not
     */
    public boolean func(){
        boolean exit = false;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Are you sure you want to exit?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK ) {
            //model.stopServers();
            if(isGenerate) {
                Alert toSave = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to save your maze?");
                Optional<ButtonType> tosave_result = toSave.showAndWait();
                if (tosave_result.get() == ButtonType.OK) {
                    //save maze
                    /***** save func ***/
                    this.saveMaze();
                }
            }
            exit=true;
        }
        return exit;
    }


    /**
     * called on mouse press and save the mouse coordinates
     * @param mouseEvent
     */
    public void mousePressed(MouseEvent mouseEvent) {
        mouseCurrentX = mouseEvent.getX();
        mouseCurrentY = mouseEvent.getY();
        mouseEvent.consume();
    }


    /**
     * close the video when the user exit the video window and start the music
     * @param Stage
     * @param player
     */
    private void SetStageCloseEvent(Stage Stage, MediaPlayer player) {
        Stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent windowEvent) {
                player.pause();
                startMusic();
            }
        });
    }
    //endregion


    //region *SETTERS AND GETTERS*

    /**
     * set the view model
     * @param viewModel
     */
    public void setViewModel(MyViewModel viewModel) {
        this.viewModel = viewModel;
    }



    /**
     * getters to characterPositionRow field
     * @return characterPositionRow string value
     */
    public String getCharacterPositionRow() {
        return characterPositionRow.get();
    }


    /**
     * getters to characterPositionRow field
     * @return characterPositionRow StringProperty value
     */
    public StringProperty characterPositionRowProperty() {
        return characterPositionRow;
    }


    /**
     * getters to getCharacterPositionColumn field
     * @return getCharacterPositionColumn String value
     */
    public String getCharacterPositionColumn() {
        return characterPositionColumn.get();
    }


    /**
     * getters to characterPositionColumn field
     * @return characterPositionColumn StringProperty value
     */
    public StringProperty characterPositionColumnProperty() {
        return characterPositionColumn;
    }


    /**
     * save the user name
     * @param name
     */
    public void setUserName(String name) {
        this.UserName = name;
    }



    /**
     * save the path of the image of choosen character
     * @param path
     */
    public void characterChoice(String path){
        mazeDisplayer.setCharacterPath(path);
    }

    /**
     * Called to set a Stage
     * @param stage to set
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Called to set a Text Field of row number
     * @param txtfld_rowsNum to set
     */
    public void setTxtfld_rowsNum(TextField txtfld_rowsNum) {
        this.txtfld_rowsNum = txtfld_rowsNum;
    }

    /**
     * Called to set a Text Field of columns number
     * @param txtfld_columnsNum to set
     */
    public void setTxtfld_columnsNum(TextField txtfld_columnsNum) {
        this.txtfld_columnsNum = txtfld_columnsNum;
    }
    //endregion


    //region *MOUSE DRAG *
    /**
     * called on mouse drag event and operate it
     * @param mouseEvent
     */
    public void mouseDragged(MouseEvent mouseEvent) {
        double mouseX = mouseEvent.getX();
        double mouseY = mouseEvent.getY();

        /**character position:**/
        double characterStartX = mazeDisplayer.getxCharacterPixel();
        double characterStartY = mazeDisplayer.getyCharacterPixel() ;

        double characterEndX= characterStartX + mazeDisplayer.getH();
        double characterEndY= characterStartY + mazeDisplayer.getW();

        /**if the mouse click is on the character**/
        if (mouseCurrentX <= characterEndX && mouseCurrentX >= characterStartX
                && mouseCurrentY <= characterEndY && mouseCurrentY >= characterStartY) {
            KeyCode movement = getKeyCode(mouseX, mouseY,characterStartX,characterStartY,characterEndX,characterEndY);
            /**legal movement**/
            if(movement!=null) {
                viewModel.moveCharacter(movement);
            }
        }
    }

    /**
     * get the key code of mouse event
     * @param mouseX
     * @param mouseY
     * @param characterStartX
     * @param characterStartY
     * @param characterEndX
     * @param characterEndY
     * @return
     */
    private KeyCode getKeyCode(double mouseX, double mouseY, double characterStartX, double characterStartY, double characterEndX, double characterEndY) {

        //RIGHT
        if (mouseY <= characterEndY && mouseY >= characterStartY && mouseX > characterEndX)
            return KeyCode.NUMPAD6;
        //LEFT
        if (mouseY <= characterEndY && mouseY >= characterStartY && mouseX < characterStartX)
            return KeyCode.NUMPAD4;
        //UP
        if (mouseX >= characterStartX && mouseX <= characterEndX && mouseY < characterStartY)
            return KeyCode.NUMPAD8;
        //DOWN
        if (mouseX >= characterStartX && mouseX <= characterEndX && mouseY > characterEndY)
            return KeyCode.NUMPAD2;


        /**DIAGONAL MOVEMENT:**/

        //Diagonal RIGHT down
        if (mouseX > characterEndX && mouseY > characterEndY)
            return KeyCode.NUMPAD3;
        //Diagonal RIGHT UP
        if (mouseX > characterEndX && mouseY < characterStartY)
            return  KeyCode.NUMPAD9;
        //Diagonal LEFT UP
        if (mouseX < characterStartX && mouseY < characterStartY)
            return KeyCode.NUMPAD7;
        //Diagonal LEFT DOWN
        if (mouseX < characterStartX && mouseY > characterEndY)
            return KeyCode.NUMPAD1;


        return null;

    }
    //endregion


    //region *RESIZE*
    /**
     * Called on Resize event and operate it
     * @param scene
     */
    public void setResizeEvent(Scene scene) {

        scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                mazeDisplayer.redraw();

            }
        });
        scene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                mazeDisplayer.redraw();
            }
        });

    }


    /**
     * called on scroll event and resize the maze size
     * @param s
     */
    public void Scroll(ScrollEvent s){
        if(this.viewModel.getMaze()!=null&&s.isControlDown()) {
            double factor = 1.1D;
            double delta = s.getDeltaY();
            if (delta > 0.0D) {
                mazeDisplayer.setZoomSize(mazeDisplayer.getZoomSize() * factor);
            } else {
                mazeDisplayer.setZoomSize(mazeDisplayer.getZoomSize() / factor);
            }
            mazeDisplayer.redraw();
            s.consume();
        }
    }

    /**
     * set the background
     */
    public void backgroundSize() {
        background = new BackgroundImage(new Image("/Images/backround5.jpg",
                width.intValue(), height.intValue(),false,true),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
        board.setBackground(new Background(background));
    }
    //endregion


    //region *MAIN BUTTONS*
    /**
     * called when user ckick on hint and operate it
     * @param actionEvent
     */
    public void hint(ActionEvent actionEvent) {

        if (isGenerate) {
            if (numOfHint < 3) {

                b_tnHint.setDisable(true);
                btn_SolveMaze.setDisable(true);
                numOfHint++;
                viewModel.hint();

            } else {
                showAlert("You wasted all off your three hints");
                b_tnHint.setDisable(true);
            }
        }
        else{
            showAlert("You should generate maze first");
        }

    }

    /**
     * called when user click to "choose character" button
     * and open the choose character window
     */
    public void ChooseChar() {
        try {
            path=true;
            Stage stage = new Stage();
            stage.setTitle("ChooseCharacter");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("characterChoise.fxml").openStream());
            characterController singer = fxmlLoader.getController();
            singer.initViewController(this);
            Scene scene = new Scene(root);
            scene.getStylesheets().add("View/characterChoise.css");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.setMinWidth(600);
            stage.setMaxWidth(600);
            stage.setMinHeight(625);
            stage.setMaxHeight(625);
            stage.show();

        } catch (Exception e) {
            System.out.println("Error ChooseSinger.fxml not found");
        }
    }

    /**
     * play the video on pressed "the story" button
     * @param actionEvent
     * @throws IOException
     */
    public void playVideo(ActionEvent actionEvent) throws IOException {
        Stage stage1 = new Stage();
        stage1.setTitle("The Story");
        StackPane root = new StackPane();
        MediaPlayer player = new MediaPlayer(new Media(getClass().getResource("/Images/GOTvideo.mp4").toExternalForm()));
        MediaView mediaView = new MediaView(player);
        root.getChildren().add(mediaView);
        Scene scene = new Scene(root, 1024, 768);
        stage1.setScene(scene);
        stage1.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
        stage1.setMinWidth(650);
        stage1.setMaxWidth(650);
        stage1.setMinHeight(390);
        stage1.setMaxHeight(390);
        stage1.show();
        player.play();
        if(mediaPlayerSound.getStatus() == MediaPlayer.Status.PLAYING){
            stopMusic();
        }
        SetStageCloseEvent(stage1, player);
    }

    /**
     * called after click on the solve button
     * @param actionEvent
     */
    public void solveMaze(ActionEvent actionEvent) {
        if(isGenerate) {
            btn_SolveMaze.setDisable(true);
            btn_generateMaze.setDisable(false);
            b_tnHint.setDisable(true);
            isSolve=true;
            txtfld_rowsNum.setDisable(false);
            txtfld_columnsNum.setDisable(false);
            viewModel.Solve();
        }
        else{
            showAlert("you must generate maze first");
        }
    }

    /**
     *Called when the user entered on the new game button
     */
    public void generateMaze() {
        try {
            /**check the input **/
            Integer.parseInt(txtfld_rowsNum.getText());
            Integer.parseInt(txtfld_columnsNum.getText());
            if(Integer.parseInt(txtfld_rowsNum.getText())>1000||Integer.parseInt(txtfld_columnsNum.getText())>1000){
                throw  new NumberFormatException();
            }
            /**if character already choosed*/
            if(path) {
                win=false;
                btn_SolveMaze.setDisable(false);
                b_tnHint.setDisable(false);
                isGenerate=true;
                numOfHint=0;
                isSolve=false;
                int heigth = Integer.valueOf(txtfld_rowsNum.getText());
                int width = Integer.valueOf(txtfld_columnsNum.getText());
                btn_generateMaze.setDisable(true);
                viewModel.generateMaze(width, heigth);
            }
            else{
                this.showAlert("Please choose your character");
            }
            btn_generateMaze.setDisable(false);
        } catch (NumberFormatException e) {
            showAlert("pleas enter a valid number between 5-1000");
        } catch (NullPointerException e) {
            showAlert("pleas enter a valid number between 5-1000");
        }
    }


    /**
     *called on entered music button - stop or start the music
     */
    public void onSoundClick(){
        if(mediaPlayerSound.getStatus() == MediaPlayer.Status.PAUSED ||
                mediaPlayerSound.getStatus() == MediaPlayer.Status.READY || mediaPlayerSound.getStatus() == MediaPlayer.Status.STOPPED ){
            startMusic();
        }
        else{
            if(mediaPlayerSound.getStatus() == MediaPlayer.Status.PLAYING) {
                stopMusic();
            }
        }
    }
    //endregion


    //region *MENU BAR OPTIONS*

    /**
     * open properties window
     * @param actionEvent
     */
    public void properties (ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();
            stage.setTitle("Properties");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("properties.fxml").openStream());
            propertiesController singer = fxmlLoader.getController();
            singer.initializeSecond(this);
            Scene scene = new Scene(root);
            scene.getStylesheets().add("View/properties.css");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.setMinWidth(500);
            stage.setMaxWidth(500);
            stage.setMinHeight(350);
            stage.setMaxHeight(350);
            stage.show();
        } catch (Exception e) {
            //.out.println("Error ChooseSinger.fxml not found");
        }
    }

    /**
     * open the About window
     * @param actionEvent
     */
    public void About(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();
            stage.setTitle("About");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("About.fxml"));
            Scene scene = new Scene(root, 400, 350);
            scene.getStylesheets().add(getClass().getResource("About.css").toExternalForm());

            stage.setMinWidth(590);
            stage.setMaxWidth(590);
            stage.setMinHeight(320);
            stage.setMaxHeight(320);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.setResizable(false);
            stage.showAndWait();
        } catch (Exception e) {
        }
    }

    /**
     * open the help window
     * @param actionEvent
     */
    public void Help(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();
            stage.setTitle("Help");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("Help.fxml"));
            Scene scene = new Scene(root, 1050, 600);
            scene.getStylesheets().add(getClass().getResource("Help.css").toExternalForm());

            stage.setMinWidth(1050);
            stage.setMaxWidth(1450);
            stage.setMinHeight(600);
            stage.setMaxHeight(850);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.setResizable(false);
            stage.showAndWait();
            //stage.show();
        } catch (Exception e) {
        }
    }

    /**
     * close the window safely
     */
    public void exit(){
        CloseProgram();
    }

    /**
     * load saved maze
     */
    public void LoadMaze(){
        isLoad = true;
        FileChooser fileChooser1 = new FileChooser();
        fileChooser1.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser1.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("*.maze", "*.maze"));
        fileChooser1.setInitialFileName("");
        File loadFile = fileChooser1.showOpenDialog(stage);

        if(loadFile!=null && loadFile.canRead()){

            win=false;
            btn_SolveMaze.setDisable(false);
            b_tnHint.setDisable(false);
            isGenerate=true;
            numOfHint=0;
            isSolve=false;

            viewModel.Load(loadFile.getPath());
            /****update mazeDisplayer****/
            mazeDisplayer.setMaze(viewModel.getMaze());
            mazeDisplayer.setCharacterPosition(viewModel.getCharacterPositionRow(),viewModel.getCharacterPositionColumn());
            isGenerate=true;
        }
    }


    /** After Click on save button **/
    public void saveMaze() {

        if(!btn_generateMaze.isDisable() && viewModel.getMaze()!=null) {
            FileChooser fileChooser1 = new FileChooser();
            fileChooser1.setInitialDirectory(new File(System.getProperty("user.home")));
            fileChooser1.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("*.maze", "*.maze"));
            fileChooser1.setInitialFileName("savedMaze_");
            File file = fileChooser1.showSaveDialog(stage);
            String charPath = mazeDisplayer.getCharacterPath();
            if(file!=null ){//&& file.canWrite()) {
                viewModel.Save(file,charPath);
            }
            else{
                this.showAlert("Something wrong with save option");
            }
        }
        else{
            this.showAlert("Please generate your maze before");
        }
    }
    //endregion


    //region *KEY PRESSED EVENTS*

    /**
     * when user press enter in row and column open the character choice window
     * @param keyEvent
     */
    public void keyPressInRowsAndCall(KeyEvent keyEvent) {
        if (keyEvent.getCode()== KeyCode.ENTER){
            ChooseChar();
        }
    }

    /**
     * Called on key pressed and operate the key Event
     * @param keyEvent
     */
    public void KeyPressed(KeyEvent keyEvent) {
        if(viewModel.getMaze()!=null&&!win) {
            if(!isSolve) {
                b_tnHint.setDisable(false);
                btn_SolveMaze.setDisable(false);
            }
            viewModel.changeCellTo0(viewModel.getCharacterPositionRow(), viewModel.getCharacterPositionColumn());
            viewModel.moveCharacter(keyEvent.getCode());
            keyEvent.consume();
        }
    }
    //endregion


    //region *MUSIC*
    /**
     * start music
     */
    private void startMusic(){
        Image imageOn = new Image( getClass().getResource("/Images/On.jpg").toExternalForm());
        ImageView on = new ImageView(imageOn);
        on.setFitHeight(40);
        on.setFitWidth(40);
        btn_sound.setGraphic(on);
        mediaPlayerSound.play();
    }


    /**
     * stop music
     */
    private void stopMusic(){
        Image imageMute = new Image(getClass().getResource("/Images/mute.jpg").toExternalForm());
        ImageView mute = new ImageView(imageMute);
        mute.setFitHeight(40);
        mute.setFitWidth(40);
        btn_sound.setGraphic(mute);
        mediaPlayerSound.stop();

    }
    //endregion
}
