/*
 * Copyright (C) 2017.  FileSharingSystem - https://github.com/ajayramesh23/FileSharingSystem
 * Programming Assignment from Professor Z.Lan
 * @author Ajay Ramesh
 * @author Chandra Kumar Basavaraj
 * Last Modified - 3/15/17 6:44 PM
 */

package cs550.pa3;


import cs550.pa3.helpers.Constants;
import cs550.pa3.processor.PeerImpl;

import java.util.Scanner;

public class ApplicationRunner {

  PeerImpl peer = new PeerImpl();

  public ApplicationRunner() {
    Scanner in = new Scanner(System.in);
    System.out.print("Default config ? (yes/no) or (y/n): ");
    String choice = in.next();

    if (choice.equalsIgnoreCase("yes") ||
            choice.equalsIgnoreCase("y")) {
      peer.initConfig(Constants.DEFAULT_SERVER_HOST, Constants.DEFAULT_SERVER_PORT);
    } else {
      System.out.println("Enter Host Name Example: 'localhost' or 127.0.0.1 ");
      String hostName = in.next();
      System.out.println("Port address  : ");
      int port = in.nextInt();
      peer.initConfig(hostName, port);
    }
  }

  public static void main(String[] args) {
    new ApplicationRunner();
  }
}
