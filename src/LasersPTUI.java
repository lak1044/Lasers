import java.io.File;
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

    public static void fileCommands(String fileName, LaserModel lasers) throws FileNotFoundException{
        Scanner sc = new Scanner(new File(fileName));
        while (sc.hasNextLine()){
            processCommand(sc.nextLine(), lasers);
        }
    }

    public static void processCommand(String commandStr, LaserModel lasers){
        String[] commandAry = commandStr.split(" +");
        switch (commandAry[0].charAt(0)){
            case 'a':
                if (commandAry.length != 3){
                    System.out.println("Incorrect coordinates");
                    return;
                }
                lasers.Add(Integer.parseInt(commandAry[1]), Integer.parseInt(commandAry[2]));
                System.out.println(lasers);
                break;
            case 'r':
                if (commandAry.length != 3){
                    System.out.println("Incorrect coordinates");
                    return;
                }
                lasers.Remove(Integer.parseInt(commandAry[1]), Integer.parseInt(commandAry[2]));
                System.out.println(lasers);
                break;
            case 'v':
                lasers.Verify();
                System.out.println(lasers);
                break;
            case 'd':
                System.out.println(lasers);
                 break;
            case 'h':
                Help();
                break;
            case 'q':
                Quit();
                break;
            default:
                System.out.println("Unrecognized command: " + commandAry[0]);
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        if (args.length == 0){
            System.out.println("Usage: java LasersPTUI safe-file [input]");
            System.exit(0);
        }
        LaserModel lasers = new LaserModel(args[0]);
        if (args.length == 2){
            fileCommands(args[1], lasers);
        }

        Scanner sc = new Scanner(System.in);
        while (true){
            System.out.printf("> ");
            String command = sc.nextLine();
            if (command.isEmpty()){
                continue;
            }
            processCommand(command, lasers);
        }
    }
}
