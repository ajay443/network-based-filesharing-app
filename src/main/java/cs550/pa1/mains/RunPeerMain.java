package cs550.pa1.mains;

import cs550.pa1.servers.PeerServer.PeerClientImpl;
import cs550.pa1.servers.PeerServer.PeerServerImpl;

import java.util.Scanner;

/**
 * Created by Ajay on 1/26/17.
 */
public class RunPeerMain {
    String hn;
    int pc;
    int ps;

    public RunPeerMain(String hn, int pc, int ps) {
        this.hn = hn;
        this.pc = pc;
        this.ps = ps;
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
            PeerServerImpl peerServerImpl = new PeerServerImpl(hn,ps);
            peerServerImpl.init();
            peerServerImpl.display();
        }
    };
    Thread peerClient = new Thread () {
        public void run () {
            System.out.println("\n\nPeer Client Started");
            try {
                PeerClientImpl peerClient = new PeerClientImpl(hn,pc,ps);
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
        Scanner in = new Scanner(System.in);
        System.out.println("Enter Host Name : ");
        String hostName = in.next();
        System.out.println("Enter Port Address  : ");
        int portNumber = in.nextInt();
        System.out.println("Enter PeerServer Port Address  : ");
	    int server_PN = in.nextInt();
        RunPeerMain runPeerMain = new RunPeerMain(hostName, portNumber, server_PN);

    }

}
