package ViewModel;
import Model.IModel;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.input.KeyCode;

import java.io.File;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Eden Mizrahi and Yarden Schwartz
 * MyViewModel class connects between MyViewController class and MyModel class
 */
public class MyViewModel extends Observable implements Observer {
    private IModel model;
    private int characterPositionRowIndex;
    private int characterPositionColumnIndex;

    public StringProperty characterPositionRow = new SimpleStringProperty("1"); //For Binding
    public StringProperty characterPositionColumn = new SimpleStringProperty("1"); //For Binding

    /**
     * Constructor
     * @param model
     */
    public MyViewModel(IModel model){
        this.model = model;
    }

    /**
     * This function ask for answer from MyModel class
     * @return the arrayList of the solution that the model return
     */
    public ArrayList<Position> getPath(){
        return model.getPath();
    }
    /**
     * This function ask for answer from MyModel class in order to know if this maze is new or loaded
     * @return true if this is a loaded maze or false else.
     */
    public boolean getLoadProblem(){
        return model.isLoadProblem();
    }

    /**
     * This function ask for answer from MyModel class in order to get the character path
     * @return path of character image
     */
    public String getCharacterPath(){
        return model.getUpdatedCharacterPath();
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o==model){
            characterPositionRowIndex = model.getCharacterPositionRow();
            characterPositionRow.set(characterPositionRowIndex + "");
            characterPositionColumnIndex = model.getCharacterPositionColumn();
            characterPositionColumn.set(characterPositionColumnIndex + "");
            setChanged();
            notifyObservers();
        }
    }

    /**
     * This function call MyModel in order to actually generate new maze
     * @param width of new maze
     * @param height of new maze
     */
    public void generateMaze(int width, int height){
        model.generateMaze(width, height);
    }

    /**
     * This function call MyModel in order to actually doing the move step
     * @param movement
     */
    public void moveCharacter(KeyCode movement){
        model.moveCharacter(movement);
    }

    /**
     * This function call MyModel in order to get the Maze
     * @return maze variable
     */
    public Maze getMaze() {
        return model.getMaze();
    }

    /**
     * This function call MyModel in order to get character position row
     * @return character position row
     */
    public int getCharacterPositionRow() {
        return model.getCharacterPositionRow();
    }

    /**
     * This function call MyModel in order to get character position column
     * @return character position column
     */
    public int getCharacterPositionColumn() {
        return model.getCharacterPositionColumn();
    }

    /**
     *  This function call MyModel in order to handle with solve actions
     */
    public void Solve (){
        model.Solve();
    }

    /**
     * This function call MyModel in order to get the goal position of the maze
     * @return goal position
     */
    public Position getGoal(){
        return model.getGoal();
    }

    /**
     * This function call MyModel in order to change the specific cell to 0
     * @param characterPositionRow
     * @param characterPositionColumn
     */
    public void changeCellTo0(int characterPositionRow,int characterPositionColumn){
        model.changeCellTo0(characterPositionRow,characterPositionColumn);
    }

    /**
     * This function call MyModel in order to handle with all the load actions
     * @param loadFile is the path of the file we are going to read from
     */
    public void Load(String loadFile){
        model.Load(loadFile);
    }

    /**
     * This function call MyModel in order to handle with all the save actions
     * @param f is file to save in
     * @param path is the character path in order to save this too
     */
    public void Save(File f, String path) {
        model.Save(f,path);
    }

    /**
     * This function return true / false if we press on home button on the keyboard
     * @return true if we press on home button or false, else.
     */
    public boolean isHome() {
        return model.isHome();
    }

    /**
     * This function updated if we pressed on home button on the keyboard or not
     * @param home
     */
    public void setHome(boolean home) {
        model.setHome(home);
    }

    /**
     * This function call to MyModel in order to give a hint
     */
    public void hint() {
        model.hint();
    }

    /**
     * This function call to MyModel in order to close the servers
     */
    public void stopServers() {
        model.stopServers();
    }
}
