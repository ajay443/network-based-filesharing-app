/*
 * Copyright (C) 2017.  FileSharingSystem - https://github.com/ajayramesh23/FileSharingSystem
 * Programming Assignment from Professor Z.Lan
 * @author Ajay Ramesh
 * @author Chandra Kumar Basavaraj
 * Last Modified - 3/18/17 12:02 PM
 */

package cs550.pa3.helpers;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

public class PeerFiles {

    HashMap<String, PeerFile> filesMetaData;

    public PeerFiles(HashMap<String, PeerFile> filesMetaData) {
        this.filesMetaData = filesMetaData;
    }

    public PeerFiles() {
        filesMetaData = new HashMap<String, PeerFile>();
    }

    public HashMap<String, PeerFile> getFilesMetaData() {
        return filesMetaData;
    }

    public void setFilesMetaData(HashMap<String, PeerFile> filesMetaData) {
        this.filesMetaData = filesMetaData;
    }

    public void setVersion(String fileName, int version){
        this.filesMetaData.get(fileName).setVersion(version);
    }

    public void setLastUpdatedTime(String fileName, LocalDateTime time){
        this.filesMetaData.get(fileName).setLastUpdated(time);
    }

    public void add(PeerFile newFile){
        filesMetaData.put(newFile.getName() /*+ ":" + newFile.getFromAddress().address()*/, newFile);

    }

    public void remove(PeerFile toDelete){
        filesMetaData.remove(toDelete.getName() /*+ ":" + toDelete.getFromAddress().address()*/);
    }

    public PeerFile getFileMetadata(String fileName/*, Host fileHost*/){
        return filesMetaData.get(fileName /*+ ":" + fileHost.address()*/);

    }

    public boolean fileExists(String fileName){
        PeerFile file = null;
        file = filesMetaData.get(fileName);
        if(file == null)
            return false;
        else
            return true;

    }

    public boolean fileExistsAndValid(String fileName){
        PeerFile file = getFileMetadata(fileName);
        if(file == null) {
            return false;
        }
        else {
            if(file.checkIsStale()) return false;
            else return true;
        }
    }

    public boolean fileExistsAndValid(String fileName, String originServer){
        PeerFile file = getFileMetadata(fileName);
        if(file == null) {
            return false;
        }
        else {
            Host host = file.getFromAddress();
            String ip_port[] = originServer.split(":");
            if(ip_port[0].equals(host.getUrl()) && ip_port[1].equals(host.getPort())) {
                if (file.checkIsStale())
                    return false;
                else
                    return true;
            }
            else
                return false;
        }
    }

    public void updateFileMetadata(String fileName,int newVersion){
        PeerFile file = getFileMetadata(fileName);
        int oldVersion = file.getVersion();
        if(newVersion > oldVersion) file.setIsStale(true);
    }
}

