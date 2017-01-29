package cs550.pa1.servers.IndexServer;

/**
 * Created by Ajay on 1/28/17.
 */
public class Registry implements  Runnable {
    FileProcessor fileProcessor;
    String peerServerID;
    String fileName;

    public Registry(FileProcessor fileProcessor,String fileName, String peerServerID) {
        this.fileProcessor = fileProcessor;
        this.peerServerID = peerServerID;
        this.fileName = fileName;
        //new Thread(this, "registry").start();
    }

    @Override
    public void run() {
        try {
            fileProcessor.registry(fileName, peerServerID);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
