package cs550.indexserver;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ajay on 1/27/17.
 */
public  class Processor {
    boolean fileLocked = false;
    Thread searchThread = new Thread () {
        public void run () {
            searchInfile();
        }
    };
    Thread registry = new Thread () {
        public void run () {
            writeFile("p1","a1");
        }
    };


     void doit(String mode) throws InterruptedException {
        if(mode.equals("registry")){

            while(fileLocked){
                System.out.print("waiting - registry");
                this.wait();
            }
            lockFile();


            registry.start();

            unlockFile();


        }else{

            while(fileLocked){
                System.out.print("waiting - lookup");
                this.wait();
            }
            lockFile();

            searchThread.start();


                unlockFile();

        }

    }
    /*synchronized void lookup() {

        lockFile();
        searchInfile();

        unlockFile();

        return ;
    }*/

    private void searchInfile() {
        System.out.println("Searching ... ");
        for(int i=0 ; i < 30;i++){
            for(int j=0 ; j < 30;j++){
                searchInFile("ajay");
            }
        }

    }

    /*synchronized void registry(String peerId,String  filename) {


        lockFile( );
        writeFile(peerId,filename);
        unlockFile();


        notify();
    }*/

     synchronized  void lockFile() { this.fileLocked = true;}
    synchronized void unlockFile() { this.fileLocked = false; notify();}

    private void writeFile(String peerId, String filename) {
        System.out.println("Registering  ... "+filename+","+peerId);
        for(int i=0 ; i < 3000;i++){
            for(int j=0 ; j < 30;j++){
                fileAppender();
            }
        }

    }

    void fileAppender(){
        try {
            String data = " Tutorials Point is a best website in the world";
            File f1 = new File("junk.txt");
            if(!f1.exists()) {
                f1.createNewFile();
            }
            FileWriter fileWritter = new FileWriter(f1.getName(),true);
            BufferedWriter bw = new BufferedWriter(fileWritter);
            bw.write(data);
            bw.close();
            //System.out.println("Done");
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public static boolean searchInFile(String string){

        BufferedReader br = null;
        List<String> lst = new ArrayList<String>();
        try{
            // creates the directory for root..
            File f1 = new File("junk.txt");

            // if file doesnt exists, then create it
            if (!f1.exists()) {
                f1.createNewFile();
            }

            br = new BufferedReader(new FileReader(f1));
            String txt = null;

            while((txt = br.readLine()) != null){
                //System.out.println("File content : "+txt);
                if(txt.contains(string)){
                   System.out.print("Found True");
                   return true;
                }
            }
            return false;
        }
        catch(IOException e){
            e.printStackTrace();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            try{
                br.close();
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
        return false;
    }
}
