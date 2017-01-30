package cs550.pa1.servers.IndexServer;

/**
 * Created by Ajay on 1/28/17.
 */
public class Registry implements  Runnable {
    FileProcessor fileProcessor;
    String peerServerAddress;
    String fileName;

    public Registry(FileProcessor fileProcessor,String fileName, String peerServerAddress) {
        this.fileProcessor = fileProcessor;
        this.peerServerAddress = peerServerAddress;
        this.fileName = fileName;
        //new Thread(this, "registry").start();
    }

    @Override
    public void run() {
        try {
            fileProcessor.registry(fileName, peerServerAddress);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
