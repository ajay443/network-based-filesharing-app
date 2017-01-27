package cs550.pa1.servers;

import cs550.pa1.helpers.Constants;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Ajay on 1/26/17.
 */
public class PeerServerImpl implements Peer{

    public String hostname_server ;
    public int port_server ;
    public ServerSocket serverSocket;

    private Socket socket = null;

    public PeerServerImpl() {
        this.hostname_server = Constants.SERVER_HOST_DEFAULT;
        this.port_server = Constants.SERVER_PORT_DEFAULT;

    }

    public PeerServerImpl(String hostname_server, int port_server) {
        this.hostname_server = hostname_server;
        this.port_server = port_server;
    }

    @Override
    public void init() {
        boolean listening = true;
        try (ServerSocket serverSocket = new ServerSocket(this.port_server)) {
            while (listening) {
                System.out.println("Server is listening to port:"+this.port_server);
                socket = serverSocket.accept();
                peerRun();

            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Could not listen on port " + this.port_server);
            System.exit(-1);
        }


    }

    @Override
    public void display() {
        System.out.println( "PeerServerImpl{" +
                "hostname_server='" + hostname_server + '\'' +
                ", port_server=" + port_server +
                '}');
    }



    public void peerRun() {
        try (
                //PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                socket.getInputStream()));
        ) {
            String inputLine, outputLine;
            outputLine = "";
            //out.println(outputLine);

            if ((inputLine = in.readLine()) != null) {
                processInput(inputLine);
                //out.println(outputLine);

            }
            socket.shutdownInput();
            //socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processInput(String input) {
        System.out.println(input);
        //String fileContent = "";
        String params[] = input.split(" ");
        if (params[0].equals("Download")){
            File f = new File("peers/"+params[1]);
            try(
                    InputStream fip = new FileInputStream(f);
            		OutputStream out = socket.getOutputStream();
            )
            {
                //int content = 0;
                byte b[] = new byte[16 * 1024];
                int count;
                while ((count = fip.read(b)) > 0) {
                    out.write(b, 0, count);
                }
                /*while((content = fip.read(b)) != -1){
                    fileContent += (char)content;
                }*/
                //System.out.println("File Content : Server " + fileContent);
                //out.println(fileContent);
                //return fileContent;
            }
            catch(Exception e){
            }
            finally{
            }
        }
        //return  fileContent;
    }



}
