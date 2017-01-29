package cs550.pa1.mains;

import cs550.pa1.helpers.Util;
import cs550.pa1.servers.PeerServer.PeerClientImpl;
import cs550.pa1.servers.PeerServer.PeerServerImpl;

/**
 * Created by Ajay on 1/26/17.
 */
public class RunPeerMain {

    public RunPeerMain() {

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
            PeerServerImpl peerServerImpl = new PeerServerImpl();
            peerServerImpl.init();
            peerServerImpl.display();
        }
    };
    Thread peerClient = new Thread () {
        public void run () {
            System.out.println("\n\nPeer Client Started");
            PeerClientImpl peerClient = new PeerClientImpl();
            peerClient.init();
        }
    };


    public static void main(String[] args) {
        runMain();
    }

    private static void runMain() {
        RunPeerMain runPeerMain = new RunPeerMain();
    }
}
