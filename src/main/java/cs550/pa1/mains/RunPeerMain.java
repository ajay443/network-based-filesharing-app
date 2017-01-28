package cs550.pa1.mains;

import cs550.pa1.helpers.Util;
import cs550.pa1.servers.PeerClientImpl;
import cs550.pa1.servers.PeerServerImpl;

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
            Util.LOGGER.info("Peer Server started");
            peerServer.start();
            peerServer.join(500);
            Util.LOGGER.info("Peer Client started");
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
	    if (args.length != 3) {
		    System.err.println("Usage: java Peer <host name> <port number of sever to connect> <port number to host as server>");
		    System.exit(1);
	    }

	    String hostName = args[0];
	    int portNumber = Integer.parseInt(args[1]);
	    int server_PN = Integer.parseInt(args[2]);
	    runMain(hostName, portNumber, server_PN);
    }

    private static void runMain(String hostName, int portNumber, int server_PN) {
        RunPeerMain runPeerMain = new RunPeerMain(hostName, portNumber, server_PN);
    }
}
