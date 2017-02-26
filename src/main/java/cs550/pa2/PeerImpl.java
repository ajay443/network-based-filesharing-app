//package pa2;

import java.net.Socket;
import java.net.ServerSocket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;
import java.io.FileReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Map;
import java.util.Collection;
import java.net.InetAddress;
//import helpers.Constants;
//import helpers.Util;
/**
 * Created by Ajay on 2/24/17.
 */
public class PeerImpl implements Peer{

	int peerID;
	HashMap neighbors;
	int myport;
	ServerSocket mysock;
	int msg_id;
	Thread clientThread, serverThread;
	HashMap seenMessages;
	HashMap seenQueryHitMessages;
     
        @Override
		public void SendSearchQuery(String query_id, String fileName, int ttl, boolean isForward){
			Socket sock = null;
			if(!isForward){
				query_id = Integer.toString(peerID) + ":" + Integer.toString(++msg_id);
				ttl = 7;
			}

			Collection port_collection = this.neighbors.values();
			Iterator i = port_collection.iterator();
			while(i.hasNext()){
				try{
					sock = new Socket("localhost",(int)i.next());
					PrintWriter out = new PrintWriter( sock.getOutputStream(), true );

					out.println("query " + query_id + " " + fileName + " " + this.myport + " " + Integer.toString(ttl));
					out.close();

					//pushing the msgid to seenMessages so that I dont have to forward again later when I get back the same
					if(!seenMessages.containsKey(query_id)){
						List ports = new ArrayList<Integer>();
						//Not adding myport to list of upstream peers, so that I dont have to send queryhit msg to myself
						//ports.add(this.myport);
						this.seenMessages.put(query_id,ports);
					}
					//else{
					//	System.out.println("2. Not forwarding query message");
					//}
				}
				catch(IOException e){
					e.printStackTrace();
				}
			}
		}
	@Override
	public void ForwardSearchQuery(String quer_id, String fileName, int ttl){
		SendSearchQuery(quer_id, fileName, ttl,true);
	}

		  @Override
			  public void SendDownloadRequest(String fileName,String host, int port)throws IOException {
				  //send request
				  //write to socket and wait for response

				  Socket peerClientSocket = null;

				  try{
					  peerClientSocket = new Socket(host,port);
					  PrintWriter out = new PrintWriter( peerClientSocket.getOutputStream(), true );
					  //BufferedReader in = new BufferedReader(new InputStreamReader(peerClientSocket.getInputStream()));
					  InputStream in = peerClientSocket.getInputStream();
					  OutputStream fout = new FileOutputStream("sharedFolder/" + fileName);

					  out.println("Download "+fileName);
					  String message = "";
					  PrintWriter p = new PrintWriter(fileName,"UTF-8");

					  byte[] bytes = new byte[16*1024];

					  int count;
					  while ((count = in.read(bytes)) > 0) {
						  fout.write(bytes, 0, count);
					  }
					  p.close();

				  }
				  catch(Exception e){
					  e.printStackTrace();
				  }
				  finally{
					  peerClientSocket.close();
				  }

			  }

    public void HandleDownloadRequest(String fileName, Socket socket){
	    //accept conn and push the file
	    //make use of download function in util

	    File f = new File("sharedFolder/"+fileName);
	    try(
			    InputStream fip = new FileInputStream(f);
			    OutputStream out = socket.getOutputStream();
	       )
	    {
		    byte b[] = new byte[16 * 1024];
		    int count;
		    while ((count = fip.read(b)) > 0) {
			    out.write(b, 0, count);
		    }
	    }
	    catch(Exception e){
	    }
	    finally{
	    }
    }

    @Override
    public boolean SearchInMyFileDB(String fileName) {
	boolean isFound = false;
	File file = new File("sharedFolder/" + fileName);
	if (file.exists()) {
		isFound = true;
        }
	return isFound;
    }

    @Override
	    public void SendQueryHit(String msgid, String fileName, int port,int ttl,boolean isForward) {
		    //lookup
		Socket sock = null;
		List ports = (List)seenMessages.get(msgid);
		//System.out.printf("Sending queryhit to %d peers",ports.size());
		Iterator i = ports.iterator();
		if(!isForward){
			ttl = 7;
		}
		
		    while(i.hasNext()){
			    try{
				    sock = new Socket("localhost",Integer.valueOf((String)i.next()));
				    PrintWriter out = new PrintWriter( sock.getOutputStream(), true );

				    out.println("queryhit " + msgid + " " + fileName + " " + port + " " + ttl);
				    out.close();
				  	
				    if(!seenQueryHitMessages.containsKey(msgid)){
					    //I saw the message I am sending
					    List p = new ArrayList<Integer>();
					    //ports.add(params[3]);//dont have to forward queryhit to myself
					    this.seenQueryHitMessages.put(msgid,p);
				    }
				    //else{
				    //	System.out.println("2. Not forwarding queryhit message");
				    //}
			    }
			    catch(IOException e){
				    e.printStackTrace();
			    }
		    }

	    }

	@Override
		public void ForwardQueryHit(String queryHit_id, String fileName, int port, int ttl){
					SendQueryHit(queryHit_id,fileName,port,ttl,true);
		}

    @Override
	    public void initialize() {

	    }

    @Override
	    public void initialize(String hostName, int id, int port, int n[][]) {
		    this.peerID = id;
		    this.myport = port;
		    this.msg_id = 0;
		    this.seenMessages = new HashMap<String,List<Integer>>();
		    this.neighbors = new HashMap();
		    this.seenQueryHitMessages = new HashMap<String,List<Integer>>();
			for (int i[] : n){
				this.neighbors.put(i[0],i[1]);
			}
		    
			try {
			    File file = new File("sharedFolder");
			    if (!file.exists()) {
				    if (file.mkdir()) {
				    } else {
					    System.out.println("Failed to create directory!");
				    }
			    }
		    }catch ( Exception e ){
			    e.printStackTrace();
		    }
			

			//Util::createFolder(Constants.LOCAL_FOLDER);
		    serverThread = new Thread () {
			    public void run () {
				    System.out.println("Peer Server Started");
				    startServer();
			    }
		    };
		    clientThread = new Thread () {
			    public void run () {
				    System.out.println("\n\nPeer Client Started");
				    try {
					    startClient();
				    } catch (Exception e) {
					    e.printStackTrace();
				    }
			    }
		    };
			serverThread.start();
			clientThread.start();


	}
		public void startServer(){
		    boolean listening = true;
		    try {
			    this.mysock = new ServerSocket(this.myport);
			    while (listening) {
				    System.out.println("Server is listening to port:"+this.myport);
				    Socket new_socket = this.mysock.accept();
				    try (
						    //PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
						    BufferedReader in = new BufferedReader(
							    new InputStreamReader(
								    new_socket.getInputStream()));
					) {
					    String message;

					    if ((message = in.readLine()) != null) {
						    //processInput(message,new_socket);
						    new Thread(new Runnable(){
							public void run(){
								processInput(message,new_socket);
								}
							}).start();
						    //out.println(outputLine);

					    }
					    //socket.shutdownInput();

					    new_socket.close();
				    } catch (IOException e) {
					    e.printStackTrace();
				    }


			    }
		    } catch (IOException e) {
			    e.printStackTrace();
			    System.err.println("Could not listen on port " + this.myport);
			    System.exit(-1);
		    }

	    }

		public void startClient(){
			String fileName="";
			try {
				while(true){
					System.out.println("\n1 : Lookup a file\n2 : Download file from a peer\n3 : Display seen query messages\n4 : Display seen queryhit messages\n5 : Exit\nEnter your choice number");
					Scanner in = new Scanner(System.in);
					int choice = in.nextInt();
					switch(choice){
						case 1: System.out.println("Enter filename : \n");
							fileName = in.next();
							SendSearchQuery(null,fileName,0,false);
							break;
						case 2:
							System.out.println("Enter filename : \n");
							fileName = in.next();
							System.out.println("Enter Host name of the download server : \n");
							String host = in.next();
							System.out.println("Enter port number of of the download server : \n");
							int hostPort = in.nextInt();
							System.out.println("Enter file name : \n");
							SendDownloadRequest(fileName,host,hostPort);
							break;
						case 3:
							DisplaySeenMessages("query");
							break;
						case 4: 
							DisplaySeenMessages("queryhit");
							break;
						case 5:
							System.exit(0);
						default:
							System.exit(0);
					}
				}
			}
			catch(Exception e){
				e.printStackTrace();
				System.exit(1);
			}
		}
    
    @Override
    public void DisplayPeerInfo(){
	    System.out.printf("Peer ID : %d\n",peerID);
	    System.out.printf("Port listening on : %d\n",myport);
	    System.out.println("Neighbors : ");
	    System.out.println("PEER ID : SERVER PORT ");
	    Set set = this.neighbors.entrySet();
	    Iterator it =  set.iterator();
	    while(it.hasNext()) {
			Map.Entry me = (Map.Entry)it.next();
			System.out.println(me.getKey() + ": " + me.getValue());
		}
	    System.out.println();
    }


    private void processInput(String input,Socket socket) {
	    System.out.println("Received Message : " + input);
	    //String fileContent = "";
	    int senderPort = 0;
	    String params[] = input.split(" ");
	    if (params[0].equals("Download")){
		    HandleDownloadRequest(params[1],socket);
	    }
	    else if(params[0].equals("query")){
		    //DisplaySeenMessages(params[0]);
		    int ttl = Integer.valueOf(params[4]);
                    ttl = ttl - 1;		

		    //not searching or forwarding already seen message
		    if(!seenMessages.containsKey(params[1]) && ttl > 0){

			    List ports = new ArrayList<Integer>();
			    ports.add(params[3]);
			    this.seenMessages.put(params[1],ports);
	
			    //DisplaySeenMessages("query");

			    ForwardSearchQuery(params[1],params[2],ttl);
			    if(SearchInMyFileDB(params[2])){
				SendQueryHit(params[1],params[2],this.myport,7,false);	
			    }
			    //ForwardSearchQuery(params);
		    }
		    else{
			    List ports = (List)seenMessages.get(params[1]);
			    if(!ports.contains(params[3])){
				    ports.add(params[3]);
			    }
			    System.out.println("Not forwarding " + input);
		    }
	    }
	else if(params[0].equals("queryhit")){
		//DisplaySeenMessages(params[0]);
		int ttl = Integer.valueOf(params[4]);
                ttl = ttl - 1;
	
		//Not forwarding already seen query hit messages
		if(!seenQueryHitMessages.containsKey(params[1]) && ttl > 0){
			List ports = new ArrayList<Integer>();
                        ports.add(params[3]);
                        this.seenQueryHitMessages.put(params[1],ports);
		
			String msg_params[] = params[1].split(":");
			if (Integer.valueOf(msg_params[0]) == this.peerID){
				//make sure we do not download from all peers
				//SendDownloadRequest();
				System.out.printf("File %s found at peer with port %d\n",params[2],Integer.valueOf(params[3]));
			}
			else{
				ForwardQueryHit(params[1],params[2],Integer.valueOf(params[3]),ttl);
			}
		}
		else{
			List ports = (List)seenQueryHitMessages.get(params[1]);
			if(!ports.contains(params[3])){
				ports.add(params[3]);
			}
			System.out.println("Not forwarding " + input);
		}
	}
    }
	public void DisplaySeenMessages(String type){
		System.out.println("Displaying seen " + type + " messages");
		Set set = null;
		if(type.equals("query")){
		set = seenMessages.entrySet();
		}
		else{
			set = seenQueryHitMessages.entrySet();
		}
		Iterator i = set.iterator();
		while(i.hasNext()){
			Map.Entry entry = (Map.Entry)i.next();
			System.out.println(entry.getKey() + ":" + entry.getValue());
		}
	}

}
