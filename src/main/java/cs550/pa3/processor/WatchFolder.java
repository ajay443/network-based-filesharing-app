package cs550.pa3.processor;

/**
 * Created by Ajay on 3/13/17.
 */
public class WatchFolder {
    /**
     * WatchFolder()
     * Now we need watch the folder
     * If master folder contents --> Trigger Update Event
     * TODO - Chandra
     */

    Push observer;

    public WatchFolder(Push push) {
        observer = push;

        new WatcherThread().run();
    }

    public void checkUpdate(){

    }
}
