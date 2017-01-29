package cs550.pa1.servers.IndexServer;

/**
 * Created by Ajay on 1/28/17.
 */
public class LookUp implements Runnable {
    FileProcessor fileProcessor;

    public LookUp(FileProcessor fileProcessor) {
        this.fileProcessor = fileProcessor;
        new Thread(this,"Lookup").start();
    }

    @Override
    public void run() {
        try {
            System.out.print("LookUP");
            fileProcessor.lookup("test");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
