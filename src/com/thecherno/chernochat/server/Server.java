package com.thecherno.chernochat.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Server implements Runnable{
	
	private List<ServerClient> clients=new ArrayList<ServerClient>();
	private List<Integer> clientResponse=new ArrayList<Integer>();
	
	private int port;
	private DatagramSocket socket;
	private boolean running=false;
	private Thread run,manage,send,receive;
	
	private final int MAX_ATTEMPTS=5;
	
	private boolean raw=false;
	
	public Server(int port){
		this.port=port;
		try {
			socket=new DatagramSocket(port);
		} catch (SocketException e) {
			e.printStackTrace();
			return;
		}
		run=new Thread(this,"Server");
		run.start();
	}
	public void run(){
		running=true;
		System.out.println("Server started at port: "+port);
		manageClients();
		receive();
		Scanner scanner=new Scanner(System.in);
		while(running){
			String text=scanner.nextLine();
			if(!text.startsWith("/")){
				sendToAll("/m/Server: "+text+"/e/");
				continue;				
			}
			text=text.substring(1);
			if(text.equals("raw")){
				raw=!raw;
				if(raw)	System.out.println("Raw mode enabled");
				else	System.out.println("Raw mode enabled");
			}
			else if(text.equals("clients")){
				System.out.println("Clients:");
				System.out.println("========");
				for(int i=0;i<clients.size();i++){
					ServerClient c=clients.get(i);
					System.out.println(c.name+"("+c.getID()+"): "+c.address.toString()+": "+c.port);
				}
				System.out.println("========");
			}
			else if(text.startsWith("kick")){
				String name=text.split(" ")[1];
				int id=-1;
				boolean number=true;
				try{
					id=Integer.parseInt(name);
				}catch(NumberFormatException e){
					number=false;
				}
				if(number){
					boolean exists=false;
					for(int i=0;i<clients.size();i++){
						if(clients.get(i).getID()==id){
							exists=true;
							break;
						}
					}
					if(exists){
						disconnect(id,true);
					}
					else {
						System.out.println("Client "+id+" doesn't exist! Check ID Number.");
					}
				}
				else{
					for(int i=0;i<clients.size();i++){
						ServerClient c=clients.get(i);
						if(name.equals(c.name)){
							disconnect(c.getID(),true);
							break;
						}
					}
				}
			}
			else if(text.equals("quit")){
				quit();
			}
			else if(text.equals("help")){
				printHelp();
			}
			else{
				System.out.println("Unknown Command.");
				printHelp();
			}
		}
		scanner.close();
	}
	
	private void printHelp(){
		System.out.println("Here is a list of all available commands:");
		System.out.println("=========================================");
		System.out.println("/raw - enables the raw mode.");
		System.out.println("/clients - shows all connected clients.");
		System.out.println("/kick [userID or userName].");
		System.out.println("/help - shows this help message.");
		System.out.println("/quit - shuts down this server.");
	}
	
	private void manageClients(){
		manage=new Thread("Manage"){
			public void run(){
				while(running){
					sendToAll("/i/server");
					sendStatus();
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					for(int i=0;i<clients.size();i++){
						ServerClient c=clients.get(i);
						if(!clientResponse.contains(new Integer(clients.get(i).getID()))){
							if(c.attempt>=MAX_ATTEMPTS){
								disconnect(c.getID(),false);
							} else{
								c.attempt++;
							}
						}
						else{
							c.attempt=0;
							clientResponse.remove(new Integer(c.getID()));
						}
					}
				}
			}
		};
		manage.start();
	}
	
	private void sendStatus(){
		if(clients.size()<=0) return;
		String users="/u/";
		for(int i=0;i<clients.size()-1;i++){
			users+=clients.get(i).name+"/n/";
		}
		users+=clients.get(clients.size()-1).name+"/e/";
		sendToAll(users);
	}
	
	private void receive(){
		receive=new Thread("Receive"){
			public void run(){
				while(running){
					byte[] data=new byte[1024];
					DatagramPacket packet=new DatagramPacket(data,data.length);
					try {
						socket.receive(packet);
					}catch(SocketException e){
						/*blank*/
					}					
					catch (IOException e) {
						e.printStackTrace();
					}
					process(packet);
				}
			}
		};
		receive.start();
	}
	
	public void sendToAll(String message){
		if(message.startsWith("/m/")){
			String text=message.substring(3);
			text=text.split("/e/")[0];
			System.out.println(text);
		}
		for(int i=0;i<clients.size();i++){
			ServerClient client=clients.get(i);
			send(message.getBytes(),client.address,client.port);
		}
	}
	
	void send(final byte[] data,InetAddress address,int port){
		send=new Thread(){
			public void run(){
				DatagramPacket packet=new DatagramPacket(data,data.length,address,port);
				try {
					socket.send(packet);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		send.start();
	}
	
	private void send(String message,InetAddress address,int port){
		message+="/e/";
		send(message.getBytes(),address,port);
	}
	
	public void process(DatagramPacket packet){
		String string=new String(packet.getData());
		if(raw) System.out.println(string);
		if(string.startsWith("/c/")){
			int id=UniqueIdentifier.getIdentifier();
			System.out.println("Identifier: "+id);
			String name=string.split("/c/|/e/")[1];
			System.out.println(name+"("+id+") connected!");
			clients.add(new ServerClient(name,packet.getAddress(),packet.getPort(),id));
			System.out.println(packet.getAddress().toString()+" : "+packet.getPort());
			String ID="/c/"+id;
			send(ID,packet.getAddress(),packet.getPort());
		}
		else if(string.startsWith("/m/")){
			sendToAll(string);
			System.out.println(string.subSequence(3,string.indexOf("/e/")));
		}
		else if(string.startsWith("/d/")){
			String id=string.split("/d/|/e/")[1];
			disconnect(Integer.valueOf(id),true);
		}
		else if(string.startsWith("/i/")){
			clientResponse.add(Integer.valueOf(string.split("/i/|/e/")[1]));
		}
		else{
			System.out.println(string);
		}
		
	}
	
	private void quit(){
		for(int i=0;i<clients.size();i++){
			disconnect(clients.get(i).getID(),true);
		}
		running=false;
		socket.close();
	}
	
	private void disconnect(int id,boolean status){
		ServerClient c=null;
		boolean existed=false;
		for(int i=0;i<clients.size();i++){
			if(clients.get(i).getID()==id){
				c=clients.get(i);
				clients.remove(i);
				existed=true;
				break;
			}
		}
		if(!existed) return;
		String message="";
		int ID=c.getID();
		String address=c.address.toString();
		int port=c.port;
		if(status){
			message="client "+c.name+" ("+ID+") @ "+address.toString()+":"+port+" disconnected.";
		}
		else{
			message="client "+c.name+" ("+ID+") @ "+address.toString()+":"+port+" timed out!";
		}
		System.out.println(message);
	}

}