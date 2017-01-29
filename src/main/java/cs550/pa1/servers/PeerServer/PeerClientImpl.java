package cs550.pa1.servers.PeerServer;

import cs550.pa1.helpers.Constants;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by Ajay on 1/26/17.
 */
public class PeerClientImpl implements Peer {

    public String hostname_client ;
    public int port_client ;


    public PeerClientImpl() {
        this.hostname_client = Constants.CLIENT_HOST_DEFAULT;
        this.port_client = Constants.CLIENT_PORT_DEFAULT;
    }

    public PeerClientImpl(String hostname_client, int port_client) {
        this.hostname_client = hostname_client;
        this.port_client = port_client;
    }

    @Override
    public void init() {
        peerClientInterface();

    }

    @Override
    public void display() {

    }


    public void peerClientInterface(){

        String fileName="";
        try {
            while(true){
                System.out.println("\n1 : Lookup a file\n2 : Download file from a peer\n3 : Register File\n4 : Exit\nEnter your choice number");
                Scanner in = new Scanner(System.in);
                int choice = in.nextInt();
                switch(choice){
                    case 1: System.out.println("Enter filename : \n");
                        lookupFile(in.next());
                        break;
                    case 2:
                        System.out.println("Enter filename : \n");
                        fileName = in.next();
                        System.out.println("Enter Host name of the download server : \n");
                        String host = in.next();
                        System.out.println("Enter port number of of the download server : \n");
                        int hostPort = in.nextInt();
                        System.out.println("Enter file name  \n");
                        download(fileName,host,hostPort);
                        break;
                    case 3:
                        System.out.println("Enter file location : ");
                        System.out.println("Example: /user/files/text1.txt  ");
                        registerFile(in.next());
                        break;
                    case 4:
                        System.exit(0);
                    default:System.exit(0);
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }

    }

    private void lookupFile(String fileName) throws IOException{
        // TODO change the default host and peer config
        Socket socketToIndexServer = new Socket( Constants.INDEX_SERVER_HOST, Constants.INDEX_SERVER_PORT_DEFAULT );
        PrintWriter out = new PrintWriter( socketToIndexServer.getOutputStream(), true );
        BufferedReader in = new BufferedReader( new InputStreamReader( socketToIndexServer.getInputStream() ) );

        out.println("lookup "+fileName);
        String message;
        System.out.println("File location ,peer port address to down load  ");
        System.out.println("***********************************************");

        while((message = in.readLine()) != null){
            if(message.length() == 0){
                in.close();
                out.close();
                return;
            }
            System.out.println(message);
        }
        System.out.println("***********************************************");

        socketToIndexServer.close();
    }



    private void download(String fileName,String host, int port) throws IOException {

        Socket peerClientSocket = new Socket(host,port);
        PrintWriter out = new PrintWriter( peerClientSocket.getOutputStream(), true );
        //BufferedReader in = new BufferedReader(new InputStreamReader(peerClientSocket.getInputStream()));
        InputStream in = peerClientSocket.getInputStream();
        OutputStream fout = new FileOutputStream("output/"+fileName);
        try{
            out.println("Download "+fileName);
            String message = "";
            PrintWriter p = new PrintWriter(fileName,"UTF-8");
            
            byte[] bytes = new byte[16*1024];

            int count;
            while ((count = in.read(bytes)) > 0) {
                fout.write(bytes, 0, count);
            }
            
            /*while((message = in.readLine()) != null){
                System.out.println(message);
                p.println(message);

                if(message.contains("Download")){
                    String params[] = message.split(" ");
                    File f = new File("peers/"+params[1]);
                    FileInputStream fip = new FileInputStream(f);

                    String fileContent = "";
                    int content = 0;
                    while((content = fip.read()) != -1){
                        fileContent += (char)content;
                    }
                    
                    
                    
                    System.out.println("File Content : " + fileContent);
                    out.println(fileContent);
                    fip.close();
                    peerClientSocket.shutdownInput();

                }
            }*/
            p.close();

        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            peerClientSocket.close();
        }
    }

    private void registerFile(String fileLocation) throws IOException{
        Socket sock = new Socket(Constants.INDEX_SERVER_HOST, Constants.INDEX_SERVER_PORT_DEFAULT);
        PrintWriter out = new PrintWriter(sock.getOutputStream(),true);
        out.println("register "+ fileLocation+", "+this.hostname_client+":"+this.port_client);
        sock.shutdownInput();
    }

}
