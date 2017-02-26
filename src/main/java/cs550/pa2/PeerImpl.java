package cs550.pa2;


import cs550.pa2.helpers.Constants;
import cs550.pa2.helpers.Util;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
//import helpers.Constants;
//import helpers.Util;
/**
 * Created by Ajay on 2/24/17.
 */
public class PeerImpl implements Peer {
    int messageID=1;
    String hostaddress;
    int peerID;
    HashMap neighbors;
    int myport;
    ServerSocket serverSocket;
    int msg_id;
    Thread clientThread, serverThread;
    HashMap seenMessages;
    HashMap seenQueryHitMessages;
    List<String> neighbours = new ArrayList<String>();

    public PeerImpl() {
        peerID = 1;
        this.myport = 0;
        this.msg_id = 0;
        this.seenMessages = new HashMap<String,List<Integer>>();
        this.neighbors = new HashMap();
        this.seenQueryHitMessages = new HashMap<String,List<Integer>>();

    }

    @Override
    public void search(String query_id, String fileName, int ttl, boolean isForward){
        Socket sock = null;
        if(!isForward){
            query_id = Integer.toString(peerID) + ":" + Integer.toString(++msg_id);
            ttl = 7;
        }

         try{
                // transmits the query to all neighbours
                for (String neighbour:neighbours) {
                    //pushing the msgid to seenMessages so that I dont have to forward again later when I get back the same
                    if(!seenMessages.containsKey(query_id)){
                        List ports = new ArrayList<Integer>();
                        //Not adding myport to list of upstream peers, so that I dont have to send queryhit msg to myself
                        //ports.add(this.myport);
                        this.seenMessages.put(query_id,ports);
                    }
                    sock = new Socket(neighbour.split(":")[0], Integer.parseInt(neighbour.split(":")[1]));
                    PrintWriter out = new PrintWriter( sock.getOutputStream(), true );
                    out.println("query " + query_id + " " + fileName + " " + this.myport + " " + Integer.toString(ttl));
                    out.close();
                }
            }
            catch(IOException e){
                e.printStackTrace();
            }

    }
	@Override
	public void forwardQuery(String quer_id, String fileName, int ttl){
		search(quer_id, fileName, ttl,true);
	}

    @Override
    public void download(String fileName, String host, int port)throws IOException {
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


    @Override
    public void returnQueryHit(String msgid, String fileName, int port, int ttl, boolean isForward) {
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
    public void forwardQueryHit(String queryHit_id, String fileName, int port, int ttl){
                returnQueryHit(queryHit_id,fileName,port,ttl,true);
    }

    @Override
    public void runPeerServer(){
        boolean listening = true;
        int peerServerPort = Integer.getInteger(hostaddress.split(":")[1]);
        try {
            this.serverSocket = new ServerSocket(peerServerPort);
            while (listening) {
                System.out.println("Server is listening to port:"+peerServerPort);
                Socket new_socket = this.serverSocket.accept();
                try ( BufferedReader in = new BufferedReader(new InputStreamReader(new_socket.getInputStream()));) {
                    String message;
                    if ((message = in.readLine()) != null)
                        new Thread(new Runnable(){public void run(){
                            processInput(message,new_socket);
                            }}).start();
                    new_socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Could not listen on port " + peerServerPort);
            System.exit(-1);
        }

    }

    @Override
    public void runPeerClient(){
        String fileName="";
        try {
            while(true){
                System.out.println("\n1 : Lookup a file\n2 : Download file from a peer\n3 : Display seen query messages\n4 : Display seen queryhit messages\n5 : Exit\nEnter your choice number");
                Scanner in = new Scanner(System.in);
                int choice = in.nextInt();
                switch(choice){
                    case 1: System.out.println("Enter filename : \n");
                        fileName = in.next();
                        search(null,fileName,0,false);
                        break;
                    case 2:
                        System.out.println("Enter filename : \n");
                        fileName = in.next();
                        System.out.println("Enter Host name of the download server : \n");
                        String host = in.next();
                        System.out.println("Enter port number of of the download server : \n");
                        int hostPort = in.nextInt();
                        System.out.println("Enter file name : \n");
                        download(fileName,host,hostPort);
                        break;
                    case 3: // TODO remove all other choices
                        displaySeenMessages(Constants.QUERY);
                        break;
                    case 4:
                        displaySeenMessages(Constants.QUERYHIT);
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
    public void displayPeerInfo(){
        System.out.printf("Peer Id : %d \n Port listening on : %d \nNeighbors : \nPEER ID : SERVER PORT ",peerID,myport);
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
	    if (params[0].equals("Download")){ // params = Download filename socket
            Util.downloadFile(params[1],socket);
            //handleDownloadRequest();
	    }
	    else if(params[0].equals(Constants.QUERY)){
		    //DisplaySeenMessages(params[0]);
		    int ttl = Integer.valueOf(params[4]);
                    ttl = ttl - 1;		

		    //not searching or forwarding already seen message
		    if(!seenMessages.containsKey(params[1]) && ttl > 0){

			    List ports = new ArrayList<Integer>();
			    ports.add(params[3]);
			    this.seenMessages.put(params[1],ports);
	
			    forwardQuery(params[1],params[2],ttl);
			    if(Util.searchInMyFileDB(params[2]))
				    returnQueryHit(params[1],params[2],this.myport,7,false);

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
				//download();
				System.out.printf("File %s found at peer with port %d\n",params[2],Integer.valueOf(params[3]));
			}
			else{
				forwardQueryHit(params[1],params[2],Integer.valueOf(params[3]),ttl);
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
	public void displaySeenMessages(String type){
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
    @Override
    public void initConfig(String hostName, int port) {
	    this.hostaddress = hostName+":"+port;
	    neighbours = new ArrayList<String>();
	    File file = new File("config.file");
	    try(BufferedReader br = new BufferedReader(new FileReader(file))){
		    String line = null;
            System.out.println("Neighbours :");
			while((line = br.readLine()) != null){
                System.out.println(line);
            }
	    }
	    catch(IOException e){
		    e.printStackTrace();
	    }

	    Util.createFolder("sharedFolder");

        serverThread = new Thread () {
            public void run () {
                System.out.println("Peer Server Started");
                runPeerServer();
            }
        };
        clientThread = new Thread () {
            public void run () {
                System.out.println("\n\nPeer Client Started");
                try {
                    runPeerClient();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        serverThread.start();
        clientThread.start();

    }



}
