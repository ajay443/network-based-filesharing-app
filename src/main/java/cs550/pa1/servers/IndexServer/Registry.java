package cs550.pa1.servers.IndexServer;

/**
 * Created by Ajay on 1/28/17.
 */
public class Registry implements  Runnable {
    FileProcessor fileProcessor;
    String peerID;
    String fileName;

    public Registry(FileProcessor fileProcessor,String peerID, String fileName) {
        this.fileProcessor = fileProcessor;
        this.peerID = peerID;
        this.fileName = fileName;
        new Thread(this, "registry").start();
    }

    @Override
    public void run() {
        try {
            fileProcessor.registry(peerID,fileName);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
