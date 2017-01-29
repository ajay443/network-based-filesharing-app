package cs550.pa1.servers.IndexServer;

import java.io.PrintWriter;

/**
 * Created by Ajay on 1/28/17.
 */
public class LookUp implements Runnable {
    FileProcessor fileProcessor;
    PrintWriter out;
    String fileName;


    public LookUp(FileProcessor fileProcessor, PrintWriter out, String fileName) {
        this.fileProcessor = fileProcessor;
        this.out = out;
        this.fileName = fileName;
        //new Thread(this,"Lookup").start();
    }

    @Override
    public void run() {
        try {// todo remove hard coded value
            fileProcessor.lookup(fileName, out);
            System.out.println("Inside Lookup");
            return;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
