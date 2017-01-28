package cs550.indexserver;



/**
 * Created by Ajay on 1/27/17.
 */
// Producer
public class Registry implements Runnable {
    IndexServerBrain indexServerBrain;
    Registry(IndexServerBrain indexServerBrain) {
        this.indexServerBrain = indexServerBrain;
        new Thread(this, "Registry").start();
    }
    public void run() {
        int i = 0;
        while(true) {
            this.indexServerBrain.registry("p1","a1",i++);
        }
    }
}
