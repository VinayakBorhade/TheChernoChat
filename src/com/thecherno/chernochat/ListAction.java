package com.thecherno.chernochat;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ListAction implements ListSelectionListener {
	
	private JList<String> listName;
	private JList<Integer> listID;
	private JList<String> listNameMain;
	private JList<Integer> listIDMain;
	private List<Integer> listids;
	
	private Client selfClient;
	
	private List<ClientChatWindow> chatWindowsList;
	
	public ListAction(Client selfClient,List<ClientChatWindow> chatWindowsList){
		listName=new JList<String>();
		listID=new JList<Integer>();
		listNameMain=new JList<String>();
		listIDMain=new JList<Integer>();
		listids=new ArrayList<Integer>();
		
		this.selfClient=selfClient;
		
		this.chatWindowsList=chatWindowsList;
	}
	
	public void valueChanged(ListSelectionEvent e) {
		String id=listIDMain.getModel().getElementAt(e.getFirstIndex()).toString();
		for(int i=0;i<chatWindowsList.size();i++){
			ClientChatWindow cw=chatWindowsList.get(i);
			if(cw.getOtherID()==Integer.valueOf(id)){
				if(cw.isVisible()==false){
					cw.setVisible(true);
				}
				return;
			}
		}
		if(listids.contains(Integer.valueOf(id))) return;
		listids.add(Integer.valueOf(id));
		if(listIDMain.getModel().getSize()==0) return;
		String connection="/p/"+"/c/"+selfClient.getID()+"/c/"+id+"/e/";		
		selfClient.send(connection.getBytes());
	}
	
	//public void removelistids()
	
	public void updateChatLists(List<ClientChatWindow> list){
		chatWindowsList=list;
	}
	
	public void updateUsersName(String[] list){
		listName.setListData(list);
	}
	
	public void updateUsersNameMain(String[] list){
		listNameMain.setListData(list);
	}

	public void updateUsersID(Integer[] list) {
		listID.setListData(list);
	}
	
	public void updateUsersIDMain(Integer[] list){
		listIDMain.setListData(list);
	}

}
