package com.p2p.thread_examples;

/**
 * Created by Ajay on 1/26/17.
 */
public class TwoThreadsExample implements  Runnable {
    static  float value = 10;
    static  boolean t1running = false;
    int valShared = 99;
    Thread t;
    public TwoThreadsExample() throws InterruptedException {
        if(t1running == false){
            t = new Thread(this,"t1Running");
            t.start();
        }
        while(!t1running){
            break;
        }
        if(t1running == true){
            t = new Thread(this,"t2 is running ");
            t.start();
        }



    }

    @Override
    public void run() {
        if(t.getName().equals("t1Running"))
            TwoThreadsExample.t1running = true;
        TwoThreadsExample.value = (float) (11+Math.random());
        this.valShared = this.valShared+this.valShared;
        System.out.println(t.getName()+TwoThreadsExample.value+"..."+this.valShared);

    }
}
