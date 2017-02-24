package cs550.pa2;

import java.util.Scanner;

/**
 * Created by Ajay on 2/24/17.
 */
public class MainPa2 {

    Peer peer = new PeerImpl();
    public static void main(String[] args) {
        // This will run programming assignment 2
        new MainPa2();
    }

    public MainPa2() {
        // Init peer ...
        Scanner in = new Scanner(System.in);
        System.out.print("Default config ? (yes/no) or (y/n): ");
        String choice = in.next();

        if(choice.equalsIgnoreCase("yes") ||
                choice.equalsIgnoreCase("y") ){
            peer.initConfig();
        }else{
            System.out.println("Enter Host Name Example: 'localhost' or 127.0.0.1 ");
            String hostName = in.next();
            System.out.println("Port address  : ");
            int port = in.nextInt();

            peer.initConfig(hostName,port);
        }
    }
}
