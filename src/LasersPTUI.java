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

    public void Add(int row, int col){
        /**
         * adds laser at given position, raises error if cannot be placed
         */
    }

    public void Display(){
        /**
         * displays current lGrid state with no status message.
         */
    }

    public void Help(){
        /**
         * The help command displays the help message to standard output,
         * with no status message.
         */
    }

    public void Quit(){
        /**
         * The quit command causes the program to silently exit, with no additional output.
         */
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
    }

    public void Remove(int row, int col){
        /**
         * removes laser from given position
         */
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
