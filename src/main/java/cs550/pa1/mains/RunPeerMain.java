package cs550.pa1.mains;

import cs550.pa1.helpers.Util;
import cs550.pa1.servers.PeerServer.PeerClientImpl;
import cs550.pa1.servers.PeerServer.PeerServerImpl;

import java.util.Scanner;

/**
 * Created by Ajay on 1/26/17.
 */
public class RunPeerMain {
    String hostName;
    int indexServerPort;
    int peerServerPort;

    public RunPeerMain(String hn, int pc, int ps) {
        this.hostName = hn;
        this.indexServerPort = pc;
        this.peerServerPort = ps;
        try {
            peerServer.start();
            peerServer.join(500);
            peerClient.start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    Thread peerServer = new Thread () {
        public void run () {
            System.out.println("Peer Server Started");
            PeerServerImpl peerServerImpl = new PeerServerImpl(peerServerPort);
            peerServerImpl.init();
            peerServerImpl.display();
        }
    };
    Thread peerClient = new Thread () {
        public void run () {
            System.out.println("\n\nPeer Client Started");
            try {
                PeerClientImpl peerClient = new PeerClientImpl(hostName, indexServerPort, peerServerPort);
                peerClient.init();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    public static void main(String[] args) {
          main();
    }

    static void main() {
        String hostName,choice;
        int indexServerPort,peerServerPort;

        Scanner in = new Scanner(System.in);
        System.out.println("Default config / Manual config ? (yes/no)");
        choice = in.next();
        if(choice.equalsIgnoreCase("yes") ||choice.equalsIgnoreCase("y") ){
            hostName = "localhost";
            indexServerPort = 5000 ;
            peerServerPort = 6100;


        }else{
            System.out.println("Enter Host Name Example : localhost: ");
            hostName = in.next();
            System.out.println("Enter Index Server Port Address  : ");
            indexServerPort = in.nextInt();
            System.out.println("Enter PeerServer Port Address  : ");
            peerServerPort = in.nextInt();
        }

        Util.createFolder("peer_"+peerServerPort);
        System.out.println("------------------------------------------");
        System.out.println("Configuration : ");
        System.out.println("Index Server Address : "+hostName+":"+indexServerPort);
        System.out.println("Peer Server Address : "+hostName+":"+peerServerPort);
        System.out.println("------------------------------------------");

        RunPeerMain runPeerMain = new RunPeerMain(hostName, indexServerPort, peerServerPort);

    }

}
