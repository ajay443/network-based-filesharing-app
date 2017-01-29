package cs550.pa1.mains;

import cs550.pa1.helpers.Constants;
import cs550.pa1.servers.IndexServer.FileProcessor;
import cs550.pa1.servers.IndexServer.IndexServerThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Scanner;

/**
 * Created by Ajay on 1/25/17.
 */
public class RunIndexMain {

    public static void main(){


        String  choice;
        int indexServerPort;
        System.out.print("Default config / Manual config ? (yes/no or y/n): ");
        Scanner in = new Scanner(System.in);
        choice = in.next();
        if(choice.equalsIgnoreCase("yes") ||
                choice.equalsIgnoreCase("y") ){
            indexServerPort = Constants.INDEX_SERVER_PORT_DEFAULT ;
        }else{
            System.out.println("Enter Index Server Port Address : ");
            indexServerPort = in.nextInt();
        }

        System.out.println("------------------------------------------");
        System.out.println("Configuration : ");
        System.out.println("Index Server Address -> "+Constants.HOST_DEFAULT+":"+indexServerPort);
        System.out.println("------------------------------------------");

        boolean listening = true;
        FileProcessor fileProcessor = new FileProcessor();
        String quit;
        try (ServerSocket serverSocket = new ServerSocket(indexServerPort)) {
            while (listening) {
                System.out.println("Index Server is running .. \nType 'quit'  or 'q' at any point of time to stop ");
                new IndexServerThread(serverSocket.accept(),fileProcessor).start();
                quit = in.next();
                if(quit.equalsIgnoreCase("quit") ||
                        quit.equalsIgnoreCase("q")) {
                    System.out.println("***************************************");
                    return;
                }

            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + indexServerPort);
            System.exit(-1);

        }


    }

    public static void main(String[] args) throws IOException {
        main();

    }
}
