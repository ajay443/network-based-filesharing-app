/*
 * Copyright (C) 2017.  FileSharingSystem - https://github.com/ajayramesh23/FileSharingSystem
 * Programming Assignment from Professor Z.Lan
 * @author Ajay Ramesh
 * @author Chandra Kumar Basavaraj
 * Last Modified - 3/15/17 6:44 PM
 */

package cs550.pa3.processor;

import cs550.pa3.helpers.Host;
import cs550.pa3.helpers.PeerFile;
import cs550.pa3.helpers.Util;

/**
 * Created by Ajay on 3/13/17.
 */
public class Pull  extends  PeerImpl implements Event {
    PeerFile dummyFile1 = new PeerFile(true,"foo.txt");
    PeerFile dummyFile2 = new PeerFile(false,"cached.txt",5, new Host("localhost",5555));
    int TTR;

    public Pull() {

    }



    public void expiryTTL(){
        Util.print("expiryTTL");
        // read the cache folder file names
    }


    /**
     * Based on TTR in Infinte loop poll the Watch folder
     *
     */

    @Override
    public void trigger() {
        Util.print("Triggered");
        pull();

    }

    public void pull(){
        /**
         * Pull will do following functions
         * for(all files in cachedFolder){
         *     1.BroadCast Poll Message ()
         * }
         *
         */
        if(Util.getValue("pull.switch").equals("on")){
            TTR = Integer.parseInt(Util.getValue("pull.TTR"));
            for(PeerFile f : peerFiles){
                if(f.fileExpired()){
                    Util.print(f.getName()+"file "+f.getName()+" is Expired");
                    Util.print("Fetching latest file from origin server ");

                }else{
                    Util.print(f.getName()+" file is not expired , Last Updated "+f.getLastUpdated().toString());
                }
            }
        }else{
            //stop the thread
            System.out.print("Pull switch is off");
        }
    }


    public void init() {
        // todo
        Util.print("Pull process now initializing...");
        peerFiles.add(dummyFile1);
        peerFiles.add(dummyFile2);
        pull();

    }

}
