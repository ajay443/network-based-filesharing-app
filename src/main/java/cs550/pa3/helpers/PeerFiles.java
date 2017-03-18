/*
 * Copyright (C) 2017.  FileSharingSystem - https://github.com/ajayramesh23/FileSharingSystem
 * Programming Assignment from Professor Z.Lan
 * @author Ajay Ramesh
 * @author Chandra Kumar Basavaraj
 * Last Modified - 3/18/17 12:02 PM
 */

package cs550.pa3.helpers;

import java.util.List;

public class PeerFiles {

    List<PeerFile> filesMetaData;

    public PeerFiles(List<PeerFile> filesMetaData) {
        this.filesMetaData = filesMetaData;
    }

    public PeerFiles() {
    }

    public List<PeerFile> getFilesMetaData() {
        return filesMetaData;
    }

    public void setFilesMetaData(List<PeerFile> filesMetaData) {
        this.filesMetaData = filesMetaData;
    }
}

