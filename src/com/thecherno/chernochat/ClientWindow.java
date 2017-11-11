package com.thecherno.chernochat;


import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.UIManager;


import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

public class ClientWindow extends JFrame implements Runnable {
	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;
	private JTextField txtMessage;
	private JTextArea history;
	private DefaultCaret caret;
	
	private Client client;
	private boolean running=false;
	
	private Thread listen;
	private Thread run;
	private JMenuBar menuBar;
	private JMenu mnFile;
	private JMenuItem mntmOnlineUsers;
	private JMenuItem mntmExit;
	
	private OnlineUsers users;
	
	private List<ClientChatWindow> chatWindows=new ArrayList<ClientChatWindow>();
	
	public ClientWindow(String name, String address, int port) {
		setTitle("Cherno Chat Client");
		client=new Client(name,address,port);
		boolean connect=client.openConnection(client.getAddress());
		if(!connect){
			System.err.println("connection failed!");
			console("Connection failed!");
		}
		createWindow();
		try {
			setTitle(name+" | "+InetAddress.getByName(address).toString());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		console("Attempting a connection to "+address+": "+port+" , user: "+name);
		String connection="/c/"+name+"/e/";
		client.send(connection.getBytes());
		users=new OnlineUsers(client,chatWindows);
		run=new Thread(this,"Running");
		running=true;
		run.start();
	}
	
	private void createWindow(){
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(880,550);
		setLocationRelativeTo(null);
		
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		mntmOnlineUsers = new JMenuItem("Online Users");
		mntmOnlineUsers.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				users.setVisible(true);
				users.refresh();
			}
		});
		mnFile.add(mntmOnlineUsers);
		
		mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String disconnect="/d/"+client.getID()+"/e/";
				send(disconnect,false);
				running=false;
				client.close();
				dispose();
				System.exit(0);
			}
		});
		mnFile.add(mntmExit);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{28,815,30,7};//sum=880
		gbl_contentPane.rowHeights = new int[]{25,485,40};//sum=550
		gbl_contentPane.columnWeights = new double[]{0.0, 1.0};
		gbl_contentPane.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		history = new JTextArea();
		history.setEditable(false);
		caret=(DefaultCaret)history.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		JScrollPane scroll=new JScrollPane(history);
		GridBagConstraints scrollConstraints = new GridBagConstraints();
		scrollConstraints.insets = new Insets(0, 0, 5, 5);
		scrollConstraints.fill = GridBagConstraints.BOTH;
		scrollConstraints.gridx = 0;
		scrollConstraints.gridy = 0;
		scrollConstraints.gridwidth=3;
		scrollConstraints.gridheight=2;
		contentPane.add(scroll, scrollConstraints);
		
		txtMessage = new JTextField();
		txtMessage.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode()==KeyEvent.VK_ENTER){
					send(txtMessage.getText(),true);
				}
			}
		});
		GridBagConstraints gbc_txtMessage = new GridBagConstraints();
		gbc_txtMessage.insets = new Insets(0, 0, 0, 5);
		gbc_txtMessage.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtMessage.gridx = 0;
		gbc_txtMessage.gridy = 2;
		gbc_txtMessage.gridwidth = 2;
		contentPane.add(txtMessage, gbc_txtMessage);
		txtMessage.setColumns(10);
		
		JButton btnSend = new JButton("Send");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				send(txtMessage.getText(),true);
				txtMessage.requestFocus();
			}
		});
		GridBagConstraints gbc_btnSend = new GridBagConstraints();
		gbc_btnSend.insets = new Insets(0, 0, 0, 5);
		gbc_btnSend.gridx = 2;
		gbc_btnSend.gridy = 2;
		contentPane.add(btnSend, gbc_btnSend);
		
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				String disconnect="/d/"+client.getID()+"/e/";
				send(disconnect,false);
				running=false;
				client.close();
			}
		});
		setVisible(true);
		txtMessage.requestFocusInWindow();
	}
	
	public void run(){
		listen();
	}

	public void send(String message,boolean text){
		if(message.equals("")) return;
		if(text){
			message=client.getName()+": "+message;
			message="/m/"+message+"/e/";
			txtMessage.setText("");
		}
		client.send(message.getBytes());
	}
	
	public void listen(){
		listen=new Thread("Listen"){
			public void run(){
				while(running){
					String message=client.receive();
					if(message.startsWith("/c/")){
						client.setID(Integer.valueOf(message.split("/c/|/e/")[1]));
						console("Successfully connected to the server! ID: "+client.getID());
					} else if(message.startsWith("/m/")){
						String text=message.substring(3);
						text=text.split("/e/")[0];
						console(text);
					} else if(message.startsWith("/i/")){
						String text="/i/"+client.getID()+"/e/";
						send(text,false);
					} else if(message.startsWith("/un/")){
						String message1=message.split("/e/")[0]+"/e/";
						String[] u=message1.split("/un/|/n/|/e/");
						users.updateName(Arrays.copyOfRange(u, 0, u.length));
					} else if(message.startsWith("/ui/")){
						String message1=message.split("/e/")[0]+"/e/";
						String[] u=message1.split("/ui/|/n/|/e/");
						users.updateID(Arrays.copyOfRange(u, 0, u.length));
					} else if(message.startsWith("/p/")){
						processPC(message.substring(3));
					}
				}
			}
		};
		listen.start();
	}
	
	private void processPC(String string){
		if(string.startsWith("/c/")){
			int pcid=Integer.valueOf(string.split("/c/|/e/")[1]);
			String otherClientName=string.split("/c/|/e/")[2];
			int otherClientID=Integer.valueOf(string.split("/c/|/e/")[3]);
			ClientChatWindow cw=new ClientChatWindow(client,pcid,otherClientName,users.getLA(),otherClientID);
			chatWindows.add(cw);
			if(string.split("/c/|/e/")[4]=="1") cw.setVisible(true);
			else cw.setVisible(false);
		}
		else if(string.startsWith("/m/")){
			int pcid=Integer.valueOf(string.split("/m/|/e/")[1]);
			
			String message=string.split("/m/|/e/")[2];
			for(int i=0;i<chatWindows.size();i++){
				ClientChatWindow cw=chatWindows.get(i);
				if(cw.getPCID()==pcid){
					if(cw.isVisible()==false)	cw.setVisible(true);
					cw.receive(message);
					break;
				}
			}
		}
		else if(string.startsWith("/d/")){
			int pcid=Integer.valueOf(string.split("/d/|/e/")[1]);
			for(int i=0;i<chatWindows.size();i++){
				ClientChatWindow cw=chatWindows.get(i);
				if(cw.getPCID()==pcid){
					cw.dispose();
					break;
				}
			}
		}
	}
	
	
	public void console(String message){
		if(new String("").equals(message))
			return;
		history.setCaretPosition(history.getDocument().getLength());
		history.append(message+"\n\r");
	}
	
}
