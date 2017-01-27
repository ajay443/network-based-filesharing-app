package com.p2p.test;

import cs550.pa1.servers.IndexImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Ajay on 1/25/17.
 */
public class IndexImplTest {
    IndexImpl indexServer;
    @Before
    public void setUp() throws Exception {
        indexServer = new IndexImpl(8080);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void lookup() throws Exception {
        System.out.println("Lookup .... ");
        for(String r: indexServer.lookup("test1")){
            System.out.println(r);
        }
    }

    @Test
    public void registry() throws Exception {


        indexServer.registry("peers/8075/test1.txt","8075","file");
        indexServer.registry("peers/8085","8085","folder");
        indexServer.registry("peers/8095","8095","folder");

    }

    @Test
    public void askWhatClientNeeds1() throws Exception {

        // lookup
    }

    @Test
    public void askWhatClientNeeds2() throws Exception {

        // registry
    }



}