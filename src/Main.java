import Model.*;
import View.MyViewController;
import ViewModel.MyViewModel;
import algorithms.mazeGenerators.MyMazeGenerator;
import javafx.application.Application;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.awt.*;
import java.util.Optional;

public class Main extends Application {

    static String first;
    @Override
    public void start(Stage primaryStage) throws Exception {

        Scene scene,scene1;
        MyModel model = new MyModel();
        model.startServers();
        MyViewModel viewModel = new MyViewModel(model);
        model.addObserver(viewModel);
        //--------------
        primaryStage.setTitle("GOT Maze!");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(150, 100, 100, 100));
        grid.setVgap(5);
        grid.setHgap(5);

        IntegerProperty width = new SimpleIntegerProperty(650);
        IntegerProperty height = new SimpleIntegerProperty(650);
        width.bind(grid.widthProperty());
        height.bind(grid.heightProperty());
        BackgroundImage background = new BackgroundImage(new javafx.scene.image.Image("/Images/backroundStart.jpg",
                width.intValue(), height.intValue(),false,true),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
        grid.setBackground(new Background(background));
        primaryStage.setMinHeight(635);
        primaryStage.setMinWidth(580);
        primaryStage.setMaxHeight(635);
        primaryStage.setMaxWidth(580);
        // Defining the Name text field
        final TextField firstName = new TextField();
        firstName.setPromptText("Enter your name.");



        firstName.setPrefColumnCount(10);

        GridPane.setConstraints(firstName, 50, 53);
        grid.getChildren().add(firstName);
        // Defining the ENTER button
        Button enter = new Button("ENTER");
        GridPane.setConstraints(enter, 50, 55);
        grid.getChildren().add(enter);
        scene1 = new Scene(grid, 300, 300);

        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root = fxmlLoader.load(getClass().getResource("View/MyView.fxml").openStream());
        scene = new Scene(root, 800, 700);


        scene.getStylesheets().add(getClass().getResource("View/View.css").toExternalForm());
        primaryStage.setScene(scene1);
        //--------------
        MyViewController view = fxmlLoader.getController();
        enter.setOnAction(e -> {
            view.setUserName( firstName.getText());
            primaryStage.setScene(scene);
            primaryStage.setMinHeight(670);
            primaryStage.setMinWidth(750);
            primaryStage.setMaxHeight(10000);
            primaryStage.setMaxWidth(10000);
        });
        firstName.setOnAction(e -> {
            view.setUserName( firstName.getText());
            primaryStage.setScene(scene);
            primaryStage.setMinHeight(670);
            primaryStage.setMinWidth(750);
            primaryStage.setMaxHeight(10000);
            primaryStage.setMaxWidth(10000);
        });


        view.setResizeEvent(scene);
        view.setViewModel(viewModel);
        view.setStage(primaryStage);
        viewModel.addObserver(view);
        //-----------
        SetStageCloseEvent(primaryStage,view);



        primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> {
            view.backgroundSize();
        });
        primaryStage.heightProperty().addListener((obs, oldVal, newVal) -> {
            view.backgroundSize();
        });

        scene.widthProperty().addListener((obs, oldVal, newVal) -> {
            view.backgroundSize();
        });


        scene.heightProperty().addListener((obs, oldVal, newVal) -> {
            view.backgroundSize();
        });
        //  SetStageCloseEvent(primaryStage,model);
        primaryStage.getIcons().add(new Image("/Images/ICON4.jpg"));

        primaryStage.show();



    }

    public void SetStageCloseEvent(Stage primaryStage, MyViewController myViewController) {
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent windowEvent) {
                if(myViewController.CloseProgram()==false)
                {
                    // ... user chose CANCEL or closed the dialog
                    windowEvent.consume();
                }
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
