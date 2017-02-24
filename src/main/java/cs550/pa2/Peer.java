package cs550.pa2;

/**
 *
 */
public interface Peer {

    void search();
    void download();
    void search(boolean searchInFileDatabase);
    void queryHit();
    void initConfig();
    void initListener();

    void initConfig(String hostName, int port);
}
