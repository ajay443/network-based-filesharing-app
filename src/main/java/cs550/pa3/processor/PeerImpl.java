/*
 * Copyright (C) 2017.  FileSharingSystem - https://github.com/ajayramesh23/FileSharingSystem
 * Programming Assignment from Professor Z.Lan
 * @author Ajay Ramesh
 * @author Chandra Kumar Basavaraj
 * Last Modified - 3/15/17 6:44 PM
 */

package cs550.pa3.processor;

import cs550.pa3.helpers.Constants;
import cs550.pa3.helpers.Host;
import cs550.pa3.helpers.PeerFile;
import cs550.pa3.helpers.PeerFiles;
import cs550.pa3.helpers.Util;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class PeerImpl implements Peer {

  private static int messageID = 0;
  private static List<String> thrash;
  private ServerSocket serverSocket;
  private Thread clientThread;
  private Thread serverThread;
  private Thread cleanUpThread;
  private Thread pullThread;
  private WatcherThread watchThread;
  private Socket new_socket;
  private HashMap seenMessages;
  private HashMap seenQueryHitMessages;
  private Set seenInvalidationHitMessages;
  private List<Host> neighbours;
  private Host host;
  private PeerFiles peerFiles;

  //todo - iniital file contents are not indexed by watch thread

  public PeerImpl() {
    this.seenMessages = new HashMap<String, List<String>>();
    this.seenQueryHitMessages = new HashMap<String, List<String>>();
    this.neighbours = new ArrayList<Host>();
    this.seenInvalidationHitMessages = new HashSet();

    thrash = new ArrayList<String>();
    //peerFiles = new ArrayList<PeerFile>();
    //files.setFilesMetaData(peerFiles);
    peerFiles = new PeerFiles();

  }

  @Override
  public void search(String query_id, String fileName, int ttl, boolean isForward) {
    Socket sock = null;
    if (!isForward) {
      query_id = host.address() + "_" + Integer.toString(++messageID);
      ttl = 7;
    }
    try {
      for (Host neighbour : neighbours) {
        //if(!query_id.contains(host.address())){
        sock = new Socket(neighbour.getUrl(), neighbour.getPort());
        PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
        out.println(
            Constants.QUERY + " " + query_id + " " + fileName + " " + host.address() + " " + Integer
                .toString(ttl));
        out.close();
        if (!seenMessages.containsKey(query_id)) {
          List addr = new ArrayList<String>();
          this.seenMessages.put(query_id, addr);
        }
      }
    } catch (ConnectException e) {
      Util.error("Peer Server is not running ....");
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  @Override
  public void forwardQuery(String quer_id, String fileName, int ttl) {
    search(quer_id, fileName, ttl, true);
  }

  @Override
  public void download(String fileName, String host, int port) throws IOException {
    downloadFile(Constants.DOWNLOAD_METADATA,getCacheFolderName(this.host)+ "/" + fileName+""+Constants.TEMP_FILE,host,port,fileName);
    downloadFile(Constants.DOWNLOAD,getCacheFolderName(this.host)+ "/" + fileName,host,port,fileName);
    //todo update the  PeerFiles database from the
  }

  @Override
  public void returnQueryHit(String msgid, String fileName, String addr, int ttl,
      boolean isForward) {
    //lookup
    Socket sock = null;
    List addresses = (List) seenMessages.get(msgid);
    //System.out.printf("Sending queryhit to %d peers",ports.size());
    Iterator i = addresses.iterator();
    if (!isForward) {
      ttl = 7;
    }

    while (i.hasNext()) {
      try {
        String toSendAddr = (String) i.next();//got concurrent modification error
        String addr_attrs[] = toSendAddr.split(":");
        sock = new Socket(addr_attrs[0], Integer.valueOf(addr_attrs[1]));
        PrintWriter out = new PrintWriter(sock.getOutputStream(), true);

        Util.print("File Information to send with QueyHit\n"+ Util.getJson(peerFiles.getFilesMetaData().get(fileName)));
        Util.print(queryHitMessage(msgid,fileName,addr,ttl));
        out.println(queryHitMessage(msgid,fileName,addr,ttl));

        // out.println(Constants.QUERYHIT + " " + msgid + " " + fileName + " " + addr + " " + ttl);
        out.close();

        if (!seenQueryHitMessages.containsKey(msgid)) {
          //I saw the message I am sending
          List p = new ArrayList<Integer>();
          //ports.add(params[3]);//dont have to forward queryhit to myself
          this.seenQueryHitMessages.put(msgid, p);
        }

        if (!thrash.contains(msgid)) {
          //Util.print();("pushing");
          thrash.add(msgid);
        }
        //else{
        //	Util.print();("2. Not forwarding queryhit message");
        //}
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

  }

  @Override
  public void forwardQueryHit(String queryHit_id, String fileName, String addr, int ttl) {
    returnQueryHit(queryHit_id, fileName, addr, ttl, true);
  }

  @Override
  public void runPeerServer() {
    Util.print("Peer Server running ... ");
    boolean listening = true;
    try {
      this.serverSocket = new ServerSocket(host.getPort());
      while (listening) {
        new_socket = this.serverSocket.accept();
        BufferedReader in = new BufferedReader(new InputStreamReader(new_socket.getInputStream()));
        String message;
        if ((message = in.readLine()) != null) {
          new Thread(new Runnable() {
            public void run() {
              processInput(message, new_socket);
            }
          }).start();
        }
      }
      Util.print("Peer Server is running ");
    } catch (BindException e) {
      Util.print("Peer Server address already in use, try again !");
    } catch (IOException e) {
      e.printStackTrace();
      System.err.println("Could not listen on port " + host.getUrl());
      System.exit(-1);
    }

  }

  @Override
  public void runPeerClient() {
    Util.print("Peer Client running ... ");
    String fileName = "";
    try {
      while (true) {
        Util.println(Constants.DISPLAY_MENU);
        Scanner in = new Scanner(System.in);
        int choice = in.nextInt();
        switch (choice) {
          case 1:
            Util.println("Enter filename : ");
            fileName = in.next();
            search(null, fileName, 0, false);
            break;
          case 2:
            Util.println("Enter filename : ");
            fileName = in.next();
            Util.println("Enter Host name of the download server : ");
            String host = in.next();
            Util.println("Enter port number of of the download server : ");
            int hostPort = in.nextInt();
            download(fileName, host, hostPort);
            break;
          case 3: // TODO remove all other choices
            displaySeenMessages(Constants.QUERY);
            break;
          case 4:
            displaySeenMessages(Constants.QUERYHIT);
            break;
          case 5:
            displayDownloadedFilesInfo();
            break;
          case 6:
            Util.println("Enter filename : \n");
            fileName = in.next();
            Host h = peerFiles.getFileMetadata(fileName).getFromAddress();
            download(fileName, h.getUrl(), h.getPort());
            break;
          case 7:
            System.exit(0);
          default:
            System.exit(0);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  @Override
  public void displayPeerInfo() {
    Util.print("Peer address "+host.address());
    Util.print("Peer Neighbours  ");
    for (Host neighbour : neighbours) {
      Util.print(neighbour.address());
    }
  }

  @Override
  public void initConfig(String hostName, int port) {
    host = new Host(hostName, port);
    createFolders(host);
    readFromFile(Constants.CONFIG_FILE_PREFIX+port+Constants.PEER_PROPERTIES_FILE);
    displayPeerInfo();
    serverThread = new Thread() {
      public void run() {
        runPeerServer();
      }
    };
    clientThread = new Thread() {
      public void run() {
        runPeerClient();
      }
    };
    cleanUpThread = new Thread() {
      public void run() {
        cleanUpSeenMessages();
      }
    };
    pullThread = new Thread() {
      public void run() {
        runPullProcess();
      }
    };
    watchThread = new WatcherThread(this, getMasterFolderName(host));
    serverThread.start();
    clientThread.start();
    cleanUpThread.start();
    //pullThread.start();
    watchThread.start();
  }

  @Override
  public void handleBroadCastEvents(String messageId, String changedFileName, int fileVersion,
      int ttl, boolean isForward) {
    Socket sock = null;
    if (!isForward) {
      messageId = host.address() + "_" + Integer.toString(++messageID);
      ttl = 7;
    }
    for (Host h : neighbours) {
      try {
        sock = new Socket(h.getUrl(), h.getPort());
        PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
        out.println(Constants.INVALIDATION + " " + messageId + " " + changedFileName + " " + Integer
            .toString(fileVersion) + " " + Integer.toString(ttl));
        sock.close();
        if (!seenInvalidationHitMessages.contains(messageId)) {
          this.seenInvalidationHitMessages.add(messageId);
        }
      } catch (Exception e) {
        e.printStackTrace();
        try {
          sock.close();
        } catch (IOException ioe) {
          ioe.printStackTrace();
        }
      }
    }

  }

  @Override
  public void runPullProcess() {
    Util.print("Pull Thread Started Running.");
    Pull pullEvent = new Pull(this);
    while (true) {
      pullEvent.trigger();
      Util.sleep(Integer.parseInt(Util.getValue("pull.TTR")));

    }
  }

  public void handleForwardBroadCastEvents(String messageId, String changedFileName,
      int fileVersion, int ttl) {
    handleBroadCastEvents(messageId, changedFileName, fileVersion, ttl, true);
  }

  private void processInput(String input, Socket socket) {
    Util.print("Received Message : " + input);
    String params[] = input.split(" ");
    // TODO - Make it simple to read by using Switch Case and Enum datatype
    if (params[0].equals(Constants.DOWNLOAD) || params[0].equals(Constants.DOWNLOAD_METADATA) ) {
      Util.print("Serving request of the type = "+params[0]);
      serveDownloadRequest(params[0], params[1], socket);
    } else if (params[0].equals(Constants.QUERY)) {
      int ttl = Integer.valueOf(params[4]);
      ttl = ttl - 1;

      //not searching or forwarding already seen message
      if (!seenMessages.containsKey(params[1]) && ttl > 0) {

        List addresses = new ArrayList<String>();
        addresses.add(params[3]);
        this.seenMessages.put(params[1], addresses);
        forwardQuery(params[1], params[2], ttl);
        if (peerFiles.fileExists(params[2])) {
          returnQueryHit(params[1], params[2], host.address(), 7, false);
        }
      } else {
        List ports = (List) seenMessages.get(params[1]);
        if (!ports.contains(params[3])) {
          ports.add(params[3]);
        }
        Util.print("Not forwarding " + input);
      }
    } else if (params[0].equals(Constants.QUERYHIT)) {
      //DisplaySeenMessages(params[0]);
      int ttl = Integer.valueOf(params[4]);
      ttl = ttl - 1;

      //Not forwarding already seen query hit messages
      if (!seenQueryHitMessages.containsKey(params[1]) && ttl > 0) {
        List addr = new ArrayList<String>();
        addr.add(params[3]);
        this.seenQueryHitMessages.put(params[1], addr);

        String msg_params[] = params[1].split("_");
        if (msg_params[0].equals(host.address())) {
          System.out
              .printf("\n------------------------------------------------------------------ \n" +
                      "File %s found at peer with port %s" +
                      " \n------------------------------------------------------------------\n",
                  params[2], params[3]);
        } else {
          forwardQueryHit(params[1], params[2], params[3], ttl);
        }
      } else {
        String msg_params[] = params[1].split("_");
        if (msg_params[0].equals(host.address())) {
          System.out
              .printf("\n------------------------------------------------------------------ \n " +
                      "File %s found at peer with port %s" +
                      "\n------------------------------------------------------------------\n",
                  params[2], params[3]);
        }
        List ports = (List) seenQueryHitMessages.get(params[1]);
        if (!ports.contains(params[3])) {
          ports.add(params[3]);
        }
        Util.print("Not forwarding " + input);
      }
    } else if (params[0].equals(Constants.INVALIDATION)) {
      int ttl = Integer.valueOf(params[4]);
      ttl = ttl - 1;

      //not forwarding already seen message
      if (!seenInvalidationHitMessages.contains(params[1]) && ttl > 0) {

        handleForwardBroadCastEvents(params[1], params[2], Integer.parseInt(params[3]), ttl);
        //Util.searchInCached(params[2],Integer.parseInt(params[3]),params[1].split("_")[0],true);
        if (peerFiles.fileExistsAndValid(params[2], params[1].split("_")[0])) {
          peerFiles.updateFileMetadata(params[2], Integer.parseInt(params[3]));
        }
      } else {
        Util.print("Not forwarding " + input);
      }
    }
  }

  public void displaySeenMessages(String type) {
    Util.print("Displaying seen " + type + " messages");
    Set set = null;
    if (type.equals(Constants.QUERY)) {
      set = seenMessages.entrySet();
    } else {
      set = seenQueryHitMessages.entrySet();
    }
    Iterator i = set.iterator();
    while (i.hasNext()) {
      Map.Entry entry = (Map.Entry) i.next();
      Util.print(entry.getKey() + ":" + entry.getValue());
    }
  }

  void readFromFile(String path) {

    String neighbors[] = new String[0];
    try {
      neighbors = Util.getValue(Constants.PEER_NEIGHBORS, new FileInputStream(path))
          .split(",");
    } catch (FileNotFoundException e) {
      Util.error("Peer Configuration is not present\nCreate  configuration "
          + "\nFileName should be: " + path +"\n\nContents of the file\n\n"
          + "peer.host=localhost\npeer.port=4000\npeer.neighbour=localhost:5000,123.34.3.3:8080");

    }
    for (String neighbour : neighbors) {
      String params[] = neighbour.split(":");
      neighbours.add(new Host(params[0], Integer.parseInt(params[1])));
    }
  }

  public void pullFile(PeerFile f) {
    Socket peerClientSocket = null;
    try {
      Host from = f.getFromAddress();
      peerClientSocket = new Socket(from.getUrl(), from.getPort());
      PrintWriter out = new PrintWriter(peerClientSocket.getOutputStream(), true);
      InputStream in = peerClientSocket.getInputStream();
      String filePath = Util.getValue("cache.folderName") + "/" + f.getName();
      OutputStream fileOutputStream = new FileOutputStream(filePath);
      out.println(Constants.DOWNLOAD + " " + f.getName());
      PrintWriter p = new PrintWriter(filePath, "UTF-8");
      byte[] bytes = new byte[16 * 1024];
      int count;
      while ((count = in.read(bytes)) > 0) {
        fileOutputStream.write(bytes, 0, count);
      }
      p.close();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        peerClientSocket.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

  }

  public void handleWatcherThreadEvents(String eventType, String fileName) {
    Util.print("Event = " + eventType + " file = " + fileName);
    if (eventType.equals("ENTRY_CREATE")) {
      peerFiles.getFilesMetaData().put(fileName,new PeerFile(1,true, fileName, 3600, host,false,LocalDateTime.now()));
      Util.print(Util.getJson(peerFiles));
    } else if (eventType.equals("ENTRY_MODIFY")) {
      PeerFile fileModified = peerFiles.getFilesMetaData().get(fileName);
      fileModified.setVersion(fileModified.getVersion()+1);
      fileModified.setLastUpdated(LocalDateTime.now());
      peerFiles.getFilesMetaData().remove(fileName);
      peerFiles.getFilesMetaData().put(fileName,fileModified);
      Util.print(Util.getJson(fileModified));
      handleBroadCastEvents(null, fileName,   fileModified.getVersion(), 0, false);
    } else if (eventType.equals("ENTRY_DELETE")) {
      peerFiles.getFilesMetaData().remove(fileName);
    }
  }

  public void serveDownloadRequest(String taskType, String fileName, Socket socket) {
    /**
     * 1.Search in peerFiles
     * 2.Check filename.stale == true if true return "Invalid Message"
     * 3.If above condition fails, then send the file
     *  3.a Send File MetaData
     *  3.b Send FIle content
     */
    Util.print(fileName);
    if (isPeerFileOutdated(fileName)) {
       Util.print("File is outdated");
      // todo send invalid message
      return;
    }
    switch (taskType){
      case Constants.DOWNLOAD:
        sendFileData(fileName,socket);
        break;
      case Constants.DOWNLOAD_METADATA:
        sendFileMetadata(fileName,socket);
        break;
    }
  }

  private void sendFileData(String fileName, Socket socket) {
    Util.print("Sending File Data");
    try (
        InputStream fip = new FileInputStream(getFilePath(fileName));
        OutputStream out = socket.getOutputStream();
    ) {
      byte b[] = new byte[16 * 1024];
      int count;
      while ((count = fip.read(b)) > 0) {
        out.write(b, 0, count);
      }
      out.flush();
    } catch (FileNotFoundException e) {
      Util.error("File is not present below locations: \n" + getCacheFolderName(this.host) + "\n"
          + getMasterFolderName(this.host));
      return;
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void sendFileMetadata(String fileName, Socket socket) {
    Util.print("Sending Meta Data.");
    try (
        OutputStream out = socket.getOutputStream();
    ) {
      InputStream fip = new ByteArrayInputStream(Util.getJson(peerFiles.getFileMetadata(fileName)).getBytes(StandardCharsets.UTF_8));
     //todo remove this commented line -> //InputStream fip = new ByteArrayInputStream(("{\"name\":\"ajayramesh-testing\"}").getBytes(StandardCharsets.UTF_8));
      byte b[] = new byte[16 * 1024];
      int count;
      while ((count = fip.read(b)) > 0) {
        out.write(b, 0, count);
      }
      out.flush();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private boolean isPeerFileOutdated(String fileName) {
    return peerFiles.getFilesMetaData().get(fileName).checkIsStale();
  }

  private String getFilePath(String fileName) {
    if(peerFiles.getFilesMetaData().get(fileName).isOriginal())
      return  getMasterFolderName(this.host) + "/" + fileName;
    return getCacheFolderName(this.host) + "/" + fileName;
  }

  public void displayDownloadedFilesInfo() {
    HashMap<String, PeerFile> hm = peerFiles.getFilesMetaData();
    Util.println("Name | version | last update time | ");
    for (PeerFile file : hm.values()) {
      Util.println(
          file.getName() + " " + file.getVersion() + " " + file.getLastUpdated().toString() + " "
              + file.getFromAddress().address() + " " + file.checkIsStale());
    }
  }

  public  String queryHitMessage(String messageID, String fileName, String address, int ttl) {
    return
        Constants.QUERYHIT + " "
            + messageID + " " +
            fileName + " " +
            address + " " +
            ttl + " " +
            Util.getJson(peerFiles.getFilesMetaData().get(fileName));
  }

  public void cleanUpSeenMessages() {
    while (true) {
      try {
        Thread.sleep(5000);
        for (String qid : thrash) {
          Util.print("Removing " + qid);
          seenMessages.remove(qid);
          seenQueryHitMessages.remove(qid);
        }
        thrash.clear();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  private String getMasterFolderName(Host host){
    return  Util.getValue(Constants.MASTER_FOLDER, Constants.PEER_PROPERTIES_FILE) + "_" + host.getHashCode();
  }

  private String getCacheFolderName(Host host){
    return  Util.getValue(Constants.CACHED_FOLDER, Constants.PEER_PROPERTIES_FILE) + "_" + host.getHashCode();
  }

  private void createFolders(Host host) {
    Util.createFolder(getMasterFolderName(host));
    Util.createFolder(getCacheFolderName(host));
  }

  /**
   * 1.Download the file PeerServer
   *  1.a) create a temp file in cache fileName_temp.json nd read the buffer content from server
   */
  private void downloadFile(String taskType,String fileName, String host, int port, String fname) {
    Socket peerClientSocket = null;
    try {
      peerClientSocket = new Socket(host, port);
      PrintWriter out = new PrintWriter(peerClientSocket.getOutputStream(), true);
      InputStream in = peerClientSocket.getInputStream();
      OutputStream fout = new FileOutputStream(fileName);
      out.println(taskType + " " + fname);
      String message = "";
      PrintWriter p = new PrintWriter(fileName, "UTF-8");
      byte[] bytes = new byte[16 * 1024];

      int count;
      while ((count = in.read(bytes)) > 0) {
        fout.write(bytes, 0, count);
      }
      p.close();

    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        peerClientSocket.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

  }

  private void updatePeerFilesList(String fileName) {
    Util.print("File Name for json "+fileName);
    //todo
    /**
     * read the content from json
     * store it to the meta data
     */
  }

}
