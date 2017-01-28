package cs550.indexserver;



/**
 * Created by Ajay on 1/27/17.
 */
// Producer
public class Registry implements Runnable {
    Processor processor;
    Registry(Processor processor) {
        this.processor = processor;
        new Thread(this, "Registry").start();
    }
    public void run() {
        try {
            this.processor.doit("registry");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}
