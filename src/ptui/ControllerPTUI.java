package ptui;

import model.Laser;
import model.LasersModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * This class represents the controller portion of the plain text UI.
 * It takes the model from the view (LasersPTUI) so that it can perform
 * the operations that are input in the run method.
 *
 * @author Sean Strout @ RIT CS
 * @author YOUR NAME HERE
 */
public class ControllerPTUI  {
    /** The UI's connection to the model */
    private LasersModel model;

    /**
     * Construct the PTUI.  Create the model and initialize the view.
     * @param model The laser model
     */
    public ControllerPTUI(LasersModel model) {
        this.model = model;
    }

    /**
     * Takes a string of a command and processes it accordingly
     */
    public static void processCommand(String commandStr, LasersModel lasers) {
        String[] commandAry = commandStr.split(" +");
        switch (commandAry[0].charAt(0)) {
            case 'a':
                if (commandAry.length != 3) {
                    System.out.println("Incorrect coordinates");
                    return;
                }
                lasers.Add(Integer.parseInt(commandAry[1]), Integer.parseInt(commandAry[2]));
                System.out.println(lasers);
                break;
            case 'r':
                if (commandAry.length != 3) {
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
    /**
     * Deals with a file of commands
     */
    public static void fileCommands(String fileName, LasersModel lasers) throws FileNotFoundException {
        Scanner sc = new Scanner(new File(fileName));
        while (sc.hasNextLine()){
            String command = sc.nextLine();
            System.out.println("> " + command);
            if (command.isEmpty()){
                continue;
            }
            processCommand(command, lasers);
        }
    }

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

    /**
     * Run the main loop.  This is the entry point for the controller
     * @param inputFile The name of the input command file, if specified
     */
    public void run(String inputFile) throws FileNotFoundException{
        System.out.println(this.model);
        if (inputFile != null){
            fileCommands(inputFile, this.model);
        }

        Scanner sc = new Scanner(System.in);
        while (true){
            System.out.printf("> ");
            String command = sc.nextLine();
            if (command.isEmpty()){
                continue;
            }
            processCommand(command, this.model);
        }
    }
}
