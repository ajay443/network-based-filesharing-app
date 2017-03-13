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
        )
        {   //int content = 0;
            byte b[] = new byte[16 * 1024];
            int count;
            while ((count = fip.read(b)) > 0) {
                out.write(b, 0, count);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }




    public static boolean searchInMyFileDB(String folderSuffix, String fileName) {
        boolean isFound = false;
        File file = new File("sharedFolder" + folderSuffix + "/" + fileName);
        if (file.exists()) {
            isFound = true;
        }
        return isFound;
    }

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

    public static void main(String[] args) {
        System.out.println(Util.getValue("master.foldername"));
        System.out.println(Util.getValue("push.switch"));
        System.out.println(Util.getValue("pull.switch"));
        System.out.println(Util.getValue("pull.TTR"));
        System.out.println(Util.getValue("cached.foldername "));
    }


}