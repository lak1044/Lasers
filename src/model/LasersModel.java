package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Observable;
import java.util.Scanner;

public class LasersModel extends Observable {

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

    /**
     * Creates a new instance of a laserModel
     * Reads in safe file and creates a local instance of the safe grid
     * Adds numerical pillars to the pillar hash for future use
     */
    public LasersModel(String filename) throws FileNotFoundException {
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
     * adds laser at given position, raises error if cannot be placed
     */
    public void Add(int row, int col) {
        if (!validCoordinates(row, col)) {
            System.out.printf("Error adding laser at: (%d, %d)\n", row, col);
            return;
        } else if (isOccupied(row, col)) {
            System.out.printf("Error adding laser at: (%d, %d)\n", row, col);
            return;
        }
        //Set coordinates to a laser
        lGrid[row][col] = LASER;
        Laser newLaser = new Laser(row, col);
        laserHash.put(hash(row, col), newLaser);
        AddBeams(row, col);
        if (isPillar(row - 1, col)) {
            pillarHash.get(hash(row - 1, col)).currLasers += 1;
        }
        if (isPillar(row + 1, col)) {
            pillarHash.get(hash(row + 1, col)).currLasers += 1;
        }
        if (isPillar(row, col - 1)) {
            pillarHash.get(hash(row, col - 1)).currLasers += 1;
        }
        if (isPillar(row, col + 1)) {
            pillarHash.get(hash(row, col + 1)).currLasers += 1;
        }
        System.out.printf("Laser added at: (%d, %d)\n", row, col);
    }

    /**
     * removes laser from given position
     */
    public void Remove(int row, int col) {
        if (!validCoordinates(row, col)) {
            System.out.printf("Error removing laser at: (%d, %d)\n", row, col);
            return;
        } else if (lGrid[row][col] != LASER) {
            System.out.printf("Error removing laser at: (%d, %d)\n", row, col);
            return;
        }
        //Set coordinates to empty
        lGrid[row][col] = EMPTY;
        RemoveBeams(row, col);
        laserHash.remove(hash(row, col));
        for (String s : laserHash.keySet()) {
            laserHash.get(s).isValid = true;
            AddBeams(laserHash.get(s).row, laserHash.get(s).col);
        }
        if (isPillar(row - 1, col)) {
            pillarHash.get(hash(row - 1, col)).currLasers -= 1;
        }
        if (isPillar(row + 1, col)) {
            pillarHash.get(hash(row + 1, col)).currLasers -= 1;
        }
        if (isPillar(row, col - 1)) {
            pillarHash.get(hash(row, col - 1)).currLasers -= 1;
        }
        if (isPillar(row, col + 1)) {
            pillarHash.get(hash(row, col + 1)).currLasers -= 1;
        }
        System.out.printf("Laser removed at: (%d, %d)\n", row, col);
    }

    /**
     * The verify command displays a status message that indicates whether the safe
     * is valid or not. In order to be valid, none of the rules of the safe may be
     * violated. Each tile that is not a pillar must have either a laser or beam
     * covering it. Each pillar that requires a certain number of neighboring lasers
     * must add up exactly. If two or more lasers are in sight of each other, in the
     * cardinal directions, it is invalid.
     */
    public void Verify() {
        //check that there are no empty tiles
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                switch (lGrid[i][j]) {
                    //Doesn't need to worry about beams or X-based pillars
                    case BEAM:
                    case ANYPILLAR:
                        break;
                    case LASER:
                        if (!ValidLaser(i, j)) {
                            System.out.println("Error verifying at: (" + i + ", " + j + ")");
                            return;
                        }
                        break;
                    case '0':
                    case '1':
                    case '2':
                    case '3':
                    case '4':
                        if (pillarHash.get(hash(i, j)).currLasers !=
                                pillarHash.get(hash(i, j)).maxLasers) {
                            System.out.println("Error verifying at: (" + i + ", " + j + ")");
                            return;
                        }
                        break;
                    //Must be empty if failed all other cases
                    default:
                        System.out.println("Error verifying at: (" + i + ", " + j + ")");
                        return;

                }
            }
        }
        System.out.println("Safe is fully verified!");
    }

    /**
     * Checks whether or not the current state of the model is valid or not
     * Does not worry about being the goal
     */
    public boolean isValid() {
        for (String s : laserHash.keySet()) {
            if (!laserHash.get(s).isValid) {
                return false;
            }
        }
        for (String s : pillarHash.keySet()) {
            if (pillarHash.get(s).currLasers > pillarHash.get(s).maxLasers) {
                return false;
            }
        }
        return true;
    }

    //Helper Functions

    /**
     * Checks if the given coordinates are occupied by a beam or pillar
     */
    public boolean isOccupied(int row, int col) {
        return (lGrid[row][col] != EMPTY && lGrid[row][col] != BEAM);
    }

    /**
     * Checks if the given coordinates are within the grid
     */
    public boolean validCoordinates(int row, int col) {
        return ((row >= 0) && (row < rows) && (col >= 0) && (col < cols));
    }

    /**
     * Extends beams from given laser coordinate
     */
    public void AddBeams(int row, int col) {
        //Extend beam down
        for (int i = row + 1; validCoordinates(i, col) &&
                !Character.isDigit(lGrid[i][col]) &&
                lGrid[i][col] != ANYPILLAR; i++) {
            if (lGrid[i][col] == LASER) {
                laserHash.get(hash(i, col)).isValid = false;
                laserHash.get(hash(i, col)).isValid = false;
                break;
            }
            lGrid[i][col] = BEAM;
        }
        //Extend the beam up
        for (int i = row - 1; validCoordinates(i, col) &&
                !Character.isDigit(lGrid[i][col]) &&
                lGrid[i][col] != ANYPILLAR; i--) {
            if (lGrid[i][col] == LASER) {
                laserHash.get(hash(i, col)).isValid = false;
                laserHash.get(hash(row, col)).isValid = false;
                break;
            }
            lGrid[i][col] = BEAM;
        }
        //Extend the beam right
        for (int j = col + 1; validCoordinates(row, j) &&
                !Character.isDigit(lGrid[row][j]) &&
                lGrid[row][j] != ANYPILLAR; j++) {
            if (lGrid[row][j] == LASER) {
                laserHash.get(hash(row, j)).isValid = false;
                laserHash.get(hash(row, col)).isValid = false;
                break;
            }
            lGrid[row][j] = BEAM;
        }
        //extend the beam left
        for (int j = col - 1; validCoordinates(row, j) &&
                !Character.isDigit(lGrid[row][j]) &&
                lGrid[row][j] != ANYPILLAR; j--) {
            if (lGrid[row][j] == LASER) {
                laserHash.get(hash(row, j)).isValid = false;
                laserHash.get(hash(row, col)).isValid = false;
                break;
            }
            lGrid[row][j] = BEAM;
        }
    }

    /**
     * Remove beams from a given laser position
     */
    public void RemoveBeams(int row, int col) {
        //Remove beam down
        for (int i = row + 1; validCoordinates(i, col) &&
                !Character.isDigit(lGrid[i][col]) &&
                lGrid[i][col] != ANYPILLAR &&
                lGrid[i][col] != LASER; i++) {
            lGrid[i][col] = EMPTY;
        }
        //Remove the beam up
        for (int i = row - 1; validCoordinates(i, col) &&
                !Character.isDigit(lGrid[i][col])
                && lGrid[i][col] != ANYPILLAR &&
                lGrid[i][col] != LASER; i--) {
            lGrid[i][col] = EMPTY;
        }
        //Remove the beam right
        for (int j = col + 1; validCoordinates(row, j) &&
                !Character.isDigit(lGrid[row][j]) &&
                lGrid[row][j] != ANYPILLAR &&
                lGrid[row][j] != LASER; j++) {
            lGrid[row][j] = EMPTY;
        }
        //Remove the beam left
        for (int j = col - 1; validCoordinates(row, j) &&
                !Character.isDigit(lGrid[row][j]) &&
                lGrid[row][j] != ANYPILLAR &&
                lGrid[row][j] != LASER; j--) {
            lGrid[row][j] = EMPTY;
        }
    }

    /**
     * Returns whether or not the laser is valid.
     * This is handled when placing lasers and is in the state of the laser itself
     */
    public boolean ValidLaser(int row, int col) {
        return laserHash.get(hash(row, col)).isValid;
    }

    /**
     * Returns whether or not the coordinates given point to a numerical pillar
     */
    public boolean isPillar(int row, int col) {
        return validCoordinates(row, col) && Character.isDigit(lGrid[row][col]);
    }

    /**
     * Makes hash key from the row and column placement in the model
     */
    public String hash(int row, int col){
        return (Integer.toString(row) + Integer.toString(col));
    }

    //Overrides

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
    // TODO

    /**
     * A utility method that indicates the model has changed and
     * notifies observers
     */
    private void announceChange() {
        setChanged();
        notifyObservers();
    }
}
