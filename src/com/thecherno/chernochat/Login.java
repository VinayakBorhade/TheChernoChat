package com.thecherno.chernochat;

import java.util.*;
import java.lang.*;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.*;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.awt.event.ActionEvent;

/*
import sun.management.ConnectorAddressLink;  
import sun.jvmstat.monitor.HostIdentifier;  
import sun.jvmstat.monitor.Monitor;  
import sun.jvmstat.monitor.MonitoredHost;  
import sun.jvmstat.monitor.MonitoredVm;  
import sun.jvmstat.monitor.MonitoredVmUtil;  
import sun.jvmstat.monitor.MonitorException;  
import sun.jvmstat.monitor.VmIdentifier;
*/
public class Login extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtName;
	private JTextField txtAddress;
	private JLabel lblIPAddress;
	private JTextField txtPort;
	private JLabel lblPort;
	private JLabel lblAddressDesc;
	private JLabel lblPortDesc;
	
	private static final int PORT = 9999;
	private static ServerSocket socket;
	
	public Login() {
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		setTitle("Login");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(300,380);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		txtName = new JTextField();
		txtName.setBounds(47, 43, 200, 30);
		contentPane.add(txtName);
		txtName.setColumns(10);
		
		JLabel lblName = new JLabel("Name:");
		lblName.setBounds(131, 28, 31, 14);
		contentPane.add(lblName);
		
		txtAddress = new JTextField();
		txtAddress.setBounds(47, 105, 200, 30);
		contentPane.add(txtAddress);
		txtAddress.setColumns(10);
		
		lblIPAddress = new JLabel("IP Address:");
		lblIPAddress.setBounds(119, 90, 56, 14);
		contentPane.add(lblIPAddress);
		
		txtPort = new JTextField();
		txtPort.setColumns(10);
		txtPort.setBounds(47, 176, 200, 30);
		contentPane.add(txtPort);
		
		lblPort = new JLabel("Port:");
		lblPort.setBounds(135, 161, 24, 14);
		contentPane.add(lblPort);
		
		lblAddressDesc = new JLabel("(eg. 192.168.1.0)");
		lblAddressDesc.setBounds(103, 136, 87, 14);
		contentPane.add(lblAddressDesc);
		
		lblPortDesc = new JLabel("(eg.8192)");
		lblPortDesc.setBounds(123, 208, 48, 14);
		contentPane.add(lblPortDesc);
		
		JButton btnLogin = new JButton("Login");
		btnLogin.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				String name=txtName.getText();
				String address=txtAddress.getText();
				int port=Integer.valueOf(txtPort.getText());
				System.out.println("port: "+port);
				login(name,address,port);
			}
		});
		btnLogin.setBounds(102, 283, 89, 23);
		contentPane.add(btnLogin);
	}
	
	private static void checkIfRunning() {
		  try {
		    //Bind to localhost adapter with a zero connection queue 
		    socket = new ServerSocket(PORT,0,InetAddress.getByAddress(new byte[] {127,0,0,1}));
		  }
		  catch (BindException e) {
		    System.err.println("Already running.");
		    System.exit(1);
		  }
		  catch (IOException e) {
		    System.err.println("Unexpected error.");
		    e.printStackTrace();
		    System.exit(2);
		  }
		}
	
	/**
	 * login stuff here...
	 */
	public void login(String name,String address,int port){
		dispose();
		new ClientWindow(name,address,port);
	}
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					checkIfRunning();
					Login frame = new Login();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
