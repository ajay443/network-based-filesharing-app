package cs550.pa1.servers.PeerServer;

import cs550.pa1.helpers.Constants;
import cs550.pa1.helpers.Util;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Ajay on 1/26/17.
 */
public class PeerServerImpl implements Peer,Runnable{

    public int port ;

    private Socket socket = null;


    public PeerServerImpl(int port_server) {
        this.port = port_server;
    }

    @Override
    public void init() {
        boolean listening = true;
        try (ServerSocket serverSocket = new ServerSocket(this.port)) {
            while (listening) {
                System.out.println(",listening to port:"+this.port);
                socket = serverSocket.accept();
                peerRun();

            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Could not listen on port " + this.port);
            System.exit(-1);
        }catch ( InterruptedException e ){
            e.printStackTrace();
            System.err.println("Could not able to download");
            System.exit(-1);
        }
    }



    public void peerRun() throws InterruptedException {
        Thread t = new Thread( this,"PeerRun");
        t.start();
        t.join();
    }



    @Override
    public void run() {
        System.out.println("Sending the file...");
        try (
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                socket.getInputStream()));
        ) {
            String inputLine;

            if ((inputLine = in.readLine()) != null) {
                processInput(inputLine);
            }
            socket.close();
            System.out.println("Sending the file completed ...");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void processInput(String input) {
        String params[] = input.split(" ");
        if (params[0].equals("Download"))
            Util.downloadFile(Constants.PEER_FOLDER_PREFIX + this.port + "/" + params[1],socket);
    }
}
