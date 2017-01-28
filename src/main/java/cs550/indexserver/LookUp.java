package cs550.indexserver;

/**
 * Created by Ajay on 1/27/17.
 */
public class LookUp implements Runnable {
    IndexServerBrain indexServerBrain;
    public LookUp(IndexServerBrain indexServerBrain) {
        this.indexServerBrain = indexServerBrain;
        new Thread(this, "LookUp").start();
    }

    public void run() {
        while(true) {
            this.indexServerBrain.lookup();
        }
    }

}

/*
class Consumer implements Runnable {
    Q q;
    Consumer(Q q) {
        this.q = q;
        new Thread(this, "Consumer").start();
    }
    public void run() {
        while(true) {
            q.get();
        }
    }
}*/