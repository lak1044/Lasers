package backtracking;

import model.LasersModel;
import model.Laser;
import model.Pillar;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Scanner;

/**
 * The class represents a single configuration of a safe.  It is
 * used by the backtracker to generate successors, check for
 * validity, and eventually find the goal.
 *
 * This class is given to you here, but it will undoubtedly need to
 * communicate with the model.  You are free to move it into the model
 * package and/or incorporate it into another class.
 *
 * @author Sean Strout @ RIT CS
 * @author YOUR NAME HERE
 */
public class SafeConfig implements Configuration {

    //Empty cell
    public static final char EMPTY = '.';
    //Laser
    public static final char LASER = 'L';
    //Beam
    public static final char BEAM = '*';
    //Pillar w/any num of lasers
    public static final char ANYPILLAR = 'X';
    //Horizontal dim
    private static int rows;
    //Vertical dim
    private static int cols;
    //Grid
    private char[][] lGrid;
    //Hash map of lasers. The key is a string made of the coordinates of said laser and value is laser object
    //Key is (hash(row, col))
    private HashMap<String, Laser> laserHash;
    //Hash map of pillar locations. Key is location and value is num of required lasers
    //Key is same as laser hash map
    private static HashMap<String, Pillar> pillarHash;
    //position of row
    private int lastRow;
    //position of column
    private int lastCol;

    public SafeConfig(String filename) throws FileNotFoundException {

        Scanner in = new Scanner(new File(filename));
        rows = Integer.parseInt(in.next());
        cols = Integer.parseInt(in.next());
        laserHash = new HashMap<>();
        pillarHash = new HashMap<>();
        //Filling the grid
        lGrid = new char[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                lGrid[i][j] = in.next().charAt(0);
                if (Character.isDigit(lGrid[i][j])) {
                    Pillar newPillar = new Pillar(i, j, Character.getNumericValue(lGrid[i][j]));
                    pillarHash.put(hash(i, j), newPillar);
                }
            }
        }
        in.close();
    }


    /**
     * Copy constructor.  Takes a config, other, and makes a full "deep" copy
     * of its instance data.
     * @param other the config to copy
     */
    public SafeConfig(SafeConfig other){
        lGrid = new char[rows][cols];
        laserHash = new HashMap<>();
        for (String s: other.laserHash.keySet()){
            laserHash.put(s, other.laserHash.get(s));
        }
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                lGrid[i][j] = other.lGrid[i][j];
            }
        }
        lastRow = other.lastRow;
        lastCol = other.lastCol;
    }

    /**
     * Makes hash key from the row and column placement in the model
     */
    public String hash(int row, int col){
        return (Integer.toString(row) + Integer.toString(col));
    }

    @Override
    public Collection<Configuration> getSuccessors() {
        Collection<Configuration> successors = new ArrayList<>();
        lastCol = (lastCol + 1) % cols;
        if (lastCol == 0){
            lastRow = (lastRow + 1) % rows;
        }
        while (lGrid[lastRow][lastCol] != EMPTY){
            lastCol = (lastCol + 1) % cols;
            if (lastCol == 0){
                lastRow = (lastRow + 1) % rows;
            }
        }
        SafeConfig laserSafe = new SafeConfig(this);
        laserSafe.lGrid[laserSafe.lastRow][laserSafe.lastCol] = LASER;
        laserSafe.laserHash.put(hash(laserSafe.lastRow, laserSafe.lastCol), new Laser(laserSafe.lastRow, laserSafe.lastCol));
        AddBeams(laserSafe.lastRow, laserSafe.lastCol, laserSafe);
        if (isPillar(laserSafe.lastRow - 1, laserSafe.lastCol)) {
            pillarHash.get(hash(laserSafe.lastRow - 1, laserSafe.lastCol)).
                    setCurrLasers(pillarHash.get(hash(laserSafe.lastRow - 1, laserSafe.lastCol)).getCurrLasers() + 1);
        }
        if (isPillar(laserSafe.lastRow + 1, laserSafe.lastCol)) {
            pillarHash.get(hash(laserSafe.lastRow + 1, laserSafe.lastCol)).
                    setCurrLasers(pillarHash.get(hash(laserSafe.lastRow + 1, laserSafe.lastCol)).getCurrLasers() + 1);
        }
        if (isPillar(laserSafe.lastRow, laserSafe.lastCol - 1)) {
            pillarHash.get(hash(laserSafe.lastRow, laserSafe.lastCol - 1)).
                    setCurrLasers(pillarHash.get(hash(laserSafe.lastRow, laserSafe.lastCol - 1)).getCurrLasers() + 1);
        }
        if (isPillar(laserSafe.lastRow, laserSafe.lastCol + 1)) {
            pillarHash.get(hash(laserSafe.lastRow, laserSafe.lastCol + 1)).
                    setCurrLasers(pillarHash.get(hash(laserSafe.lastRow, laserSafe.lastCol + 1)).getCurrLasers() + 1);
        }
        successors.add(laserSafe);
        successors.add(this);
        return successors;
    }

    /**
     * Extends beams from given laser coordinate
     */
    public void AddBeams(int row, int col, SafeConfig safe) {
        //Extend beam down
        for (int i = row + 1; validCoordinates(i, col) &&
                !Character.isDigit(safe.lGrid[i][col]) &&
                safe.lGrid[i][col] != ANYPILLAR; i++) {
            if (safe.lGrid[i][col] == LASER) {
                safe.laserHash.get(hash(i, col)).setValid(false);
                safe.laserHash.get(hash(i, col)).setValid(false);
                break;
            }
            safe.lGrid[i][col] = BEAM;
        }
        //Extend the beam up
        for (int i = row - 1; validCoordinates(i, col) &&
                !Character.isDigit(safe.lGrid[i][col]) &&
                safe.lGrid[i][col] != ANYPILLAR; i--) {
            if (safe.lGrid[i][col] == LASER) {
                safe.laserHash.get(hash(i, col)).setValid(false);
                safe.laserHash.get(hash(row, col)).setValid(false);
                break;
            }
            safe.lGrid[i][col] = BEAM;
        }
        //Extend the beam right
        for (int j = col + 1; validCoordinates(row, j) &&
                !Character.isDigit(safe.lGrid[row][j]) &&
                safe.lGrid[row][j] != ANYPILLAR; j++) {
            if (safe.lGrid[row][j] == LASER) {
                safe.laserHash.get(hash(row, j)).setValid(false);
                safe.laserHash.get(hash(row, col)).setValid(false);
                break;
            }
            safe.lGrid[row][j] = BEAM;
        }
        //extend the beam left
        for (int j = col - 1; validCoordinates(row, j) &&
                !Character.isDigit(lGrid[row][j]) &&
                safe.lGrid[row][j] != ANYPILLAR; j--) {
            if (safe.lGrid[row][j] == LASER) {
                safe.laserHash.get(hash(row, j)).setValid(false);
                safe.laserHash.get(hash(row, col)).setValid(false);
                break;
            }
            safe.lGrid[row][j] = BEAM;
        }
    }

    /**
     * Checks if the given coordinates are within the grid
     */
    public boolean validCoordinates(int row, int col) {
        return ((row >= 0) && (row < rows) && (col >= 0) && (col < cols));
    }

    /**
     * Returns whether or not the coordinates given point to a numerical pillar
     */
    public boolean isPillar(int row, int col) {
        return validCoordinates(row, col) && Character.isDigit(lGrid[row][col]);
    }

    @Override
    public boolean isValid() {
        for (String s : laserHash.keySet()) {
            if (!laserHash.get(s).isValid()) {
                return false;
            }
        }
        for (String s : pillarHash.keySet()) {
            if (pillarHash.get(s).getCurrLasers() > pillarHash.get(s).getMaxLasers()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isGoal() {
        if (!isValid()){ return false; }
        //Check to make sure that there are no empty spaces
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                switch (lGrid[i][j]) {
                    case EMPTY:
                        return false;
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        String result = "  ";
        for (int i = 0; i < cols; i++) {
            if (i == cols - 1) {
                result += i % 10 + "\n  ";
                continue;
            }
            result += i % 10 + " ";
        }

        for (int i = 0; i < cols * 2 - 1; i++) {
            result += "-";
        }
        result += "\n";

        for (int i = 0; i < rows; i++) {
            result += i % 10 + "|";
            for (int j = 0; j < cols; j++) {
                if (j == cols - 1 && i == rows - 1) {
                    result += lGrid[i][j];
                    continue;
                } else if (j == cols - 1) {
                    result += lGrid[i][j] + "\n";
                    continue;
                }
                result += lGrid[i][j] + " ";
            }
        }
        return result;
    }
}
