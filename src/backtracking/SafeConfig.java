package backtracking;

import model.Laser;
import model.LasersModel;
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
 * <p>
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
    private HashMap<String, Pillar> pillarHash;
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
     *
     * @param other the config to copy
     */
    public SafeConfig(SafeConfig other) {
        lGrid = new char[rows][cols];
        laserHash = new HashMap<>();
        pillarHash = new HashMap<>();
        for (String s : other.laserHash.keySet()) {
            laserHash.put(s, other.laserHash.get(s));
        }
        for (String s : other.pillarHash.keySet()) {
            pillarHash.put(s, other.pillarHash.get(s));
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
     * Copy constructor using a LasersModel config. Used for the "hint" button in the GUI
     * Copies the lgrid, laserhash, and pillarhash from the lasersmodel.
     */
    public SafeConfig(LasersModel other){
        this.rows = other.rows;
        this.cols = other.cols;
        lGrid = new char[rows][cols];
        laserHash = new HashMap<>();
        pillarHash = new HashMap<>();
        for (String s : other.getLaserHash().keySet()) {
            laserHash.put(s, other.getLaserHash().get(s));
        }
        for (String s : other.getPillarHash().keySet()) {
            pillarHash.put(s, other.getPillarHash().get(s));
        }
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                lGrid[i][j] = other.getlGrid()[i][j];
            }
        }
        this.lastRow = 0;
        this.lastCol = 0;
    }

    /** Returns the lGrid*/
    public char[][] getlGrid() { return this.lGrid; }

    /** Returns laserHash*/
    public HashMap<String, Laser> getLaserHash() { return this.laserHash; }

    /** Returns pillarHash*/
    public HashMap<String, Pillar> getPillarHash() { return this.pillarHash; }

    /**
     * Makes hash key from the row and column placement in the model
     */
    public String hash(int row, int col) {
        return (Integer.toString(row) + Integer.toString(col));
    }

    @Override
    public Collection<Configuration> getSuccessors() {
        Collection<Configuration> successors = new ArrayList<>();
        if (lastRow == -1 && lastCol == -1){
            return successors;
        }
        else if (lastRow == 0 && lastCol == 0 && lGrid[0][0] != EMPTY){
            incrementPos(this);
        }
        SafeConfig lSafe = new SafeConfig(this);
        SafeConfig eSafe = new SafeConfig(this);
        lSafe.lGrid[lSafe.lastRow][lSafe.lastCol] = LASER;
        lSafe.laserHash.put(hash(lSafe.lastRow, lSafe.lastCol), new Laser(lSafe.lastRow, lSafe.lastCol));
        AddBeams(lSafe);
            incrementPos(lSafe);
            incrementPos(eSafe);
            successors.add(lSafe);
        successors.add(eSafe);
        return successors;
    }

    /**
     * Increments the lastRow and lastCol states
     */
    public void incrementPos(SafeConfig safe){
        if (safe.lastRow == rows - 1 && safe.lastCol == cols - 1){
            safe.lastRow = -1;
            safe.lastCol = -1;
            return;
        }
        safe.lastCol = (safe.lastCol + 1) % cols;
        if (safe.lastRow == rows - 1 && safe.lastCol == cols - 1){
            return;
        }
        if (safe.lastCol == 0) {
            safe.lastRow = (safe.lastRow + 1) % rows;
        }
        while (safe.lGrid[safe.lastRow][safe.lastCol] != EMPTY && safe.lGrid[safe.lastRow][safe.lastCol] != LASER) {
            safe.lastCol = (safe.lastCol + 1) % cols;
            if (safe.lastRow == rows - 1 && safe.lastCol == cols - 1){
                return;
            }
            if (safe.lastCol == 0) {
                safe.lastRow = (safe.lastRow + 1) % rows;
            }
        }
    }
    /**
     * Extends beams from given laser coordinate
     */
    public void AddBeams(SafeConfig safe) {
        //Extend beam down
        for (int i = safe.lastRow + 1; validCoordinates(i, safe.lastCol) &&
                !Character.isDigit(safe.lGrid[i][safe.lastCol]) &&
                safe.lGrid[i][safe.lastCol] != ANYPILLAR; i++) {
            if (safe.lGrid[i][safe.lastCol] == LASER) {
                safe.laserHash.get(hash(i, safe.lastCol)).setValid(false);
                safe.laserHash.get(hash(safe.lastRow, safe.lastCol)).setValid(false);
                break;
            }
            safe.lGrid[i][safe.lastCol] = BEAM;
        }
        //Extend the beam up
        for (int i = safe.lastRow - 1; validCoordinates(i, safe.lastCol) &&
                !Character.isDigit(safe.lGrid[i][safe.lastCol]) &&
                safe.lGrid[i][safe.lastCol] != ANYPILLAR; i--) {
            if (safe.lGrid[i][safe.lastCol] == LASER) {
                safe.laserHash.get(hash(i, safe.lastCol)).setValid(false);
                safe.laserHash.get(hash(safe.lastRow, safe.lastCol)).setValid(false);
                break;
            }
            safe.lGrid[i][safe.lastCol] = BEAM;
        }
        //Extend the beam right
        for (int j = safe.lastCol + 1; validCoordinates(safe.lastRow, j) &&
                !Character.isDigit(safe.lGrid[safe.lastRow][j]) &&
                safe.lGrid[safe.lastRow][j] != ANYPILLAR; j++) {
            if (safe.lGrid[safe.lastRow][j] == LASER) {
                safe.laserHash.get(hash(safe.lastRow, j)).setValid(false);
                safe.laserHash.get(hash(safe.lastRow, safe.lastCol)).setValid(false);
                break;
            }
            safe.lGrid[safe.lastRow][j] = BEAM;
        }
        //extend the beam left
        for (int j = safe.lastCol - 1; validCoordinates(safe.lastRow, j) &&
                !Character.isDigit(lGrid[safe.lastRow][j]) &&
                safe.lGrid[safe.lastRow][j] != ANYPILLAR; j--) {
            if (safe.lGrid[safe.lastRow][j] == LASER) {
                safe.laserHash.get(hash(safe.lastRow, j)).setValid(false);
                safe.laserHash.get(hash(safe.lastRow, safe.lastCol)).setValid(false);
                break;
            }
            safe.lGrid[safe.lastRow][j] = BEAM;
        }
    }

    /**
     * Checks if the given coordinates are within the grid
     */
    public boolean validCoordinates(int row, int col) {
        return ((row >= 0) && (row < rows) && (col >= 0) && (col < cols));
    }

    /**
     * returns whether or not the current grid has empty spaces in it
     */
    public boolean hasEmptySpaces(){
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                switch (lGrid[i][j]) {
                    case EMPTY:
                        return true;
                }
            }
        }
        return false;
    }

    /**
     * Updates everypillars current laser amount
     */
    public void updatePillars(SafeConfig safe){
        int row;
        int col;
        int currLasers;
        int currEmpty;
        for (String s: safe.pillarHash.keySet()){
            row = safe.pillarHash.get(s).getRow();
            col = safe.pillarHash.get(s).getCol();
            if (safe.lastRow - 1 > row && safe.lastCol > col){
                continue;
            }
            currLasers = 0;
            currEmpty = 0;

            if (validCoordinates(row - 1, col)){
                if (safe.lGrid[row - 1][col] == LASER){
                    currLasers += 1;
                }
                else if (safe.lGrid[row - 1][col] == EMPTY){
                    currEmpty += 1;
                }
            }
            if (validCoordinates(row + 1, col)){
                if (safe.lGrid[row + 1][col] == LASER){
                    currLasers += 1;
                }
                else if (safe.lGrid[row + 1][col] == EMPTY){
                    currEmpty += 1;
                }
            }
            if (validCoordinates(row, col - 1)){
                if (safe.lGrid[row][col - 1] == LASER){
                    currLasers += 1;
                }
                else if (safe.lGrid[row][col - 1] == EMPTY){
                    currEmpty += 1;
                }
            }
            if (validCoordinates(row, col + 1)){
                if (safe.lGrid[row][col + 1] == LASER){
                    currLasers += 1;
                }
                else if (safe.lGrid[row][col + 1] == EMPTY){
                    currEmpty += 1;
                }
            }
            safe.pillarHash.get(s).setCurrLasers(currLasers);
            safe.pillarHash.get(s).setCurrEmpty(currEmpty);
        }
    }

    @Override
    public boolean isValid() {
        updatePillars(this);
        for (String s : this.laserHash.keySet()) {
            if (!this.laserHash.get(s).isValid()) {
                return false;
            }
        }
        for (String s : pillarHash.keySet()) {
            if (lastRow > pillarHash.get(s).getRow() && lastCol > pillarHash.get(s).getCol()){
                if (pillarHash.get(s).getCurrLasers() != pillarHash.get(s).getMaxLasers()){
                    return false;
                }
            }
            else {
                if (pillarHash.get(s).getCurrLasers() > pillarHash.get(s).getMaxLasers()) {
                    return false;
                } else if (pillarHash.get(s).getCurrLasers() + pillarHash.get(s).getCurrEmpty() < pillarHash.get(s).getMaxLasers()) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean isGoal() {
        updatePillars(this);
        for (String s: this.laserHash.keySet()){
            if (!this.laserHash.get(s).isValid()){
                return false;
            }
        }
        for (String s: this.pillarHash.keySet()){
            if (pillarHash.get(s).getCurrLasers() != pillarHash.get(s).getMaxLasers()){
                return false;
            }
        }
        return !hasEmptySpaces();
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
