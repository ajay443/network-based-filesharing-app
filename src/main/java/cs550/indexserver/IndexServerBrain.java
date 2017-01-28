package cs550.indexserver;

/**
 * Created by Ajay on 1/27/17.
 */
public  class IndexServerBrain {
    int n;
    boolean valueSet = false;

    synchronized  int lookup() {
        while(!valueSet)
            try {
                wait();
            } catch(InterruptedException e) {
                System.out.println("InterruptedException caught");
            }
        System.out.println("Got: " + n);
        valueSet = false;
        notify();
        return n;
    }

    synchronized  void registry(String peerId,String  filename,int n) {
        while(valueSet)
            try {
                wait();
            } catch(InterruptedException e) {
                System.out.println("InterruptedException caught");
            }
        valueSet = true;
        this.n = n;
        //indexFile(String peerId,String  filename);
        System.out.println("Put: " + n);
        notify();
    }
}

/*public boolean indexFile(String peerId, String fileName){
    return false;
}*/
/*
class Q {
    int n;
    boolean valueSet = false;
    synchronized int get() {
        while(!valueSet)
            try {
                wait();
            } catch(InterruptedException e) {
                System.out.println("InterruptedException caught");
            }
        System.out.println("Got: " + n);
        valueSet = false;
        notify();
        return n;
    }
    synchronized void put(int n) {
        while(valueSet)
            try {
                wait();
            } catch(InterruptedException e) {
                System.out.println("InterruptedException caught");
            }
        this.n = n;
        valueSet = true;
        System.out.println("Put: " + n);
        notify();
    }
}*/
