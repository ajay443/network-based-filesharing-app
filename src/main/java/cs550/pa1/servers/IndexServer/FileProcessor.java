package cs550.pa1.servers.IndexServer;

import cs550.pa1.helpers.Util;

import java.util.List;

/**
 * Created by Ajay on 1/28/17.
 */
public class FileProcessor{
     int fileUnlocked = 0;
     

    public FileProcessor() {

    }
    public  synchronized  void registry(String peerID, String filename) throws InterruptedException {
        while(fileUnlocked == 1) wait(2000);
        lockIndexFile();
        registry(peerID,filename,"file");
        unlockIndexFile();

    }
    public synchronized List<String> lookup(String text) throws InterruptedException{
        while(fileUnlocked == 1) wait(2000);
        lockIndexFile();
        List<String> results = search(text);
        unlockIndexFile();
        return results;
    }


    public boolean registry(String loc,String portRequested,String type) {
        String[] locArray;
        if(type.equals("folder"))
            for(String result: Util.listFiles(loc))
                Util.appendDataToFile(result+", "+portRequested+"\n");

        if(type.equals("file")){
            Util.appendDataToFile(loc+", "+portRequested+"\n");
        }

        return false;
    }


    public List<String> search(String text) {
        return Util.searchInFile(text);
    }

    private void lockIndexFile(){
        fileUnlocked = 1;
    }
    private void unlockIndexFile(){
        fileUnlocked = 0;
        notify();
    }
}

