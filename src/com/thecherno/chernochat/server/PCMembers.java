package com.thecherno.chernochat.server;

public class PCMembers {
	private ServerClient client1;
	private ServerClient client2;
	private int PCID;
	
	public PCMembers(ServerClient client1,ServerClient client2,int PCID){
		this.client1=client1;
		this.client2=client2;
		this.PCID=PCID;
	}
	public int getPCID(){
		return PCID;
	}
	public ServerClient getClient1(){
		return client1;
	}
	public ServerClient getClient2(){
		return client2;
	}
}
