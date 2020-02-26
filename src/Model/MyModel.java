package Model;

import IO.MyCompressorOutputStream;
import IO.MyDecompressorInputStream;
import Server.IServerStrategy;
import Server.ServerStrategyGenerateMaze;
import Server.ServerStrategySolveSearchProblem;
import algorithms.mazeGenerators.Maze;
import Server.*;
import Client.*;
import Client.Client;
import IO.MyDecompressorInputStream;
import Server.Server;
import Server.ServerStrategyGenerateMaze;
import Server.ServerStrategySolveSearchProblem;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.MyMazeGenerator;
import algorithms.mazeGenerators.Position;
import algorithms.search.AState;
import algorithms.search.Solution;
import javafx.scene.input.KeyCode;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by Eden Mizrahi and Yarden Schwartz
 * MyViewController class operate all the internal functionality of the application
 */
public class MyModel extends Observable implements IModel {


    //region *FIELDS*
    private  ArrayList<Position> path = new ArrayList<Position>();
    private ExecutorService threadPool = Executors.newCachedThreadPool();
    private Maze maze;
    Server mazeGeneratingServer;
    Server solveSearchProblemServer;
    private boolean home = false;
    public String updatedCharacterPath;
    public boolean loadProblem = false;
    public boolean isSolve=false;
    private int characterStartPositionRow=1;
    private int characterStartPositionCol=1;
    private int characterPositionRow = 1;
    private int characterPositionColumn = 1;
    private int goalPositionColumn=1;
    private int goalPositionRow=1;
    //endregion


    /**
     * getter to updatedCharacterPath
     * @return updatedCharacterPath value
     */
    public String getUpdatedCharacterPath() {
        return updatedCharacterPath;
    }

    /**
     * set setUpdatedCharacterPath
     * @param updatedCharacterPath
     */
    public void setUpdatedCharacterPath(String updatedCharacterPath) {
        this.updatedCharacterPath = updatedCharacterPath;
    }

    /**
     * getter to isHome
     * @return
     */
    public boolean isHome() {
        return home;
    }


    /**
     * setter to iHome
     * @param home
     */
    public void setHome(boolean home) {
        this.home = home;
    }


    /**
     * start the servers
     */
    public void startServers() {
        mazeGeneratingServer = new Server(5400, 1000, new ServerStrategyGenerateMaze());
        solveSearchProblemServer = new Server(5401, 1000, new ServerStrategySolveSearchProblem());
        solveSearchProblemServer.start();
        mazeGeneratingServer.start();
    }

    /**
     * stop the servers
     */
    public  void stopServers() {
        solveSearchProblemServer.stop();
        mazeGeneratingServer.stop();
    }

    /**
     * getter to isLoadProblem
     * @return if was a load problem
     */
    public boolean isLoadProblem() {
        return loadProblem;
    }

    /**
     * getter to the path
     * @return the solution path
     */
    public ArrayList<Position> getPath(){
        return path;
    }

    /**
     * operate the generate maze with conecting to server
     * @param row
     * @param column
     */
    public void generateMaze(int row, int column) {

        try {
            Client client = new Client(InetAddress.getLocalHost(), 5400, new IClientStrategy() {
                @Override
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        int[] mazeDimensions = new int[]{row, column};
                        toServer.writeObject(mazeDimensions); //send maze dimensions to server
                        toServer.flush();
                        byte[] compressedMaze = (byte[]) fromServer.readObject(); //read generated maze (compressed with MyCompressor) from server
                        InputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                        byte[] decompressedMaze = new byte[(row * column) + 12]; //allocating byte[] for the decompressed maze -
                        is.read(decompressedMaze); //Fill decompressedMaze with bytes
                        maze = new Maze(decompressedMaze);
                        characterStartPositionRow=maze.getStartPosition().getRowIndex();
                        characterStartPositionCol=maze.getStartPosition().getColumnIndex();
                        characterPositionColumn=maze.getStartPosition().getColumnIndex();
                        characterPositionRow=maze.getStartPosition().getRowIndex();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        setChanged();
        notifyObservers();
    }


    /**
     * setter to maze
     * @param maze - the maze
     */
    public void setMaze(Maze maze) {
        this.maze = maze;
    }

    /**
     * operate in loading saved maze
     * @param path- file path
     */
    public void Load(String path){
        try {
            // load maze from a file
            File file = new File(path);
            if(file.exists()) {
                /**check if extension is maze**/
                FileInputStream newFile = new FileInputStream(file);
                ObjectInputStream toRead = new ObjectInputStream(newFile);
                /**read maze**/
                byte[] savedMazeBytes = (byte[]) toRead.readObject();
                Maze newMaze = new Maze(savedMazeBytes);
                setMaze(newMaze);
                /**read position**/
                Position characterPos = (Position) toRead.readObject();
                maze.setStart(characterPos);
                this.characterPositionColumn = maze.getStartPosition().getColumnIndex();
                this.characterPositionRow = maze.getStartPosition().getRowIndex();
                /**read character path**/
                String characterPath = (String) toRead.readObject();
                this.setUpdatedCharacterPath(characterPath);

                newFile.close();
                toRead.close();
            }
            else{
                loadProblem = true;
            }

        } catch (IOException e) {
            loadProblem = true;
        } catch (ClassNotFoundException e) {
            loadProblem = true;
        }

        setChanged();
        notifyObservers();
    }

    /**
     * operating in saving current maze
     * @param f
     * @param path
     */
    public void Save(File f,String path) {
        try {
            f.createNewFile();
            FileOutputStream newFile = new FileOutputStream(f);
            ObjectOutputStream toWrite = new ObjectOutputStream(newFile);
            /****write to file the maze byte[]****/
            byte[] mazeArray = maze.toByteArray();
            toWrite.writeObject(mazeArray);
            /****write to file position****/
            Position characterPos = new Position(this.getCharacterPositionRow(),this.getCharacterPositionColumn());
            toWrite.writeObject(characterPos);
            /****write to file character path****/
            toWrite.writeObject(path);

            newFile.close();
            toWrite.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * connecting to the solution server
     */
    private void connectSolServer(){

        try {
            Client client = new Client(InetAddress.getLocalHost(), 5401, new IClientStrategy() {
                @Override
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        maze.setStart(new Position(characterPositionRow,characterPositionColumn));
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        toServer.writeObject(maze); //send maze to server
                        toServer.flush();
                        //Solution solution = (Solution) fromServer.readObject(); //read generated maze (compressed with MyCompressor) from server
                        Solution mazeSolution = (Solution) fromServer.readObject();
                        ArrayList<AState> mazeSolutionSteps = mazeSolution.getSolutionPath();
                        path = new ArrayList<Position>();
                        for (int i = 0; i < mazeSolutionSteps.size() - 1; ++i) {
                            AState astate = mazeSolutionSteps.get(i);
                            String s = astate.getStr_state();
                            String[] words = s.split(",");
                            int row = Integer.parseInt(words[0].substring(1));
                            int col = Integer.parseInt(words[1].substring(0, (words[1].length() - 1)));
                            path.add(new Position(row, col));
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }

    /**
     * solve maze problem
     */
    public void Solve(){
        cleanMazeFromSol();
        connectSolServer();
        int[][]solArray =maze.getArray();
        for(int i=0 ;i<solArray.length; i++){
            for(int j=0; j<solArray[0].length;j++){
                Position cur=new Position(i,j);
                if(path.contains(cur)){
                    solArray[i][j]=2;
                }
            }
        }
        Position start = maze.getStartPosition();
        Position end = maze.getGoalPosition();

        maze=new Maze(solArray);
        maze.setStart(start);
        maze.setGoal(end);

        isSolve=true;
        setChanged();
        notifyObservers();
    }


    /**
     * getter to maze
     * @return maze
     */
    public Maze getMaze() {
        return maze;
    }

    /**
     * operating in move character
     * @param movement
     */
    public void moveCharacter(KeyCode movement) {
        if (movement == KeyCode.UP||movement==KeyCode.NUMPAD8||movement==KeyCode.W) {
            // int col = tempMaze.colLimit();
            int newRow = characterPositionRow - 1;

            if(newRow >= 0 &&
                    maze.getValue(newRow,characterPositionColumn)!=1){
                characterPositionRow = newRow;
                //characterColumnNewPosition = characterColumnCurrentPosition;
            }
        }
        else if (movement == KeyCode.DOWN||movement==KeyCode.NUMPAD2||movement==KeyCode.X) {
            int newRow = characterPositionRow+1;
            if(newRow <= maze.rowLimit() &&
                    maze.getValue(characterPositionRow+1, characterPositionColumn) !=1) {
                characterPositionRow = characterPositionRow + 1;
                characterPositionColumn = characterPositionColumn;
            }
        }
        else if (movement == KeyCode.RIGHT||movement==KeyCode.NUMPAD6||movement==KeyCode.D) {
            // int col = mazeDisplayer.getMazeCols();
            int col = maze.colLimit();
            int newCol = characterPositionColumn + 1;

            if(newCol <= col &&
                    maze.getValue(characterPositionRow,characterPositionColumn + 1)!=1) {
                characterPositionRow = characterPositionRow;
                characterPositionColumn = characterPositionColumn + 1;
            }
        }
        else if (movement == KeyCode.LEFT||movement==KeyCode.NUMPAD4||movement == KeyCode.A ) {
            int col = characterPositionColumn - 1;
            if(col >= 0 &&
                    maze.getValue(characterPositionRow, characterPositionColumn-1) !=1) {
                // characterRowNewPosition = characterRowCurrentPosition;
                characterPositionColumn = characterPositionColumn - 1;
            }
        }

        if (movement == KeyCode.E||movement==KeyCode.NUMPAD9 ) {
            int col = characterPositionColumn + 1;
            int row= characterPositionRow -1;
            if(col <= maze.colLimit() && row>=0&&
                    maze.getValue(row, col) !=1) {
                // characterRowNewPosition = characterRowCurrentPosition;
                characterPositionColumn = col;
                characterPositionRow=row;
            }
        }

        if (movement == KeyCode.Q||movement==KeyCode.NUMPAD7 ) {
            int col = characterPositionColumn -1;
            int row= characterPositionRow -1;
            if(col >=0 && row>=0&&
                    maze.getValue(row, col) !=1) {
                characterPositionRow = row;
                characterPositionColumn = col;
            }
        }

        if (movement == KeyCode.Z||movement==KeyCode.NUMPAD1 ) {
            int col = characterPositionColumn - 1;
            int row= characterPositionRow +1;
            if(col >=0 && row<=maze.rowLimit()&&
                    maze.getValue(row, col) !=1) {
                characterPositionRow = row;
                characterPositionColumn = col;
            }
        }
        if (movement == KeyCode.C||movement==KeyCode.NUMPAD3 ) {
            int col = characterPositionColumn + 1;
            int row= characterPositionRow +1;
            if(col <=maze.colLimit() && row<=maze.rowLimit()&&
                    maze.getValue(row, col) !=1) {
                characterPositionRow = row;
                characterPositionColumn = col;
            }
        }

        else if (movement == KeyCode.HOME){
            characterPositionColumn = characterStartPositionCol;
            characterPositionRow = characterStartPositionRow;

            int[][]mazeWithoutSol =maze.getArray();

            for(int i=0 ;i<mazeWithoutSol.length; i++){
                for(int j=0; j<mazeWithoutSol[0].length;j++){
                    if(maze.getValue(i,j)==2&&(!(j==goalPositionColumn&&i==goalPositionRow)))
                        mazeWithoutSol[i][j]=0;
                }
            }
            Position start = maze.getStartPosition();
            Position end = maze.getGoalPosition();
            maze=new Maze(mazeWithoutSol);
            maze.setStart(start);
            maze.setGoal(end);
            home=true;
        }



        setChanged();
        notifyObservers();
    }

    /**
     * getters to the maze goal
     * @return
     */
    public Position getGoal(){
        if(maze != null && maze.getGoalPosition() != null)
            return maze.getGoalPosition();
        return null;
    }

    /**
     * getter to the CharacterPositionRow
     * @return CharacterPositionRow
     */
    public int getCharacterPositionRow() {
        return characterPositionRow;
    }

    /**
     * getter to characterPositionColumn
     * @return characterPositionColumn
     */
    public int getCharacterPositionColumn() {
        return characterPositionColumn;
    }

    /**
     * change cell in maze to path cell
     * @param characterPositionRow
     * @param characterPositionColumn
     */
    public void changeCellTo0(int characterPositionRow,int characterPositionColumn){
        if (maze != null) {
            maze.changeCellTo0(characterPositionRow, characterPositionColumn);
            setChanged();
            notifyObservers();
        }
    }

    /**
     * check if the position is legal
     * @param row
     * @param column
     * @return
     */
    private boolean isLegalPosition(int row, int column) {
        boolean isLegal = false;
        if (row >= 0 && column >= 0 && row <= maze.rowLimit() && column <= maze.colLimit()) {
            if (maze.getValue(row, column) == 0) {
                isLegal = true;
            }
        }
        return isLegal;
    }

    /**
     * operate in hint
     */
    public void hint(){
        cleanMazeFromSol();
        connectSolServer();
        int[][]hintArray =maze.getArray();
        hintArray[path.get(1).getRowIndex()][path.get(1).getColumnIndex()]=2;

        Position start = maze.getStartPosition();
        Position end = maze.getGoalPosition();

        maze=new Maze(hintArray);
        maze.setStart(start);
        maze.setGoal(end);
        setChanged();
        notifyObservers();


    }

    /**
     * clean maze from solution path
     */
    private void cleanMazeFromSol(){
        int [][] mazeArray=maze.getArray();
        for(int i=0; i<mazeArray.length;i++){
            for(int j=0;j<mazeArray[0].length;j++){
                if(mazeArray[i][j]==2){
                    maze.changeCellTo0(i,j);
                }
            }
        }
        path=new ArrayList<Position>();

    }



}
