import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Created by lak1044 on 4/11/16.
 */
public class LasersPTUI {

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


    public LasersPTUI(String fileName) throws FileNotFoundException {
        Scanner in = new Scanner(new File(fileName));
        rows = Integer.parseInt(in.next());
        cols = Integer.parseInt(in.next());
        //Filling the grid
        lGrid = new char[rows][cols];
        for (int i = 0; i < rows; i++){
            for (int j = 0; j < cols; j++){
                lGrid[i][j] = in.next().charAt(0);
            }
        }
        in.close();
    }

    /**
     * Checks if the given coordinates are occupied (i.e. not empty)
     */
    public static boolean isOccupied(int row, int col){
        if (lGrid[row][col] != EMPTY){
            return false;
        }
        return true;
    }

    /**
     * Checks if the given coordinates are within the grid
     */
    public static boolean validCoordinates(int row, int col){
        if (! (row < rows)  || !(col < cols)){
            return false;
        }
        return true;
    }

    /**
     * adds laser at given position, raises error if cannot be placed
     */
    public void Add(int row, int col){
        if (!validCoordinates(row, col)){
            System.out.printf("Error adding laser at: (%d, %d)", row, col);
            return;
        }
        else if (isOccupied(row, col)){
            System.out.printf("Error adding laser at: (%d, %d", row, col);
            return;
        }
        //Set coordinates to a laser
        lGrid[row][col] = 'L';
        //Extend beam down
        for (int i = row + 1; i < row && !Character.isDigit(lGrid[i][col])  && lGrid[i][col] != ANYPILLAR; i++){
            lGrid[i][col] = BEAM;
        }
        //Extend the beam up
        for (int i = row - 1; i < row && !Character.isDigit(lGrid[i][col])  && lGrid[i][col] != ANYPILLAR; i--){
            lGrid[i][col] = BEAM;
        }
        //Extend the beam right
        for (int j =  col + 1; j < row && !Character.isDigit(lGrid[row][j])  && lGrid[row][col] != ANYPILLAR; j++){
            lGrid[row][j] = BEAM;
        }
        //extend the beam left
        for (int j = col - 1; j < row && !Character.isDigit(lGrid[j][col])  && lGrid[j][col] != ANYPILLAR; j--){
            lGrid[row][j] = BEAM;
        }
    }

    /**
     * displays current lGrid state with no status message.
     */
    public void Display(){
        System.out.println(lGrid.toString());
    }

    /**
     * The help command displays the help message to standard output,
     * with no status message.
     */
    public void Help(){
        System.out.printf("a|add r c: Add laser to (r,c)\n" +
                "d|display: Display safe\n" +
                "h|help: Print this help message\n" +
                "q|quit: Exit program\n" +
                "r|remove r c: Remove laser from (r,c)\n" +
                "v|verify: Verify safe correctness");
    }

    /**
     * The quit command causes the program to silently exit, with no additional output.
     */
    public void Quit(){
        System.exit(0);
    }

    public boolean ValidLazer(int r, int c){
        boolean isValid=true;

        for (int i=0; i<cols; i++){
            if (!(i==c)){
                if (lGrid[r][i] == 'L'){
                    isValid = false;
                }
            }
        }
        for (int i=0; i<rows; i++){
            if (!(i==r)){
                if (lGrid[i][c]=='L'){
                    isValid = false;
                }
            }
        }

        return isValid;
    }

    public boolean ValidPillar(int r, int c){
        boolean isValid;
        int toCheck = lGrid[r][c];
        int checkCount = 0;
        int top=r-1;
        int bottom=r+1;
        int left=c-1;
        int right=c+1;
        //check top
        if (top>=0){
            if (lGrid[top][c]=='L'){
                checkCount+=1;
            }
        }
        //check bottom
        if (bottom<rows){
            if (lGrid[bottom][c]=='L'){
                checkCount+=1;
            }
        }
        //check left
        if (left>=0){
            if (lGrid[r][left]=='L'){
                checkCount+=1;
            }
        }
        //check right
        if (right<cols){
            if (lGrid[r][right]=='L'){
                checkCount+=1;
            }
        }
        isValid = toCheck==checkCount;
        return isValid;
    }



    public void Verify(){
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
                        if (!ValidLazer(i,j))
                            System.out.println("Error verifying at: ("+i+", "+j+")");
                    case '*':
                    case 'X':
                    case '0':
                        if (!ValidPillar(i,j)){
                            System.out.println("Error verifying at: ("+i+", "+j+")");
                        }
                    case '1':
                    case '2':
                    case '3':
                    case '4':
                        if (!ValidPillar(i,j)){
                            System.out.println("Error verifying at: ("+i+", "+j+")");
                        }
                    case '.':
                        //case were the tile is not filled
                        System.out.println("Error verifying at: ("+i+", "+j+")");

                }
            }
        }




    }

    /**
     * removes laser from given position
     */
    public void Remove(int row, int col){
    }




    public static void main(String[] args) throws FileNotFoundException {
        LasersPTUI lasers = new LasersPTUI(args[0]);
        System.out.println(lasers.toString());
    }

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
