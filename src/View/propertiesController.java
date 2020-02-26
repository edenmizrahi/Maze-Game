package View;

import Server.Server;
import javafx.beans.property.IntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BackgroundImage;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Eden Mizrahi and Yarden Schwartz
 * This class control all the actions that happened in the properties window
 */
public class propertiesController implements Initializable {
    @FXML
    public javafx.scene.control.Button btn_Ok;

    public ChoiceBox<String>chGenerator;
    public ChoiceBox<String>chAlgorithm;

    MyViewController view;

    /*Init Algorithm list*/
    ObservableList<String> AlgoChoicesList = FXCollections.
            observableArrayList("BestFirstSearch","BreadthFirstSearch","DepthFirstSearch");

    /*Init Generator list*/
    ObservableList<String> GeneratorChoicesList = FXCollections.
            observableArrayList("MyMazeGenerator","SimpleMazeGenerator","EmptyMazeGenerator");

    /**
     * Called to initialize a controller after its root element has been
     * completely processed.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  <tt>null</tt> if the location is not known.
     * @param resources The resources used to localize the root object, or <tt>null</tt> if
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //initialize the items (Algorithm's and Generate's options) at the choiceBoxes
        chGenerator.setItems(GeneratorChoicesList);
        chAlgorithm.setItems(AlgoChoicesList);
    }

    /**
     * Initialize the main controller we came from
     * @param v is the controller that we receive from MyViewController class
     */
    public void initializeSecond(MyViewController v){
        view = v;
    }

    /**
     * This function save the changes at the properties window, after click on 'ok' button
     */
    public void ClickOk (){
        //save the value the client choose for algorithm
        String AlgoChoice = chAlgorithm.getValue();
        //save the value the client choose for generateS
        String GeneratorChoice = chGenerator.getValue();

        //it the client didnt fill the both boxes, we ask him to fill them before he click 'ok'
        if(AlgoChoice == null || GeneratorChoice == null){
           view.showAlert("Please fill all the boxes first");
        }
        //we update our configuration file with client's chooses
        else {
            Server.Configurations.SetGeneratorAndAlgo(AlgoChoice, GeneratorChoice);
            closeW();
        }
    }

    /**
     * This function close the properties window after updating the changes
     */
    private void closeW() {
        Stage s = (Stage) btn_Ok.getScene().getWindow();
        s.close();
    }


}
