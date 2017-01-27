package com.p2p.thread_examples;

import org.junit.Test;

/**
 * Created by Ajay on 1/26/17.
 */

public class ExampleThreadTest {

    @Test
    public void threadCreation() throws InterruptedException {
        ExampleThread t1 = new ExampleThread();
        (new Thread()).sleep(5000);
    }

}