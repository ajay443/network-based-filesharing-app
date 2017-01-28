package cs550.indexserver;


import java.io.IOException;
import java.net.ServerSocket;
import java.util.Scanner;

/**
 * Created by Ajay on 1/27/17.
 */
public class IndexServerTest {

    public static void main(String[] args) throws IOException {
        System.out.print("Enter Port Address : ");
        Scanner io = new Scanner(System.in);
        int port = io.nextInt();
        boolean listening = true;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (listening) {
                System.out.print("Index Server is running ");
                new IndexServerThread(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + port);
            System.exit(-1);
        }
    }
}
