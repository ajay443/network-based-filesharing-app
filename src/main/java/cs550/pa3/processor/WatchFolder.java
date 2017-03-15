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
     * If master folder contents --> Trigger Update Event
     * TODO - Chandra
     */

    Push observer;

    public WatchFolder(Push push) {
        observer = push;

        new WatcherThread().run();
    }

    public void checkUpdate(){

    }
}
