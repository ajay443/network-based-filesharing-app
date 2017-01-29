package cs550.pa1.servers.IndexServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Ajay on 1/28/17.
 */
public class IndexServerThread extends  Thread{
    private Socket socket = null;
    FileProcessor fileProcessor;
    public IndexServerThread(Socket socket,FileProcessor fileProcessor) {
        super("IndexServerThread");
        this.socket = socket;
        this.fileProcessor = fileProcessor;
    }

    public void run() {
        try (
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                socket.getInputStream()));
        ) {

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                process(inputLine, out);
                out.println(inputLine);
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void process(String inputLine,  PrintWriter out) throws IOException {
        String params[] = inputLine.split(" ");
        if(params[0].equals("lookup")){
            new LookUp(fileProcessor,out);
            socket.shutdownOutput();
        }else if (params[0].equals("register")) {
            new Registry(fileProcessor);
        }else{
            System.out.println("Invalid Input");
        }

    }



}
