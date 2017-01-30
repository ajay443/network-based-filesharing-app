package cs550.pa1.servers.PeerServer;

import cs550.pa1.helpers.Constants;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

/**
 * Created by Ajay on 1/28/17.
 */
class WatcherThread extends Thread{

    private WatchService watcher;
    private Map<WatchKey, Path> keys;

    String hostName;
    int indexServerPort;
    int peerServerPort;

    public WatcherThread(){
        try {
            this.watcher = FileSystems.getDefault().newWatchService();
            this.keys = new HashMap<WatchKey, Path>();
            this.hostName = Constants.INDEX_SERVER_HOST;
            this.indexServerPort = Constants.INDEX_SERVER_PORT_DEFAULT;
            this.peerServerPort = Constants.SERVER_PORT_DEFAULT;
            Path dir = Paths.get(Constants.PEER_FOLDER_PREFIX + this.peerServerPort);
            registerDirectory(dir);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public WatcherThread(String hostName, int indexServerPort, int peerServerPort ){
        try {
            this.watcher = FileSystems.getDefault().newWatchService();
            this.keys = new HashMap<WatchKey, Path>();
            this.hostName = hostName;
            this.indexServerPort = indexServerPort;
            this.peerServerPort = peerServerPort;
            Path dir = Paths.get(Constants.PEER_FOLDER_PREFIX + this.peerServerPort);
            registerDirectory(dir);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
    private void registerDirectory(Path dir) throws IOException
    {

        WatchKey key = dir.register(watcher, /*ENTRY_CREATE,*/ ENTRY_DELETE, ENTRY_MODIFY);
        keys.put(key, dir);
    }

    public void run(){
        for (;;) {

            // wait for key to be signalled
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                return;
            }

            Path dir = keys.get(key);
            if (dir == null) {
                System.err.println("WatchKey not recognized!!");
                continue;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                @SuppressWarnings("rawtypes")
                WatchEvent.Kind kind = event.kind();

                // Context for directory entry event is the file name of entry
                @SuppressWarnings("unchecked")
                Path name = ((WatchEvent<Path>)event).context();
                Path child = dir.resolve(name);

                // Debug lines
                /*System.out.println(event.kind().name()+"\n");
                System.out.println(child+"\n");
                */

                Socket sock = null;
                // if directory is created, and watching recursively, then register it and its sub-directories
                if (kind == ENTRY_DELETE )  {
                    try{
                        sock = new Socket( hostName, indexServerPort );
                        PrintWriter out = new PrintWriter(sock.getOutputStream(),true);
                        out.println("delete " + name.toString() + " " + peerServerPort);
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                }else if( kind == ENTRY_CREATE || kind == ENTRY_MODIFY){
                    try{
                        sock = new Socket( hostName, indexServerPort );
                        PrintWriter out = new PrintWriter(sock.getOutputStream(),true);
                        out.println("register " + name.toString() + " " + hostName+":"+peerServerPort);
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
            // reset key and remove from set if directory no longer accessible
            boolean valid = key.reset();
            if (!valid) {
                keys.remove(key);

                // all directories are inaccessible
                if (keys.isEmpty()) {
                    break;
                }
            }
        }
    }
}