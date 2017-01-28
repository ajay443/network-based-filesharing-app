package cs550.pa1.servers;

import cs550.pa1.helpers.Constants;
import cs550.pa1.helpers.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

/**
 * Created by Ajay on 1/25/17.
 */
public  class IndexImpl implements Index {

    ServerSocket indexServerSocket;
    Socket clientSocket;
    PrintWriter out;
    BufferedReader in;

    public IndexImpl() throws IOException {
        System.out.print("Index Server is running ..... ");
        indexServerSocket = new ServerSocket(Constants.INDEX_SERVER_PORT_DEFAULT);
        indexServerClientInterface();
    }

    public IndexImpl(int serverPortNumber) throws IOException {
        System.out.print("Index Server is running ..... ");
        indexServerSocket = new ServerSocket(serverPortNumber);
        indexServerClientInterface();
    }

    private void indexServerClientInterface() {
        while(true){
            try {
                clientSocket = indexServerSocket.accept();
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                String inputLineFromClient;
                while ((inputLineFromClient = in.readLine()) != null) {
                    String params[] = inputLineFromClient.split(" ");
                    if(params[0].equals("lookup")){
                        System.out.println("Searching for file");
                        List<String> l = lookup(params[1]);
                        for(String t : l){
                            out.println(t);
                        }
                        clientSocket.shutdownOutput();
                    }
                    else if (params[0].equals("register")){
                        registry(params[1],params[2],"file");
                        //clientSocket.shutdownInput();
                    }
		    else if(params[0].equals("Delete")){
			Delete(params[1],params[2]);
		    }
                }
                clientSocket.shutdownInput();
            }
            catch (IOException e) {
                System.out.println("Exception caught when trying to listen on port or listening for a connection");
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
            finally{
                try{
                    clientSocket.close();
                }
                catch(IOException e){
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public List<String> lookup(String fileName) {
          return Util.searchInFile(fileName);
    }

    @Override
    public boolean registry(String loc,String portRequested,String type) {
        String[] locArray;
        if(type.equals("folder"))
            for(String result:Util.listFiles(loc))
                Util.appendDataToFile(result+", "+portRequested+"\n");

        if(type.equals("file")){
            Util.appendDataToFile(loc+", "+portRequested+"\n");
        }

        return false;
    }
	
   public void Delete(String fileName, String hostingClient){
	Util.DeleteSingleLineInFile(fileName,hostingClient);	
   }

}
