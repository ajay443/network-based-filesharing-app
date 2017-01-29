package cs550.pa1.mains;

import java.util.Scanner;

/**
 * Created by Ajay on 1/28/17.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("***************************************");
        System.out.println("1. Run Index Server \n2. Run Peer Server");
        System.out.print("Enter your choice (1/2) ? : ");
        Scanner in = new Scanner(System.in);
        switch (in.nextInt()){
            case 1: RunIndexMain.main();break;
            case 2: RunPeerMain.main();break;
            default: break;
        }
    }
}
