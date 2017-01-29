package cs550.pa1.servers.IndexServer;

import java.io.PrintWriter;

/**
 * Created by Ajay on 1/28/17.
 */
public class LookUp implements Runnable {
    FileProcessor fileProcessor;
    PrintWriter out;


    public LookUp(FileProcessor fileProcessor, PrintWriter out) {
        this.fileProcessor = fileProcessor;
        this.out = out;
        new Thread(this,"Lookup").start();
    }

    @Override
    public void run() {
        try {
            fileProcessor.lookup("test", out);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
