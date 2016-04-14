import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IntSummaryStatistics;
import java.util.Scanner;

/**
 * Created by lak1044 on 4/13/16.
 */
public class LaserModel {

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
    //Key is (Integer.toString(row) + Integer.toString(col))
    private static HashMap<String, Laser> laserHash;
    //Hash map of pillar locations. Key is location and value is num of required lasers
    //Key is same as laser hash map
    private static HashMap<String, Integer> pillarHash;

    public LaserModel(String fileName) throws FileNotFoundException {
        Scanner in = new Scanner(new File(fileName));
        rows = Integer.parseInt(in.next());
        cols = Integer.parseInt(in.next());
        laserHash = new HashMap<>();
        pillarHash = new HashMap<>();
        //Filling the grid
        lGrid = new char[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                lGrid[i][j] = in.next().charAt(0);
                if (Character.isDigit(lGrid[i][j])){
                    pillarHash.put(Integer.toString(i) + Integer.toString(j), Character.getNumericValue(lGrid[i][j]));
                }
                else if (lGrid[i][j] == ANYPILLAR){
                    pillarHash.put(Integer.toString(i) + Integer.toString(j), -1);
                }
            }
        }
        in.close();
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
                        if (!ValidPillar(i, j)) {
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
        laserHash.put(Integer.toString(row) + Integer.toString(col), new Laser(row, col));
        AddBeams(row, col);
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
        laserHash.remove(Integer.toString(row) + Integer.toString(col));
        for (String s : laserHash.keySet()) {
            laserHash.get(s).isValid = true;
            AddBeams(laserHash.get(s).row, laserHash.get(s).col);
        }
        System.out.printf("Laser removed at: (%d, %d)\n", row, col);
    }

    //Helper Functions

    /**
     * Checks if the given coordinates are occupied (i.e. not empty)
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
                laserHash.get(Integer.toString(i) + Integer.toString(col)).isValid = false;
                laserHash.get(Integer.toString(row) + Integer.toString(col)).isValid = false;
                break;
            }
            lGrid[i][col] = BEAM;
        }
        //Extend the beam up
        for (int i = row - 1; validCoordinates(i, col) &&
                !Character.isDigit(lGrid[i][col]) &&
                lGrid[i][col] != ANYPILLAR; i--) {
            if (lGrid[i][col] == LASER) {
                laserHash.get(Integer.toString(i) + Integer.toString(col)).isValid = false;
                laserHash.get(Integer.toString(row) + Integer.toString(col)).isValid = false;
                break;
            }
            lGrid[i][col] = BEAM;
        }
        //Extend the beam right
        for (int j = col + 1; validCoordinates(row, j) &&
                !Character.isDigit(lGrid[row][j]) &&
                lGrid[row][j] != ANYPILLAR; j++) {
            if (lGrid[row][j] == LASER) {
                laserHash.get(Integer.toString(row) + Integer.toString(j)).isValid = false;
                laserHash.get(Integer.toString(row) + Integer.toString(col)).isValid = false;
                break;
            }
            lGrid[row][j] = BEAM;
        }
        //extend the beam left
        for (int j = col - 1; validCoordinates(row, j) &&
                !Character.isDigit(lGrid[row][j]) &&
                lGrid[row][j] != ANYPILLAR; j--) {
            if (lGrid[row][j] == LASER) {
                laserHash.get(Integer.toString(row) + Integer.toString(j)).isValid = false;
                laserHash.get(Integer.toString(row) + Integer.toString(col)).isValid = false;
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
    public boolean ValidLaser(int r, int c) {
        return laserHash.get(Integer.toString(r) + Integer.toString(c)).isValid;
    }

    /**
     * Returns whether or not a pillar is valid
     */
    public boolean ValidPillar(int r, int c) {
        if (pillarHash.get(Integer.toString(r) + Integer.toString(c)) == ANYPILLAR){
            return true;
        }
        boolean isValid;
        int toCheck = pillarHash.get(Integer.toString(r) + Integer.toString(c));
        int checkCount = 0;
        int top = r - 1;
        int bottom = r + 1;
        int left = c - 1;
        int right = c + 1;
        //check top
        if (top >= 0) {
            if (lGrid[top][c] == LASER) {
                checkCount += 1;
            }
        }
        //check bottom
        if (bottom < rows) {
            if (lGrid[bottom][c] == LASER) {
                checkCount += 1;
            }
        }
        //check left
        if (left >= 0) {
            if (lGrid[r][left] == LASER) {
                checkCount += 1;
            }
        }
        //check right
        if (right < cols) {
            if (lGrid[r][right] == LASER) {
                checkCount += 1;
            }
        }
        isValid = toCheck == checkCount;
        return isValid;
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
                if (j == cols - 1 && i == rows - 1){
                    result += lGrid[i][j];
                    continue;
                }
                else if (j == cols - 1) {
                    result += lGrid[i][j] + "\n";
                    continue;
                }
                result += lGrid[i][j] + " ";
            }
        }
        return result;
    }
}