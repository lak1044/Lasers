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
