/*
 * Copyright (C) 2017.  FileSharingSystem - https://github.com/ajayramesh23/FileSharingSystem
 * Programming Assignment from Professor Z.Lan
 * @author Ajay Ramesh
 * @author Chandra Kumar Basavaraj
 * Last Modified - 3/15/17 6:44 PM
 */

/**
 * Main java
 * Starts Index Server or Peer based on the user selection
 */

package cs550.pa1.mains;

import java.util.Scanner;


public class Main {
    public Main() {
        System.out.println("***************************************");
        System.out.println("1. Run Index Server \n2. Run Peer");
        System.out.print("Enter your choice (1/2) ? : ");
        Scanner in = new Scanner(System.in);
        switch (in.nextInt()){
            case 1: RunIndexMain.main();break;
            case 2: RunPeerMain.main();break;
            default: break;
        }
    }

    public static void main(String[] args) {
        new Main();
    }
}
