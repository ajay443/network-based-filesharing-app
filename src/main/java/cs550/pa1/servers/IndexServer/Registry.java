package cs550.pa1.servers.IndexServer;

/**
 * Created by Ajay on 1/28/17.
 */
public class Registry implements  Runnable {
    FileProcessor fileProcessor;

    public Registry(FileProcessor fileProcessor) {
        this.fileProcessor = fileProcessor;
        new Thread(this, "registry").start();
    }

    @Override
    public void run() {
        try {
            fileProcessor.registry("test","test");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

/*
public class LookUp implements Runnable {
    FileProcessor fileProcessor;

    public LookUp(FileProcessor fileProcessor) {
        this.fileProcessor = fileProcessor;
        new Thread("Lookup").start();
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
*/
