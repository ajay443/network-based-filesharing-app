package com.p2p.thread_examples;

/**
 * Created by Ajay on 1/26/17.
 */
public class ExampleThread  implements  Runnable{
    Thread t;
    ExampleThread() {
        t = new Thread(this,"Example Thread ");
        t.start();

    }

    @Override
    public void run() {
        try {
            for(int i = 5; i > 0; i--) {
                System.out.println("Child Thread: " + i);
                Thread.sleep(500);
            }
        } catch (InterruptedException e) {
            System.out.println("Child interrupted.");
        }
        System.out.println("Exiting child thread.");
    }



}
