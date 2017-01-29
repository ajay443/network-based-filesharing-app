package cs550.pa1.servers.IndexServer;

import cs550.pa1.helpers.Util;

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
            System.out.println("Socket at peer client from index server : "+ socket.getPort());
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                process(inputLine, out);
                //out.println(inputLine);
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void process(String inputLine,  PrintWriter out) throws IOException {
        String params[] = inputLine.split(" ");
        if(params[0].equals("lookup")){
            // comments
            try {
               LookUp lkup =  new LookUp(fileProcessor, out, params[1]);
                Thread lkup_thread = new Thread(lkup);
                lkup_thread.start();
                lkup_thread.join();
                System.out.println("Inside IndexServerThread");

                socket.shutdownOutput();
                return;
            }
            catch(Exception e){
                e.printStackTrace();
            }

        }else if (params[0].equals("register")) {
            try {
                Registry rgstr = new Registry(fileProcessor, params[1], params[2]);
                Thread rgstr_thread = new Thread(rgstr);
                rgstr_thread.start();
                rgstr_thread.join();
            }
            catch(Exception e){
                e.printStackTrace();
            }

        }else if(params[0].equals("delete")){
            for(String t : params){
                System.out.println(t + "\n");
            }
            Util.DeleteSingleLineInFile(params[1], params[2]);

        }
        else{
            System.out.println("Invalid Input");
        }

    }
}
