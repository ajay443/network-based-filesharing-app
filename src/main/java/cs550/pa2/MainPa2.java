//package pa2;

//import java.util.Scanner;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.ArrayList;
import java.io.FileReader;
import java.io.File;
import java.util.Iterator;
/**
 * Created by Ajay on 2/24/17.
 */
public class MainPa2 {

    Peer peer = new PeerImpl();
    public static void main(String[] args) {
        // This will run programming assignment 2
        new MainPa2();
    }

    public MainPa2() {
        // Init peer ...
        /*
	Scanner in = new Scanner(System.in);
        System.out.print("Default config ? (yes/no) or (y/n): ");
        String choice = in.next();

        if(choice.equalsIgnoreCase("yes") ||
                choice.equalsIgnoreCase("y") ){
            peer.initConfig();
        }else{
            System.out.println("Enter Host Name Example: 'localhost' or 127.0.0.1 ");
            String hostName = in.next();
            System.out.println("Port address  : ");
            int port = in.nextInt();

            peer.initConfig(hostName,port);
        }
	*/

	    int defaultId = 100;
	    int defaultNeighbors[] = {101,102,103,104};
	    int port = 52001;
	    ArrayList n_list = new ArrayList<Integer>();
	    ArrayList p_list = new ArrayList<Integer>();

	    File file = new File("Config.file");
	    try(
			    //InputStream in =  Files.newInputStream(file);
			    BufferedReader br = new BufferedReader(new FileReader(file))
	       ){
		    String line = null;
		    while((line = br.readLine()) != null){
			    System.out.println("Printing file contents : " + line);
			    String params[] = line.split(":");
			    if(params[0].equals("ID ")){
				    defaultId = Integer.valueOf(params[1]);
			    }
			    else if(params[0].equals("Server Port ")){
				    port = Integer.valueOf(params[1]);
			    }
			    else if (params[0].equals("Neighbors ")){
				    String n_ids[] = params[1].split(",");
				    for(String i : n_ids){
					    n_list.add(Integer.valueOf(i));
				    }
			    }
			    else{
				String ports[] = params[1].split(",");
				for(String p : ports){
					p_list.add(Integer.valueOf(p));
				}
			    }

		    }
	    }
	    catch(IOException e){
		    e.printStackTrace();
	    }
	
	    int[][] neighbors = new int[n_list.size()][2];
	    Iterator<Integer> n_iterator = n_list.iterator();
	    Iterator<Integer> p_iterator = p_list.iterator();
	
	    for (int i=0; i < neighbors.length; i++)
	    {
		    neighbors[i][0] = n_iterator.next().intValue();
		    neighbors[i][1] = p_iterator.next().intValue();	    
	    }
	    peer.initConfig("localhost",defaultId, port, neighbors);
	    peer.DisplayPeerInfo();
    }
}
