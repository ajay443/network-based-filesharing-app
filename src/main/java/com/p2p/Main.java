package com.p2p;

import java.io.*;
import java.util.Scanner;

public class Main {
    public static int peerCount = 0 ;

    public static void main(String[] args) {
        init();
        for(int i=1;i<=Main.peerCount & i>=0; i++ )
            refreshOldIndexData("IndexDataFile-"+i+".txt");
        for(int i=1;i<=Main.peerCount & i>=0; i++ ){
            setupPeers(i);
            System.out.println("..................");
            indexPeers(i);
            searchInDataFile("IndexDataFile-"+i+".txt");
        }




    }




    public static void init(){
        System.out.print("Programming Assignment 1 : Configure the Index server first \nEnter the port for Index Server : " );
        Scanner scanner = new Scanner(System.in);
        String portNumber = scanner.next();
        System.out.println("Enter Number of Peer  " );
        peerCount = scanner.nextInt();
        scanner.close();
        System.out.println("Port: "+portNumber+"\nPeer = "+ peerCount);
    }

    /**
     * This method creates some folders for peers
     * example - Data/Peer1 ...
     * Reference  http://beginnersbook.com/2014/01/how-to-create-a-file-in-java/
     * @param port
     */
    private static void setupPeers(int port) {

        try {
            File dir = new File("Data/peer"+port);
            boolean successful = dir.mkdirs();
            File file = new File("Data/peer"+port+"/newfile.txt"+Math.random());
            boolean fvar = file.createNewFile();
        } catch (IOException e) {
            System.out.println("Exception Occurred:");
            e.printStackTrace();
        }
    }



    /**
     * This method is to register the peers to index server
     * It will create index file for each peers and stores it file list of that peer
     * example - Data/Peer1 ...
     * Reference  http://beginnersbook.com/2014/01/how-to-create-a-file-in-java/
     * @param port
     */
    private static void indexPeers(int port) {
        File folder = new File("Data/peer"+port);
        File[] listOfFiles = folder.listFiles();
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                writeToDataFile("IndexDataFile-"+port+".txt","Peer "+port+"," + listOfFiles[i].getName()+"\n");
               // System.out.println("Peer "+port+"," + listOfFiles[i].getName());
            } else if (listOfFiles[i].isDirectory()) {
                //todo out of scope for PA1
                System.out.println("Directory " + listOfFiles[i].getName());
            }
        }
    }

    private static void refreshOldIndexData(String filename) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(filename);
            writer.print("");
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public static void writeToDataFile(String filename,String data){
        BufferedWriter bw = null;
        FileWriter fw = null;

        try {

            File file = new File(filename);

            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            // true = append file
            fw = new FileWriter(file.getAbsoluteFile(), true);
            bw = new BufferedWriter(fw);

            bw.write(data);



        } catch (IOException e) {

            e.printStackTrace();

        } finally {
            close(bw,fw);


        }

    }

    private static void close(BufferedWriter bw, FileWriter fw) {
        try {

            if (bw != null)
                bw.close();

            if (fw != null)
                fw.close();

        } catch (IOException ex) {

            ex.printStackTrace();

        }
    }

    private static void close(BufferedReader br, FileReader fr) {
        try {
            if (br != null)
                br.close();

            if (fr != null)
                fr.close();

        } catch (IOException ex1) {

            ex1.printStackTrace();

        }
    }


    public static void  searchInDataFile(String filename){
        BufferedReader br = null;
        FileReader fr = null;

        try {

            fr = new FileReader(filename);
            br = new BufferedReader(fr);

            String sCurrentLine;

            br = new BufferedReader(new FileReader(filename));

            while ((sCurrentLine = br.readLine()) != null) {
                System.out.println(sCurrentLine+"..."+sCurrentLine.contains("new"));
            }

        } catch (IOException e) {

            e.printStackTrace();

        } finally {
            close(br,fr);
        }
    }


}
