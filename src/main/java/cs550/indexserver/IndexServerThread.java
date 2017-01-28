package cs550.indexserver;

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

    public IndexServerThread(Socket socket) {
        super("IndexServerThread");
        this.socket = socket;
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
                process(inputLine);
                out.println(inputLine);
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void process(String inputLine) {


    }
}
