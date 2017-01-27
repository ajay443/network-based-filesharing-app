package  com.p2p;
import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.util.ListIterator;
public class EchoClient {
	
	ServerSocket ss;
	public EchoClient() throws IOException{
		ss = new ServerSocket(0);
	}
	public EchoClient(int portNum) throws IOException{
		ss =  new ServerSocket(portNum);
	}
	public static void main(String[] args) throws IOException {

		if (args.length != 3) {
			System.err.println("Usage: java EchoClient <host name> <port number of sever to connect> <port number to host as server>");
			System.exit(1);
		}

		String hostName = args[0];
		int portNumber = Integer.parseInt(args[1]);
		int server_PN = Integer.parseInt(args[2]);
		
		String fileName = "";	
		try {	
				EchoClient ec = new EchoClient(server_PN);

				while(true){
					System.out.println("1 : Lookup a file\n2 : Download file from a peer\n3 : Do nothing\n4 :Register File\n5 : Exit\nEnter your choice");
					Scanner in = new Scanner(System.in);
					int choice = in.nextInt();
					switch(choice){
						case 2: System.out.println("Enter filename : \n");
                                                        fileName = in.next();
							System.out.println("Enter Host name of the download server : \n");
                                                        String hn = in.next();
							System.out.println("Enter port number of of the download server : \n");
                                                        int pn = in.nextInt();
							ec.SendDownloadRequest(hn,pn,fileName);
							break;
						case 1: System.out.println("Enter filename : \n"); 
 							fileName = in.next();
							List<String> l = new ArrayList<String>(); 
							ec.LookupFile(hostName,portNumber,fileName,l);
							ListIterator<String> iter = l.listIterator();
							System.out.println(l.size());
							while(iter.hasNext()){
								System.out.println(iter.next());
							}
							break;
						case 3: ec.WaitForRequest();
							break;
						case 4: System.out.println("Enter filename : \n");
                                                        fileName = in.next();
							ec.RegisterFile(hostName,portNumber,fileName,Integer.toString(server_PN));
							break;
						default:System.exit(0);
					}
				}
			} 
                        catch (UnknownHostException e) {
						System.err.println("Don't know about host " + hostName);
						System.exit(1);
			} 
			catch (IOException e) {
						System.err.println("Couldn't get I/O for the connection to " +hostName);
						System.exit(1);
			}
			catch(Exception e){
				e.printStackTrace();
				System.exit(1);
			}
	}
	private void LookupFile(String hostName, int pn, String fileName,List<String> lst) throws IOException{
		Socket s = new Socket( hostName, pn );
		PrintWriter out = new PrintWriter( s.getOutputStream(), true );
		BufferedReader in = new BufferedReader( new InputStreamReader( s.getInputStream() ) );
		
		out.println("lookup "+fileName);
		String message;
		while((message = in.readLine()) != null){
			//System.out.println(message);
			if(message.length() == 0){
				in.close();
				out.close();
				return;
			}
			String peers[] = message.split(" ");
			for(String temp : peers){
				System.out.println(temp);  
				lst.add(temp);
			}
		}
		System.out.println("Returning");
		s.close();
	}
	private void SendDownloadRequest(String hn, int pn,String fileName) throws IOException{
		Socket sock = new Socket(hn,pn);
		PrintWriter out = new PrintWriter( sock.getOutputStream(), true );
		BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		
		try{
			out.println("Download "+fileName);
			sock.shutdownOutput();
			String message = "";
			PrintWriter p = new PrintWriter(fileName,"UTF-8");
			while((message = in.readLine()) != null){
				System.out.println(message);
				p.println(message);
			}
			p.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			sock.close();
		}
	}

	private void WaitForRequest() throws IOException{
		Socket cs = ss.accept();

		BufferedReader in = new BufferedReader( new InputStreamReader(cs.getInputStream() ) );
		PrintWriter out = new PrintWriter(cs.getOutputStream(), true);
		try{
			String inputLine = "";
			while ((inputLine = in.readLine()) != null) {
				if(inputLine.contains("Download")){
					String params[] = inputLine.split(" ");
					File f = new File(params[1]);
					FileInputStream fip = new FileInputStream(f);
					
					String fileContent = "";
					int content = 0;
					while((content = fip.read()) != -1){
						fileContent += (char)content;
					}
					System.out.println("File Content : " + fileContent);
					out.println(fileContent);
					fip.close();
					
				}
			}
			cs.shutdownInput();
		}	
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			cs.close();
		}
	}

	private void RegisterFile(String hn, int pn, String fn, String clientport) throws IOException{
		Socket sock = new Socket("localhost",pn);
		PrintWriter out = new PrintWriter(sock.getOutputStream(),true);
		out.println("register "+ fn+" "+clientport);	
		sock.close();
	}
	
}
