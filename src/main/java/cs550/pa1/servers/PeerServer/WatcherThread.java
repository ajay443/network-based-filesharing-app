package cs550.pa1.servers.PeerServer;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

/**
 * Created by Ajay on 1/28/17.
 */
class WatcherThread extends Thread{

    private WatchService watcher;
    private Map<WatchKey, Path> keys;

    String hn;
    int port_server;
    int port_client;

    public WatcherThread(){
        try {
            this.watcher = FileSystems.getDefault().newWatchService();
            this.keys = new HashMap<WatchKey, Path>();
            this.hn = "localhost";
            this.port_server = 8080;
            this.port_client = 8100;
            Path dir = Paths.get(".");
            registerDirectory(dir);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public WatcherThread(String hn, int port_client, int port_server ){
        try {
            this.watcher = FileSystems.getDefault().newWatchService();
            this.keys = new HashMap<WatchKey, Path>();
            this.hn = hn;
            this.port_server = port_server;
            this.port_client = port_client;
            Path dir = Paths.get(".");
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

                // print out event
                System.out.format("%s: %s\n", event.kind().name(), child);

                // if directory is created, and watching recursively, then register it and its sub-directories
                if (kind == ENTRY_DELETE || kind == ENTRY_MODIFY) {
                    try{
                        //Socket sock = new Socket(Constants.INDEX_SERVER_HOST, Constants.INDEX_SERVER_PORT_DEFAULT);
                        Socket sock = new Socket( hn, port_server );
                        PrintWriter out = new PrintWriter(sock.getOutputStream(),true);

                        out.println("Delete " + name.toString() + port_client);
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