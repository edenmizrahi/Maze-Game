package View;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.RadioButton;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Eden Mizrahi and Yarden Schwartz
 * This class control all the actions that happened in the character choice window
 */
public class characterController {
    @FXML
    public javafx.scene.control.Button o_kBut;
    public RadioButton jon;
    public RadioButton Arya;
    public RadioButton tyrion;
    public RadioButton Daenery;
    public AnchorPane ancor;
    private BackgroundImage background;
    private IntegerProperty height;
    private IntegerProperty width;


    MyViewController view;

    /**
     * Initialize the main controller we came from
     *
     * @param v is the controller that we receive from MyViewController class
     */
    public void initViewController(MyViewController v) {
        view = v;
    }

    /**
     * this function represent what happened when we click ok - at the choose character window
     * we need to update the path of the selected character
     *
     * @param actionEvent this is the event that brings us to this function
     */
    public void OkClick(ActionEvent actionEvent) {
        if (this.jon.isSelected()) {
            view.characterChoice("resources/Images/jonS1.jpg");
            closeW();
        } else {
            if (this.Arya.isSelected()) {
                view.characterChoice("resources/Images/aryaS1.png");
                closeW();
            } else {
                if (this.tyrion.isSelected()) {
                    view.characterChoice("resources/Images/tyrionT1.png");
                    closeW();
                } else {
                    if (this.Daenery.isSelected()) {
                        view.characterChoice("resources/Images/halisi6.png");
                        closeW();
                    }
                    //if someone press ok without choosing any character, alert will appear
                    else {
                        view.showAlert("Choose Character First");
                    }
                }
            }
        }

    }

    /**
     * This function close the character choosing window, after pressing '0k'
     */
    private void closeW() {
        Stage s = (Stage) o_kBut.getScene().getWindow();
        s.close();
    }
}
