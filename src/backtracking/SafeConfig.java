package backtracking;

import model.Laser;
import model.Pillar;

import java.io.File;
import java.io.FileNotFoundException;
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
    private static char[][] lGrid;
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
     * Makes hash key from the row and column placement in the model
     */
    public String hash(int row, int col){
        return (Integer.toString(row) + Integer.toString(col));
    }

    @Override
    public Collection<Configuration> getSuccessors() {
        // TODO
        return null;
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
        // TODO
        return false;
    }
}
