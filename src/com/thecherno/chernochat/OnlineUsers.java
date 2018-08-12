package com.thecherno.chernochat;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.JList;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.Font;

public class OnlineUsers extends JFrame {

	private JPanel contentPane;
	private JList<String> listName;
	private JList<Integer> listID;
	private List<String> usersNameMain=new ArrayList<String>();
	private List<Integer> usersIDMain=new ArrayList<Integer>();
	private Client selfClient;
	private ListAction la;

	public OnlineUsers(Client selfClient,List<ClientChatWindow> chatWindowsList) {
		this.selfClient=selfClient;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(300,500);
		setLocationRelativeTo(null);
		setTitle("Online Users"+" | "+selfClient.getName());
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		listName = new JList<String>();
		listName.setFont(new Font("Tahoma", Font.PLAIN, 24));
		listID=new JList<Integer>();
		listName.setBounds(0, 0, 284, 400);
		contentPane.add(listName);
		
		la=new ListAction(selfClient,chatWindowsList);
		listName.addListSelectionListener(la);
		
		JButton btnNewButton = new JButton("Refresh!");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				listName.clearSelection();
				refresh();
			}
		});
		listName.addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent e){
				listName.clearSelection();
				refresh();
			}
		});
		btnNewButton.setBounds(97, 411, 89, 23);
		contentPane.add(btnNewButton);
	}
	
	public void updateName(String[] names){
		usersNameMain.clear();
		String[] laUsers=new String[names.length];
		int c=0;
		for(int i=1;i<names.length;i++){
			if(selfClient.getName().equals(names[i]))
				continue;
			usersNameMain.add(names[i]);
			laUsers[c++]=names[i];
		}
		la.updateUsersNameMain(laUsers);
	}
	
	public void updateID(String[] ids){
		usersIDMain.clear();
		Integer[] laID=new Integer[ids.length];
		int c=0;
		for(int i=1;i<ids.length;i++){
			try{
				if(selfClient.getID()==Integer.valueOf(ids[i]))
					continue;
				usersIDMain.add(Integer.valueOf(ids[i]));
				laID[c++]=Integer.valueOf(ids[i]);
			}catch(Exception e){}
		}
		la.updateUsersIDMain(laID);
	}
	
	public ListAction getLA(){
		return la;
	}
	
	public void refresh(){
		String[] ns=new String[usersNameMain.size()];
		Integer[] is=new Integer[usersIDMain.size()];
		for(int i=0;i<usersNameMain.size();i++){
			ns[i]=usersNameMain.get(i);
			is[i]=usersIDMain.get(i);
		}
		listName.setListData(ns);
		listID.setListData(is);
		//la.updateUsersName(ns);/* redundant */
		//la.updateUsersID(is);/* redundant */
		la.updateUsersNameMain(ns);
		la.updateUsersIDMain(is);
	}
}
