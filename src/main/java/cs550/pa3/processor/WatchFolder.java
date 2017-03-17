/*
 * Copyright (C) 2017.  FileSharingSystem - https://github.com/ajayramesh23/FileSharingSystem
 * Programming Assignment from Professor Z.Lan
 * @author Ajay Ramesh
 * @author Chandra Kumar Basavaraj
 * Last Modified - 3/15/17 6:44 PM
 */

package cs550.pa3.processor;

/**
 * Created by Ajay on 3/13/17.
 */
public class WatchFolder {
     /**
     * WatchFolder()
     * Now we need watch the folder
     * If master folder contents change--> Trigger Update Event
     * TODO - Chandra
     */

    PeerImpl observer;
    String folderName;

    public WatchFolder(PeerImpl watchingPeer,String folder) {
        observer = watchingPeer;
        folderName = folder;
        new WatcherThread(observer,folderName).run();
    }

    public void checkUpdate(){

    }
}
