package com.p2p.fileDownload;

import java.io.*;
import java.net.Socket;

/**
 *
 * http://stackoverflow.com/questions/9520911/java-sending-and-receiving-file-byte-over-sockets
 * Created by Ajay on 1/26/17.
 */
public class Client {
    public static void main(String[] args) throws IOException {
        Socket socket = null;
        String host = "127.0.0.1";

        socket = new Socket(host, 4444);

        File file = new File("peers/file1.txt");
        // Get the size of the file
        long length = file.length();
        byte[] bytes = new byte[16 * 1024];
        InputStream in = new FileInputStream(file);
        OutputStream out = socket.getOutputStream();

        int count;
        while ((count = in.read(bytes)) > 0) {
            out.write(bytes, 0, count);
        }

        out.close();
        in.close();
        socket.close();
    }
}