package cs550.pa1.helpers;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ajay on 1/25/17.
 */
public  class Util {


    public static List<String> searchInFile(String text){

        BufferedReader br = null;
        List<String> lst = new ArrayList<String>();
        try {
            // creates the directory for root..
            File dir = new File(Constants.INDEX_FILE_NAME.split("/")[0]);
            boolean successful = dir.mkdirs();
            //if(!successful) throw new Exception();
            File file = new File(Constants.INDEX_FILE_NAME);

            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            br = new BufferedReader(new FileReader(file));
            String txt = null;

            while ((txt = br.readLine()) != null) {
                //System.out.println("File content : "+txt);
                if(txt.contains(text)){
                    System.out.println(txt+"\n");
                    String temp[] = txt.split(" ");
                    lst.add(temp[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return lst;
    }


    public static List<String> listFiles(String loc) {
        try {
            List<String> results = new ArrayList<String>();
            File folder = new File(loc);
            File[] listOfFiles = folder.listFiles();
            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile()) {
                    results.add(loc + "/" + listOfFiles[i].getName());
                    // System.out.println("Peer "+port+"," + listOfFiles[i].getName());
                } else if (listOfFiles[i].isDirectory()) {
                    //todo out of scope for PA1
                    System.out.println("Directory " + listOfFiles[i].getName());
                }
            }
            return results;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static boolean appendDataToFile(String data) {
        BufferedWriter bw = null;
        FileWriter fw = null;

        try {

            File file = new File(Constants.INDEX_FILE_NAME);

            // if file doesnt exists, then create itportRequested
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

    public boolean removeLines(List<String> lines) {
        //todo - remove the lines when peer updates
        /*
        http://stackoverflow.com/questions/1377279/find-a-line-in-a-file-and-remove-it
        File inputFile = new File("myFile.txt");
        File tempFile = new File("myTempFile.txt");

        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

        String lineToRemove = "bbb";
        String currentLine;

        while((currentLine = reader.readLine()) != null) {
            // trim newline when comparing with lineToRemove
            String trimmedLine = currentLine.trim();
            if(trimmedLine.equals(lineToRemove)) continue;
            writer.write(currentLine + System.getProperty("line.separator"));
        }
        writer.close();
        reader.close();
        boolean successful = tempFile.renameTo(inputFile);*/

        return false;
    }

    public static void DeleteSingleLineInFile(String fileName, String portNumber) {
        //todo - remove the lines when peer updates
        try {

            //http://stackoverflow.com/questions/1377279/find-a-l
            // ine-in-a-file-and-remove-it
            File inputFile = new File("output/index.txt");
            File tempFile = new File("output/myTempFile.txt");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String lineToRemove = fileName + " " + portNumber;
            String currentLine;

            while ((currentLine = reader.readLine()) != null) {
                // trim newline when comparing with lineToRemove
                String trimmedLine = currentLine.trim();
                if (trimmedLine.equals(lineToRemove))
                    continue;
                writer.write(currentLine + System.getProperty("line.separator"));
            }
            writer.close();
            reader.close();
            if (inputFile.delete())
                tempFile.renameTo(inputFile);

            //return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
    public static void downloadFile(String filename,Socket socket){
        File f = new File(filename);
        try(
                InputStream fip = new FileInputStream(f);
                OutputStream out = socket.getOutputStream();
        )
        {
            //int content = 0;
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

}