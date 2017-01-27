package com.p2p;

import java.net.*;
import java.io.*;
import java.util.*;


//import java.nio.channels.*;
public class EchoServer {
	ServerSocket ss;
	public EchoServer() throws IOException{
		ss = new ServerSocket(0);
	}
	public EchoServer(int pn) throws IOException{
		ss = new ServerSocket(pn);
	}
	public static void main(String[] args) throws IOException {
		if (args.length != 1) {
			System.err.println("Usage: java EchoServer <port number>");
			System.exit(1);
		}

		int portNumber = Integer.parseInt(args[0]);
		EchoServer es = new EchoServer(portNumber);
		es.WaitForRequest();
		return;
	}
	private void WaitForRequest(){
		Socket clientSocket = null;
		while(true){
			try {
				//ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]));
				clientSocket = ss.accept();
				PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

				String inputLine;
				while ((inputLine = in.readLine()) != null) {
					System.out.println(inputLine);
					String params[] = inputLine.split(" ");
					if(params[0].equals("lookup")){
						System.out.println("Searching for file");
						List<String> l = SearchForFile(params[1]);
						//System.out.println(clientSocket.getLocalPort());
						//System.out.println(clientSocket.getPort());
						//System.out.println(l.size());
						for(String t : l){
							System.out.println("t : "+t);
							out.println(t);
						}
						clientSocket.shutdownOutput();
					}
					else if (params[0].equals("register")){
						IndexFile(params[1],params[2]);
					}
					/*
					else if (params[0].equals("Download")){
						File f = new File(params[1]);
						try(
								FileInputStream fip = new FileInputStream(f);

						   )
						{
							String fileContent = "";
							int content = 0;
							while((content = fip.read()) != -1){
								fileContent += (char)content;
							}
							System.out.println("File Content : " + fileContent);
							out.println(fileContent);
						}
						catch(Exception e){
						}
						finally{
						}

<<<<<<< HEAD
					}
					*/

				}
				clientSocket.shutdownInput();
			} 
			catch (IOException e) {
				System.out.println("Exception caught when trying to listen on port or listening for a connection");
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
			finally{
				try{	
					clientSocket.close();
				}
				catch(IOException e){
					e.printStackTrace();
				}
			}
		}
	}
	private List<String> SearchForFile(String fileName){
		System.out.println("FileName : "+fileName);
		File file = new File("list.txt");
		BufferedReader br = null;
		List<String> lst = new ArrayList<String>();
		try{
			br = new BufferedReader(new FileReader(file));
			String txt = null;

			while((txt = br.readLine()) != null){
				//System.out.println("File content : "+txt);
				if(txt.contains(fileName)){
					String temp[] = txt.split(" ");
					lst.add(temp[1]);
				}
			}		
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
		return lst;
	}
	private void IndexFile(String fileName,String port){
		File file = new File("list.txt");
		BufferedWriter bw = null;
		List<String> lst = new ArrayList<String>();
		try{
			bw = new BufferedWriter(new FileWriter(file,true));
			bw.write("\n");
			bw.write(fileName+" "+port);
			//bw.append(fileName+" "+port);
		}
		catch(IOException e){
			e.printStackTrace();
			//br.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			try{
				bw.close();
			}
			catch(IOException e){
				e.printStackTrace();
			}
		}
	}
}
