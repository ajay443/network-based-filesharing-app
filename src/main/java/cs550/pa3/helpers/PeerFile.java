/*
 * Copyright (C) 2017.  FileSharingSystem - https://github.com/ajayramesh23/FileSharingSystem
 * Programming Assignment from Professor Z.Lan
 * @author Ajay Ramesh
 * @author Chandra Kumar Basavaraj
 * Last Modified - 3/15/17 6:44 PM
 */

package cs550.pa3.helpers;

import java.time.LocalDateTime;

/**
 * Created by Ajay on 3/13/17.
 */
public class PeerFile {
    int version;
    boolean original;
    String name;
    int TTR;
    Host fromAddress;
    LocalDateTime lastUpdated;

    public PeerFile(boolean original, String name) {
        this.original = original;
        this.name = name;
        this.TTR = -1;
        this.fromAddress = null; // TODO change to make peer server address .
        this.lastUpdated = LocalDateTime.now();

    }

    public PeerFile(boolean original, String name, int TTR, Host address) {
        this.original = original;
        this.name = name;
        this.TTR = TTR;
        this.fromAddress = address;
        this.lastUpdated = LocalDateTime.now();
    }

    public boolean isOriginal() {
        return original;
    }

    public String getName() {
        return name;
    }

    public int getTTR() {
        return TTR;
    }

    public Host getFromAddress() {
        return fromAddress;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }
    // TODO write unit test
    public boolean fileExpired(){
        if(!original){
            return LocalDateTime.now().isBefore(lastUpdated.plusSeconds(TTR));
        }else{
            return false;
        }

    }
}
