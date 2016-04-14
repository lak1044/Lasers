import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Created by lak1044 on 4/11/16.
 */
public class LasersPTUI {

    /**
     * The help command displays the help message to standard output,
     * with no status message.
     */
    public static void Help(){
        System.out.printf("a|add r c: Add laser to (r,c)\n" +
                "d|display: Display safe\n" +
                "h|help: Print this help message\n" +
                "q|quit: Exit program\n" +
                "r|remove r c: Remove laser from (r,c)\n" +
                "v|verify: Verify safe correctness\n");
    }

    /**
     * The quit command causes the program to silently exit, with no additional output.
     */
    public static void Quit(){
        System.exit(0);
    }

    public static void main(String[] args) throws FileNotFoundException {
        LaserModel lasers = new LaserModel(args[0]);
        Scanner sc = new Scanner(System.in);
        if (args.length==1) {
            System.out.println(lasers.toString());
            while (true){
                System.out.printf("> ");
                String s = sc.nextLine();
                if (s.charAt(0)=='h'){
                    Help();
                }
                else if (s.charAt(0) == 'a'){
                    String[] add=s.split(" ");
                    LaserModel.Add(Integer.parseInt(add[1]),Integer.parseInt(add[2]));
                }
                else if (s.charAt(0)=='r'){
                    String[] remove = s.split(" ");
                    LaserModel.Remove(Integer.parseInt(remove[1]), Integer.parseInt(remove[2]));
                }
                else if (s.charAt(0)=='d'){
                    System.out.println(lasers);
                }
                else if (s.charAt(0)=='v'){
                    LaserModel.Verify();
                }
                else if (s.charAt(0)=='q'){
                    Quit();
                }

            }
        }
    }
}
