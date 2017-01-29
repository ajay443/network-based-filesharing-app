package cs550.pa1.servers.IndexServer;


import java.io.IOException;
import java.net.ServerSocket;
import java.util.Scanner;

/**
 * Created by Ajay on 1/27/17.
 */
public class IndexServerMain {

    public static void main(String[] args) throws IOException {
        System.out.print("Enter Port Address : ");
        Scanner io = new Scanner(System.in);
        int port = io.nextInt();
        boolean listening = true;
        FileProcessor fileProcessor = new FileProcessor();

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (listening) {
                System.out.println("Index Server is running ");
                new IndexServerThread(serverSocket.accept(),fileProcessor).start();
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + port);
            System.exit(-1);
        }
    }
}
