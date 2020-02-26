package Model;

import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import javafx.scene.input.KeyCode;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Eden Mizrahi and Yarden Schwartz
 * operate all the internal functionality of the application
 */
public interface IModel {
    /**
     * operate the generate maze with conecting to server
     * @param width
     * @param height
     */
    void generateMaze(int width, int height);
    /**
     * operating in move character
     * @param movement
     */
    void moveCharacter(KeyCode movement);
    /**
     * solve maze problem
     */
    void Solve();
    /**
     * getter to maze
     * @return maze
     */
    Maze getMaze();

    /**
     * getter to the CharacterPositionRow
     * @return CharacterPositionRow
     */
    int getCharacterPositionRow();

    /**
     * getter to the CharacterPositionColumn
     * @return
     */
    int getCharacterPositionColumn();

    /**
     * get the solution path
     * @return solution path
     */
    ArrayList<Position> getPath();

    /**
     * getter to the goal position
     * @return goal position
     */
    Position getGoal();

    /**
     * stop servers
     */
    public  void stopServers();

    /**
     * change cell in maze to path cell
     * @param characterPositionRow
     * @param characterPositionColumn
     */
    void changeCellTo0(int characterPositionRow,int characterPositionColumn);
    /**
     * getter to isLoadProblem
     * @return if was a load problem
     */
    boolean isLoadProblem();
    /**
     * getter to updatedCharacterPath
     * @return updatedCharacterPath value
     */
    String getUpdatedCharacterPath();

    /**
     * load saved game
     * @param loadFile
     */
    void Load(String loadFile);

    /**
     * save current game
     * @param f
     * @param path
     */
    void Save(File f, String path);

    /**
     * getter to is home
     * @return if user click on home button
     */
    boolean isHome();

    /**
     * setter to home
     * @param home
     */
    void setHome(boolean home);

    /**
     * operate hint
     */
    void hint();
}

