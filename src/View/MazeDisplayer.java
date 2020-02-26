package View;

import algorithms.mazeGenerators.Maze;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by Eden Mizrahi and Yarden Schwartz
 * This class extends from Canvas class and represent maze situation
 */
public class MazeDisplayer extends Canvas {
    private double zoomSize =1;
    private Maze maze;
    private int characterPositionRow = 1;
    private int characterPositionColumn = 1;
    double xCharacterPixel;
    double yCharacterPixel;
    double cellH;
    double cellW;
    public String characterPath;

    public StringProperty imageCharacter =new SimpleStringProperty();

    private StringProperty ImageFileNameWall = new SimpleStringProperty();

    private StringProperty ImageFileNameCharacter = new SimpleStringProperty();

    /**
     * This function
     * @return character picture path
     */
    public String getCharacterPath() {
        return characterPath;
    }

    /**
     * This function get character picture path and updates the current variable with this path
     * @param path to update
     */
    public void setCharacterPath(String path){
        this.characterPath = path;
    }

    /**
     * This function get maze and update the current variable with the new maze
     * @param maze to update
     */
    public void setMaze(Maze maze) {
        this.maze = maze;
        //after update maze we need to draw him
        redraw();
    }

    /**
     * This function get row and col of the character's new position
     * @param row to update
     * @param column to update
     */
    public void setCharacterPosition(int row, int column) {
        characterPositionRow = row;
        characterPositionColumn = column;
    }

    /**
     * This function
     * @return position of row where the character is
     */
    public int getCharacterPositionRow() {
        return characterPositionRow;
    }

    /**
     * This function
     * @return position of column where the character is
     */
    public int getCharacterPositionColumn() {
        return characterPositionColumn;
    }

    /**
     * This function responsible to draw the maze
     */
    public void redraw() {
        //maze.print();
        System.out.println();
        if (maze != null) {
            double canvasHeight = getHeight()* zoomSize;
            double canvasWidth = getWidth()* zoomSize;
            double cellHeight = canvasHeight / (maze.rowLimit() + 1);
            double cellWidth = canvasWidth / (maze.colLimit() + 1);
            cellH=cellHeight;
            cellW=cellWidth;

            try {
                /*initializes images*/
                Image wallImage = new Image(new FileInputStream("resources/Images/tile2.jpg"));
                Image solImage = new Image(new FileInputStream("resources/Images/fire.jpg"));
                Image endImage = new Image(new FileInputStream("resources/Images/end.jpg"));
                Image pathImage = new Image(new FileInputStream("resources/Images/path.jpg"));

                GraphicsContext gc = getGraphicsContext2D();
                gc.clearRect(0, 0, getWidth(), getHeight());

                for (int i = 0; i < maze.rowLimit() + 1; i++) {
                    for (int j = 0; j < maze.colLimit() + 1; j++) {
                        //if this cell is part of the solution
                        if (maze.getArray()[i][j] == 2) {
                            gc.drawImage(solImage, j * cellWidth, i * cellHeight, cellWidth, cellHeight);
                        }
                        //if the cell is wall
                        if (maze.getArray()[i][j] == 1) {
                            //gc.fillRect(i * cellHeight, j * cellWidth, cellHeight, cellWidth);
                            gc.drawImage(wallImage, j * cellWidth, i * cellHeight, cellWidth, cellHeight);
                        }
                        //if the cell is part of the way
                        if(maze.getArray()[i][j] == 0) {
                            gc.drawImage(pathImage, j * cellWidth, i * cellHeight, cellWidth, cellHeight);
                        }
                    }
                }
                /*draw end image*/
                gc.drawImage(endImage, maze.getGoalPosition().getColumnIndex() * cellWidth, maze.getGoalPosition().getRowIndex() * cellHeight, cellWidth, cellHeight);
                /*initialize character image according to current character*/
                Image characterImage = new Image(new FileInputStream(characterPath));
                /*draw character image*/
                gc.drawImage(characterImage,  characterPositionColumn * cellWidth, characterPositionRow * cellHeight,cellWidth, cellHeight);

                xCharacterPixel = characterPositionColumn * cellWidth;
                yCharacterPixel = characterPositionRow * cellHeight;

            }
            catch(FileNotFoundException e){
                //e.printStackTrace();
            }
        }
    }

    /**
     * This function
     * @return path of wall image
     */
    public String getImageFileNameWall() {
        return ImageFileNameWall.get();
    }

    /**
     * This function updated path of wall
     * @param imageFileNameWall wall path to update
     */
    public void setImageFileNameWall(String imageFileNameWall) {
        this.ImageFileNameWall.set(imageFileNameWall);
    }

    /**
     * This function
     * @return path of character
     */
    public String getImageFileNameCharacter() {
        return ImageFileNameCharacter.get();
    }

    /**
     * This function updated path of character
     * @param imageFileNameCharacter character path to update
     */
    public void setImageFileNameCharacter(String imageFileNameCharacter) {
        this.ImageFileNameCharacter.set(imageFileNameCharacter);
    }

    /**
     * This function
     * @return maze variable
     */
    public Maze getMaze(){
        return maze;
    }

    /**
     * This function updated the num of zoom needed
     * @param zoomSize to update
     */
    public void setZoomSize(double zoomSize){
        this.zoomSize = zoomSize;
    }

    /**
     * This function
     * @return zoom size
     */
    public double getZoomSize(){return zoomSize;}

    /**
     * This function
     * @return character position of row in pixel
     */
    public double getxCharacterPixel(){return xCharacterPixel;}

    /**
     * This function
     * @return character position of column in pixel
     */
    public double getyCharacterPixel(){return yCharacterPixel;}

    /**
     * This function
     * @return cell height
     */
    public double getH(){return cellH;}

    /**
     * This function
     * @return cell width
     */
    public double getW(){return cellW;}
}
