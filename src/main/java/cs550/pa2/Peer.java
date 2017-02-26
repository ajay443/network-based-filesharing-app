//package pa2;
import java.io.IOException;
/**
 *
 */
public interface Peer {

    void SendDownloadRequest(String fileName, String host, int port) throws IOException;
    void SendSearchQuery(String message_id, String fileName, int ttl, boolean forward);
    void ForwardSearchQuery(String message_id, String fileName, int ttl);
    void SendQueryHit(String msgid, String fileName, int port,int ttl,boolean forward);
    void ForwardQueryHit(String msg_id, String fileName, int port,int ttl);
    void initConfig();
    void DisplayPeerInfo();
    boolean SearchInMyFileDB(String fileName);
    void initConfig(String hostName, int id, int port, int[][] arr );
}
