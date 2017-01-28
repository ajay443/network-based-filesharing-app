package cs550.indexserver;

/**
 * Created by Ajay on 1/27/17.
 */
public class LookUp implements Runnable {
    Processor processor;
    public LookUp(Processor processor) {
        this.processor = processor;
        new Thread(this, "lookUp").start();

    }

    public void run() {
        try {
            this.processor.doit("lookup");
        } catch (InterruptedException e) {
            e.printStackTrace();
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