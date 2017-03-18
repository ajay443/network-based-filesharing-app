/*
 * Copyright (C) 2017.  FileSharingSystem - https://github.com/ajayramesh23/FileSharingSystem
 * Programming Assignment from Professor Z.Lan
 * @author Ajay Ramesh
 * @author Chandra Kumar Basavaraj
 * Last Modified - 3/15/17 6:44 PM
 */

/**
 * File Name : Util.java
 * Description : Implementation of all utility functions
 * @authors : Ajay Ramesh and Chandra Kumar Basavaraju
 * version : 1.0
 * @date : 01/28/2017
 */
package cs550.pa3.helpers;

import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.Properties;


/**
 * Created by Ajay on 1/25/17.
 */
public  class Util {


    /**
     * Creates a folder is not already existing
     * @param rootDirName, path of the directory
     */
    public static void createFolder(String rootDirName){
        Util.print("Creating folder "+rootDirName);
        try {
            File file = new File(rootDirName);
            if (!file.exists()) {
                if (file.mkdir()) {
                } else {
                    System.out.println("Failed to create directory!");
                }
            }
        }catch ( Exception e ){
            e.printStackTrace();
        }

    }


    public static void downloadFile(String filePath,Socket socket){
        File f = new File(filePath);
        try(
                InputStream fip = new FileInputStream(f);
                OutputStream out = socket.getOutputStream();
        ) {   //int content = 0;
            byte b[] = new byte[16 * 1024];
            int count;
            while ((count = fip.read(b)) > 0) {
                out.write(b, 0, count);
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }


    public static boolean searchInMaster( String fileName) {
        boolean isFound = false;
        File file = new File(Util.getValue(Constants.MASTER_FOLDER,Constants.PEER_PROPERTIES_FILE) + "/" + fileName);
        if (file.exists()) {
            isFound = true;
        }
        return isFound;
    }

    public static boolean searchInCached(String fileName, int version, String originServer, boolean validateVersion){

        //BufferedReader br = null;
        RandomAccessFile raf = null;

        try {
            //createFolder(Constants.INDEX_FILE_NAME.split("/")[0]);

            //File file = new File("downloadlist.txt");
            raf = new RandomAccessFile(Util.getValue(Constants.DOWNLOADED_LIST,Constants.PEER_PROPERTIES_FILE), "rw");

            // if file doesnt exists, then create it
            if (raf == null) {
                //file.createNewFile();
                return false;
            }

            //br = new BufferedReader(new FileReader(file));
            String txt = null;

            while ((txt = raf.readLine()) != null) {
                System.out.println("file content : " + txt);
                if(txt.contains(fileName)){
                    String fileAttrs[] = txt.split(" ");
                    if(!validateVersion && fileAttrs[3].equals("valid")){
                        return true;
                    }
                    //System.out.println(txt+"\n");
                    if(validateVersion) {
                        if ((fileAttrs[2].equals(originServer)) && (version > Integer.parseInt(fileAttrs[1])) && (fileAttrs[3].equals("valid"))) {
                            //markFileInvalid(fileName, originServer);
                            //FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
                            //BufferedWriter bw = new BufferedWriter(fw);
                            txt = txt.replace("valid", "invalid");
                            System.out.println("Changed content : " + txt);
                            synchronized (Util.class){
                                raf.writeChars(txt);
                            }
                            return false;
                        } else if (fileAttrs[3] == "valid" && originServer.equals(fileAttrs[2])) {
                            return true;
                        }
                    }

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                raf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /*
    private static void markFileInvalid(String fileName, String originServer) {
        BufferedWriter bw = null;
        FileWriter fw = null;

        try {

            File file = new File("downloadlist.txt");
            //createFolder(Constants.INDEX_FILE_NAME.split("/")[0]);

            // if file doesnt exists, then create itportRequested
            /*
            if (!file.exists()) {
                file.createNewFile();
            }

            // true = append file
            fw = new FileWriter(file.getAbsoluteFile(), true);
            bw = new BufferedWriter(fw);

            bw.write(data);
            return true;

        } catch (IOException e) {

            e.printStackTrace();
            return false;

        } finally {
            try {
                if (bw != null)
                    bw.close();

                if (fw != null)
                    fw.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }
    }
     */
    public static String getValue(String key) {

        Properties prop = new Properties();
        String filePath = "";

        try {

            InputStream inputStream =
                    Util.class.getClassLoader().getResourceAsStream("config.properties");

            prop.load(inputStream);
            filePath = prop.getProperty(key);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return filePath;

    }

    public static String getValue(String key, String fromFile) {

        Properties prop = new Properties();
        String filePath = "";

        try {

            InputStream inputStream =
                    Util.class.getClassLoader().getResourceAsStream(fromFile);

            prop.load(inputStream);
            filePath = prop.getProperty(key);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return filePath;

    }


    public static void print(String input){
        if(Util.getValue("debug").equals("on"))
            System.out.println(new Date().toString()+" >>>> PA3 - Debug Information ::  "+ input);
    }

    public static void println(String input){
         System.out.println(new Date().toString()+" >>>> PA3 -  Information ::  "+ input);
    }

    public  static void sleep(int seconds){
        try{
            Thread.sleep(seconds*1000);
        }catch(InterruptedException e){
            Util.print(e.getMessage());
        }
    }

    public static void error(String errorMessage){
        System.err.println("*******************************************************");
        System.err.println(errorMessage);
        System.err.println("*******************************************************");
        System.exit(-1);
    }

    public static void main(String[] args) {
        Util.print("Hello World");
        System.out.println(Util.getValue(Constants.MASTER_FOLDER));
        System.out.println(Util.getValue("push.switch"));
        System.out.println(Util.getValue("pull.switch"));
        System.out.println(Util.getValue("pull.TTR"));
        System.out.println(Util.getValue(Constants.CACHED_FOLDER));
        System.out.println(Util.getValue("debug"));
    }


}