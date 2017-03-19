/*
 * Copyright (C) 2017.  FileSharingSystem - https://github.com/ajayramesh23/FileSharingSystem
 * Programming Assignment from Professor Z.Lan
 * @author Ajay Ramesh
 * @author Chandra Kumar Basavaraj
 * Last Modified - 3/15/17 6:44 PM
 */

package cs550.pa3.processor;

import cs550.pa3.helpers.*;

import javax.print.DocFlavor;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.*;
import java.nio.charset.Charset;

public class PeerImpl implements Peer {
        private static int messageID = 0;
        private static List<String> thrash;
        private ServerSocket serverSocket;
        private Thread clientThread;
        private Thread  serverThread;
        private Thread cleanUpThread;
        private Thread pullThread;
        private WatcherThread watchThread;
        private Socket new_socket;
        private HashMap seenMessages;
        private HashMap seenQueryHitMessages;
        private Set seenInvalidationHitMessages;
        private List<Host> neighbours;
        private Host host;
        //public  ArrayList<PeerFile> peerFiles;
        private PeerFiles myfiles;
        private PeerFiles downloadedFiles;


        public PeerImpl() {
                this.seenMessages = new HashMap<String, List<String>>();
                this.seenQueryHitMessages = new HashMap<String, List<String>>();
                this.neighbours = new ArrayList<Host>();
                this.seenInvalidationHitMessages = new HashSet();

                thrash = new ArrayList<String>();
                //peerFiles = new ArrayList<PeerFile>();
                //files.setFilesMetaData(peerFiles);
                myfiles = new PeerFiles();
                downloadedFiles = new PeerFiles();

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
                                out.println(Constants.QUERY + " " + query_id + " " + fileName + " " + host.address() + " " + Integer.toString(ttl));
                                out.close();
                                if (!seenMessages.containsKey(query_id)) {
                                        List addr = new ArrayList<String>();
                                        this.seenMessages.put(query_id, addr);
                                }
                        }
                } catch (ConnectException e){
                    Util.error("Peer Server is not running ....");
                }
                catch (IOException e) {
                        e.printStackTrace();
                }

        }

        @Override
        public void forwardQuery(String quer_id, String fileName, int ttl) {
                search(quer_id, fileName, ttl, true);
        }

        @Override
        public void download(String fileName, String host, int port) throws IOException {
                Socket peerClientSocket = null;
                try {
                        peerClientSocket = new Socket(host, port);
                        PrintWriter out = new PrintWriter(peerClientSocket.getOutputStream(), true);
                        //BufferedReader in = new BufferedReader(new InputStreamReader(peerClientSocket.getInputStream()));
                        InputStream in = peerClientSocket.getInputStream();
                        OutputStream fout = new FileOutputStream(Util.getValue(Constants.CACHED_FOLDER,Constants.PEER_PROPERTIES_FILE) + "/" + fileName);
                        out.println(Constants.DOWNLOAD + " " + fileName);
                        String message = "";
                        PrintWriter p = new PrintWriter(fileName, "UTF-8");

                        byte[] bytes = new byte[16 * 1024];

                        int count;
                        while ((count = in.read(bytes)) > 0) {
                                fout.write(bytes, 0, count);
                        }
                        //in.wait(2);
                    byte[] b = new byte[4];
                        in.read(b);
                        int version = ByteBuffer.wrap(b).getInt();
                        in.read(b);
                        int ttr = ByteBuffer.wrap(b).getInt();
                        in.read(bytes);
                        String origin = new String(bytes,"UTF-8");
                        String addr_params[] = origin.split(":");
                        Util.println("Version : " + Integer.toString(version) + " TTR : " + Integer.toString(ttr) + " Origin : " + origin);
                        registry(fileName, new Host(addr_params[0],Integer.parseInt(addr_params[1])),ttr,version);
                        p.close();

                } catch (Exception e) {
                        e.printStackTrace();
                } finally {
                        peerClientSocket.close();
                }

        }

        private void registry(String fileName, Host host, int ttr, int version) {
            // TODO when you download some thing from the remote peer, register it
            if(!downloadedFiles.fileExists(fileName))
                downloadedFiles.add(new PeerFile(false, fileName,ttr, host, version));
         }


        @Override
        public void returnQueryHit(String msgid, String fileName, String addr, int ttl, boolean isForward) {
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

                                out.println(Constants.QUERYHIT + " " + msgid + " " + fileName + " " + addr + " " + ttl);
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
                returnQueryHit(queryHit_id,fileName,addr,ttl,true);
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
                                if ((message = in.readLine()) != null)
                                        new Thread(new Runnable() {
                                                public void run() {
                                                        processInput(message, new_socket);
                                                }
                                        }).start();
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
                                                Util.println("Enter filename : \n");
                                                fileName = in.next();
                                                search(null, fileName, 0, false);
                                                break;
                                        case 2:
                                                Util.println("Enter filename : \n");
                                                fileName = in.next();
                                                Util.println("Enter Host name of the download server : \n");
                                                String host = in.next();
                                                Util.println("Enter port number of of the download server : \n");
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
                                                Host h = downloadedFiles.getFileMetadata(fileName).getFromAddress();
                                                download(fileName,h.getUrl(),h.getPort());
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
                System.out.printf("Peer address %s. \nMy Neighbours are \n", host.address());
                for (Host neighbour : neighbours) {
                        Util.print(neighbour.address());
                }
        }

        @Override
        public void initConfig(String hostName, int port) {
                host = new Host(hostName, port);
                readFromFile(Constants.PEER_PROPERTIES_FILE);
                displayPeerInfo();
                Util.createFolder(Util.getValue(Constants.MASTER_FOLDER,Constants.PEER_PROPERTIES_FILE));
                Util.createFolder(Util.getValue(Constants.CACHED_FOLDER,Constants.PEER_PROPERTIES_FILE));
                //System.out.println("After Create Folder");
                serverThread = new Thread() {
                        public void run() {
                                runPeerServer();
                        }
                };
                clientThread = new Thread() {
                        public void run(){
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
                watchThread = new WatcherThread(this,Util.getValue(Constants.MASTER_FOLDER,Constants.PEER_PROPERTIES_FILE));
                serverThread.start();
                clientThread.start();
                cleanUpThread.start();
                //pullThread.start();
                 watchThread.start();
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

        /**
         * Handle Broad Cast Event here
         * 1. Check if I have the latest file or not
         * 2. If not Issue Download Request
         */
        @Override
        public void  handleBroadCastEvents(String messageId, String changedFileName, int fileVersion, int ttl, boolean isForward) {
            Socket sock = null;
            if(!isForward){
                messageId = host.address() + "_" + Integer.toString(++messageID);
                ttl = 7;
            }
            for(Host h : neighbours){
                try {
                    sock = new Socket(h.getUrl(), h.getPort());
                    PrintWriter out = new PrintWriter(sock.getOutputStream(),true);
                    out.println(Constants.INVALIDATION + " " + messageId  + " " + changedFileName + " " + Integer.toString(fileVersion) + " " + Integer.toString(ttl));
                    sock.close();
                    if (!seenInvalidationHitMessages.contains(messageId)) {
                        this.seenInvalidationHitMessages.add(messageId);
                    }
                }
                catch(Exception e){
                    e.printStackTrace();
                    try{
                        sock.close();
                    }
                    catch(IOException ioe){
                        ioe.printStackTrace();
                    }
                }
            }

        }

        public void handleForwardBroadCastEvents(String messageId, String changedFileName, int fileVersion, int ttl){
        handleBroadCastEvents(messageId, changedFileName, fileVersion, ttl, true);
    }

        /**
         * 1. Create a Pull->thread and let that run in background when peerserver starts.
         * 2.
         */
        @Override
        public void runPullProcess() {
                Util.print("Pull Thread Started Running.");
                Pull pullEvent = new Pull(this);
                while(true){
                        pullEvent.trigger();
                        Util.sleep(Integer.parseInt(Util.getValue("pull.TTR")));

                }
        }

        private void processInput(String input, Socket socket) {
                Util.print("Received Message : " + input);
                String params[] = input.split(" ");
                // TODO - Make it simple to read by using Switch Case and Enum datatype
                if (params[0].equals(Constants.DOWNLOAD)) {
                        serveDownloadRequest(params[1], socket);
                } else if (params[0].equals(Constants.QUERY)) {
                        //DisplaySeenMessages(params[0]);
                        int ttl = Integer.valueOf(params[4]);
                        ttl = ttl - 1;

                        //not searching or forwarding already seen message
                        if (!seenMessages.containsKey(params[1]) && ttl > 0) {

                                List addresses = new ArrayList<String>();
                                addresses.add(params[3]);
                                this.seenMessages.put(params[1], addresses);
                                forwardQuery(params[1], params[2], ttl);
                                if (myfiles.fileExists(params[2]) || downloadedFiles.fileExistsAndValid(params[2]))
                                        returnQueryHit(params[1], params[2], host.address(), 7, false);
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
                                        System.out.printf("\n------------------------------------------------------------------ \n" +
                                                "File %s found at peer with port %s" +
                                                " \n------------------------------------------------------------------\n", params[2], params[3]);
                                } else {
                                        forwardQueryHit(params[1], params[2], params[3], ttl);
                                }
                        } else {
                                String msg_params[] = params[1].split("_");
                                if (msg_params[0].equals(host.address())) {
                                        System.out.printf("\n------------------------------------------------------------------ \n " +
                                                "File %s found at peer with port %s" +
                                                "\n------------------------------------------------------------------\n", params[2], params[3]);
                                }
                                List ports = (List) seenQueryHitMessages.get(params[1]);
                                if (!ports.contains(params[3])) {
                                        ports.add(params[3]);
                                }
                                Util.print("Not forwarding " + input);
                        }
                }
                else if (params[0].equals(Constants.INVALIDATION)) {
                    int ttl = Integer.valueOf(params[4]);
                    ttl = ttl - 1;

                    //not forwarding already seen message
                    if (!seenInvalidationHitMessages.contains(params[1]) && ttl > 0) {

                        handleForwardBroadCastEvents(params[1], params[2],Integer.parseInt(params[3]), ttl);
                        //Util.searchInCached(params[2],Integer.parseInt(params[3]),params[1].split("_")[0],true);
                        if(downloadedFiles.fileExistsAndValid(params[2],params[1].split("_")[0]))
                            downloadedFiles.updateFileMetadata(params[2],Integer.parseInt(params[3]));
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
                String neighbors[] = Util.getValue(Constants.PEER_NEIGHBORS, Constants.PEER_PROPERTIES_FILE).split(",");
                for (String neighbour: neighbors) {
                        String params[] = neighbour.split(":");
                        neighbours.add(new Host(params[0],Integer.parseInt(params[1])));
                }
        }

        public void pullFile(PeerFile f) {
            Socket peerClientSocket = null;
            try {
                Host from = f.getFromAddress();
                peerClientSocket = new Socket(from.getUrl(), from.getPort());
                PrintWriter out = new PrintWriter(peerClientSocket.getOutputStream(), true);
                InputStream in = peerClientSocket.getInputStream();
                String filePath = Util.getValue("cache.folderName") +"/"+ f.getName();
                OutputStream fileOutputStream = new FileOutputStream(filePath);
                out.println(Constants.DOWNLOAD + " " + f.getName());
                PrintWriter p = new PrintWriter(filePath, "UTF-8");
                byte[] bytes = new byte[16 * 1024];
                int count;
                while ((count = in.read(bytes)) > 0) {
                    fileOutputStream.write(bytes, 0, count);
                }
                p.close();
            }catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    peerClientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        public void handleWatcherThreadEvents(String eventType, String fileName){
            Util.println("Event : " + eventType + " file : " + fileName);
            if(eventType.equals("ENTRY_CREATE")){
                //add file to the peerFiles list
                PeerFile newFile = new PeerFile(true, fileName, 0, host,1);
                myfiles.add(newFile);
            }
            else if(eventType.equals("ENTRY_MODIFY")){
                //modify file metadata in peerFiles and send out INVALIDATION message to all neighbors
                PeerFile toModify = myfiles.getFileMetadata(fileName);
                int oldVersion = toModify.getVersion();
                Util.println("Old version : " + Integer.toString(oldVersion));
                myfiles.setVersion(fileName,oldVersion+1);
                myfiles.setLastUpdatedTime(fileName,LocalDateTime.now());
                Util.println("New version : " + myfiles.getFileMetadata(fileName).getVersion());

                handleBroadCastEvents(null,fileName,oldVersion+1,0,false);


            }
            else if(eventType.equals("ENTRY_DELETE")){
                //remove file from peerFiles list
                PeerFile toDelete = new PeerFile(true, fileName, 0, host,0);
                myfiles.remove(toDelete);
            }

        }

        public void serveDownloadRequest(String fileName, Socket socket){
            boolean myaddr = false;
            File sourceFile = null;
            if(myfiles.fileExists(fileName)) {
                sourceFile = new File(Util.getValue(Constants.MASTER_FOLDER, Constants.PEER_PROPERTIES_FILE) + "/" + fileName);
                myaddr = true;
            }
            else{
                sourceFile = new File(Util.getValue(Constants.CACHED_FOLDER, Constants.PEER_PROPERTIES_FILE) + "/" + fileName);
            }

            try(
                    InputStream fip = new FileInputStream(sourceFile);
                    OutputStream out = socket.getOutputStream();
            ) {   //int content = 0;
                byte b[] = new byte[16 * 1024];
                int count;
                while ((count = fip.read(b)) > 0) {
                    out.write(b, 0, count);
                }
                //fetch the file details from peerFiles object
                //out.wait(2);
                out.flush();

                //send file attributes : version, origin server, TTR and last modified time
                ByteBuffer bb = ByteBuffer.allocate(8);

                if(myaddr) {
                    bb.putInt(myfiles.getFileMetadata(fileName).getVersion());
                    byte[] b_i = bb.array();
                    out.write(b_i);

                    bb.putInt(Integer.parseInt(Util.getValue(Constants.PULL_TTR,Constants.PEER_PROPERTIES_FILE)));
                    b_i = bb.array();
                    out.write(b_i);

                    String origin = host.getUrl() + ":" + host.getPort();
                    byte[] b_o = origin.getBytes(Charset.forName("UTF-8"));
                    out.write(b_o);
                }
                else{
                    bb.putInt(downloadedFiles.getFileMetadata(fileName).getVersion());
                    byte[] b_i = bb.array();
                    out.write(b_i);

                    bb.putInt(Integer.parseInt(Util.getValue(Constants.PULL_TTR,Constants.PEER_PROPERTIES_FILE)));
                    b_i = bb.array();
                    out.write(b_i);

                    Host addr = downloadedFiles.getFileMetadata(fileName).getFromAddress();
                    String origin = addr.getUrl() + ":" + addr.getPort();
                    byte[] b_o = origin.getBytes(Charset.forName("UTF-8"));
                    out.write(b_o);
                }






            } catch(Exception e){
                e.printStackTrace();
            }
        }

        public PeerFiles getMyfiles(){
            return myfiles;
        }

        public PeerFiles getDownloadedFiles(){
            return downloadedFiles;
        }

        public void displayDownloadedFilesInfo(){
            HashMap<String,PeerFile> hm = downloadedFiles.getFilesMetaData();
            Util.println("Name | version | last update time | ");
            for (PeerFile file : hm.values()){
                Util.println(file.getName() + " " + file.getVersion() + " " + file.getLastUpdated().toString() + " " + file.getFromAddress().address() + " " + file.checkIsStale());
            }
        }

}
