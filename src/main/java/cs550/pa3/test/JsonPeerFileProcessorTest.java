/*
 * Copyright (C) 2017.  FileSharingSystem - https://github.com/ajayramesh23/FileSharingSystem
 * Programming Assignment from Professor Z.Lan
 * @author Ajay Ramesh
 * @author Chandra Kumar Basavaraj
 * Last Modified - 3/17/17 8:39 PM
 */

package cs550.pa3.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import cs550.pa3.helpers.Host;
import cs550.pa3.helpers.PeerFile;
import cs550.pa3.helpers.PeerFiles;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class JsonPeerFileProcessorTest {
    public static void main(String[] args) throws Exception {
        List<PeerFile> files = new ArrayList<PeerFile>();
        PeerFile file = new PeerFile(false,"mountain.jpg",1,new Host("localhost",9999));
        files.add(file);
        file = new PeerFile(false,"mountain2.jpg",1, new Host("localhost",9999));
        files.add(file);
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        String json = mapper.writeValueAsString(files);
        //System.out.println(json);
        String content = new String(Files.readAllBytes(Paths.get("PeerDownloads/Cache/files.metadata.json")));
        // Util.error(content);
        PeerFile newFile = mapper.readValue(content, PeerFile.class);
        System.out.println(newFile.getLastUpdated().toString());
        PeerFiles p = new PeerFiles(files);
        json = mapper.writeValueAsString(p);
        System.out.println(json);
        content = new String(Files.readAllBytes(Paths.get("PeerDownloads/Master/files.metadata.json")));
        PeerFiles fs = mapper.readValue(content, PeerFiles.class);

    }
}

