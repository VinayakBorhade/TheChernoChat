package com.thecherno.chernochat;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;

import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ClientChatWindow extends JFrame {

	private JPanel contentPane;
	
	private JTextField txtMessage;
	private JTextArea history;
	private DefaultCaret caret;
	
	private Client client;
	private String otherName;
	private int otherID;
	private ListAction la;
	
	private int PCID;
	
	public ClientChatWindow(Client client,int PCID,String n,ListAction la,int otherID){
		this(n);
		this.client=client;
		this.PCID=PCID;
		this.la=la;
		this.otherID=otherID;
	}
	
	/**
	 * @wbp.parser.constructor
	 */
	public ClientChatWindow(String n) {
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(300,400);
		setLocationRelativeTo(null);
		otherName=new String(n);
		setTitle("PC | "+n);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0};
		gbl_contentPane.rowHeights = new int[]{0};
		gbl_contentPane.columnWeights = new double[]{Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{Double.MIN_VALUE};
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
		
		//setVisible(true);
		txtMessage.requestFocusInWindow();
	}
	
	/*
	public void send(String message,boolean text){
		
	}
	*/
	
	public int getPCID(){
		return PCID;
	}
	
	public int getOtherID(){
		return otherID;
	}
	
	public void send(String message,boolean text){
		if(message.equals("")) return;
		if(text){
			message=client.getName()+": "+message;
			message="/p/"+"/m/"+PCID+"/m/"+message+"/e/";
			txtMessage.setText("");
		}
		client.send(message.getBytes());
	}
	
	public void receive(String message){
		console(message);
	}
	
	private void console(String message){
		if(new String("").equals(message))
			return;
		history.setCaretPosition(history.getDocument().getLength());
		history.append(message+"\n\r");
	}

}
