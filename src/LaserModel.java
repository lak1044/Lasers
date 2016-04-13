import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
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
    //List of lasers
    private static ArrayList<Laser> laserList;

    public LaserModel(String fileName) throws FileNotFoundException {
        Scanner in = new Scanner(new File(fileName));
        rows = Integer.parseInt(in.next());
        cols = Integer.parseInt(in.next());
        //Filling the grid
        laserList = new ArrayList<>();
        lGrid = new char[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                lGrid[i][j] = in.next().charAt(0);
            }
        }
        in.close();
    }

    /**
     * adds laser at given position, raises error if cannot be placed
     */
    public static void Add(int row, int col) {
        if (!validCoordinates(row, col)) {
            System.out.printf("Error adding laser at: (%d, %d)\n", row, col);
            return;
        } else if (isOccupied(row, col)) {
            System.out.printf("Error adding laser at: (%d, %d\n", row, col);
            return;
        }
        //Set coordinates to a laser
        lGrid[row][col] = LASER;
        laserList.add(new Laser(row, col));
        AddBeams(row, col);
    }


    public static void Verify(){
        /**
         * The verify command displays a status message that indicates whether the safe
         * is valid or not. In order to be valid, none of the rules of the safe may be
         * violated. Each tile that is not a pillar must have either a laser or beam
         * covering it. Each pillar that requires a certain number of neighboring lasers
         * must add up exactly. If two or more lasers are in sight of each other, in the
         * cardinal directions, it is invalid.
         */

        //check that there are no empty tiles
        for (int i=0; i< rows; i++){
            for (int j=0; j<cols; j++){
                switch (lGrid[i][j]){
                    case 'L':
                        if (!ValidLaser(i,j)) {
                            System.out.println("Error verifying at: (" + i + ", " + j + ")");
                            return;
                        }
                        break;
                    case '*':
                    case 'X':
                        break;
                    case '0':
                        //if (!ValidPillar(i,j)){
                        //    System.out.println("Error verifying at: ("+i+", "+j+")");
                        //    return;
                        //}
                    case '1':
                    case '2':
                    case '3':
                    case '4':
                        if (!ValidPillar(i,j)){
                            System.out.println("Error verifying at: ("+i+", "+j+")");
                            return;
                        }
                        break;
                    case '.':
                        //case were the tile is not filled
                        System.out.println("Error verifying at: ("+i+", "+j+")");
                        return;

                }
            }
        }
        System.out.println("Safe is fully verified!");
    }

    /**
     * removes laser from given position
     */
    public static void Remove(int row, int col){
        if (!validCoordinates(row, col)){
            System.out.printf("Error removing laser at: (%d, %d)\n", row, col);
            return;
        }
        else if (lGrid[row][col] != LASER){
            System.out.printf("Error removing laser at: (%d, %d)\n", row, col);
            return;
        }
        //Set coordinates to empty
        lGrid[row][col] = EMPTY;
        RemoveBeams(row, col);
        int toRemove = -1;
        for (Laser l: laserList){
            if (l.row == row && l.col == col){
                toRemove = laserList.indexOf(l);
                continue;
            }
            l.isValid = true;
            AddBeams(l.row, l.col);
        }
        if (toRemove != -1){
            laserList.remove(toRemove);
        }
    }

    //Helper Functions
    /**
     * Checks if the given coordinates are occupied (i.e. not empty)
     */
    public static boolean isOccupied(int row, int col) {
        return (lGrid[row][col] != EMPTY && lGrid[row][col] != BEAM);
    }

    /**
     * Checks if the given coordinates are within the grid
     */
    public static boolean validCoordinates(int row, int col) {
        return ((row >= 0) && (row < rows) && (col >= 0) && (col < cols));
    }

    /**
     * Extends beams from given laser coordinate
     */
    public static void AddBeams(int row, int col){
        //Extend beam down
        for (int i = row + 1; validCoordinates(i, col) &&
                !Character.isDigit(lGrid[i][col]) &&
                lGrid[i][col] != ANYPILLAR; i++){
            if (lGrid[i][col] == LASER){
                for (Laser l: laserList){
                    if (l.row == i && l.col == col){
                        l.isValid = false;
                    }
                    else if (l.row == row && l.col == col){
                        l.isValid = false;
                    }
                }
                break;
            }
            lGrid[i][col] = BEAM;
        }
        //Extend the beam up
        for (int i = row - 1; validCoordinates(i, col) &&
                !Character.isDigit(lGrid[i][col]) &&
                lGrid[i][col] != ANYPILLAR; i--){
            if (lGrid[i][col] == LASER){
                for (Laser l: laserList){
                    if (l.row == i && l.col == col){
                        l.isValid = false;
                    }
                    else if (l.row == row && l.col == col){
                        l.isValid = false;
                    }
                }
                break;
            }
            lGrid[i][col] = BEAM;
        }
        //Extend the beam right
        for (int j =  col + 1; validCoordinates(row, j) &&
                !Character.isDigit(lGrid[row][j]) &&
                lGrid[row][j] != ANYPILLAR; j++){
            if (lGrid[row][j] == LASER){
                for (Laser l: laserList){
                    if (l.row == row && l.col == j){
                        l.isValid = false;
                    }
                    else if (l.row == row && l.col == col){
                        l.isValid = false;
                    }
                }
                break;
            }
            lGrid[row][j] = BEAM;
        }
        //extend the beam left
        for (int j = col - 1; validCoordinates(row, j)&&
                !Character.isDigit(lGrid[row][j]) &&
                lGrid[row][j] != ANYPILLAR; j--){
            if (lGrid[row][j] == LASER){
                for (Laser l: laserList){
                    if (l.row == row && l.col == j){
                        l.isValid = false;
                    }
                    else if (l.row == row && l.col == col){
                        l.isValid = false;
                    }
                }
                break;
            }
            lGrid[row][j] = BEAM;
        }
    }

    /**
     * Remove beams from a given laser position
     * @param row
     * @param col
     */
    public static void RemoveBeams(int row, int col){
        //Remove beam down
        for (int i = row + 1; validCoordinates(i, col) &&
                !Character.isDigit(lGrid[i][col]) &&
                lGrid[i][col] != ANYPILLAR &&
                lGrid[i][col] != LASER; i++){
            lGrid[i][col] = EMPTY;
        }
        //Remove the beam up
        for (int i = row - 1; validCoordinates(i, col) &&
                !Character.isDigit(lGrid[i][col])
                && lGrid[i][col] != ANYPILLAR &&
                lGrid[i][col] != LASER; i--){
            lGrid[i][col] = EMPTY;
        }
        //Remove the beam right
        for (int j =  col + 1; validCoordinates(row, j) &&
                !Character.isDigit(lGrid[row][j]) &&
                lGrid[row][j] != ANYPILLAR &&
                lGrid[row][j] != LASER; j++){
            lGrid[row][j] = EMPTY;
        }
        //Remove the beam left
        for (int j = col - 1; validCoordinates(row, j) &&
                !Character.isDigit(lGrid[row][j]) &&
                lGrid[row][j] != ANYPILLAR &&
                lGrid[row][j] != LASER; j--){
            lGrid[row][j] = EMPTY;
        }
    }

    public static boolean ValidLaser(int r, int c){
        for (Laser l: laserList){
            if (l.row == r && l.col == c){
                return l.isValid;
            }
        }
        return true; //Shouldn't get here, just to satisfy intellij
    }

    public static boolean ValidPillar(int r, int c){
        boolean isValid;
        String checkStr = lGrid[r][c]+"";
        int toCheck = Integer.parseInt(checkStr);
        int checkCount = 0;
        int top=r-1;
        int bottom=r+1;
        int left=c-1;
        int right=c+1;
        //check top
        if (top>=0){
            if (lGrid[top][c]== LASER){
                checkCount+=1;
            }
        }
        //check bottom
        if (bottom<rows){
            if (lGrid[bottom][c]==LASER){
                checkCount+=1;
            }
        }
        //check left
        if (left>=0){
            if (lGrid[r][left]==LASER){
                checkCount+=1;
            }
        }
        //check right
        if (right<cols){
            if (lGrid[r][right]==LASER){
                checkCount+=1;
            }
        }
        isValid = toCheck==checkCount;
        return isValid;
    }

    //Overrides
    @Override
    public String toString(){
        String result = "  ";
        for (int i = 0; i < cols; i++){
            if (i == cols - 1){
                result += i + "\n  ";
                continue;
            }
            result += i + " ";
        }

        for (int i = 0; i < cols * 2 - 1; i++){
            result += "-";
        }
        result += "\n";

        for (int i = 0; i < rows; i++){
            result += i + "|";
            for (int j = 0; j < cols; j++){
                if (j == cols - 1){
                    result += lGrid[i][j] + "\n";
                    continue;
                }
                result += lGrid[i][j] + " ";
            }
        }
        return result;
    }
}