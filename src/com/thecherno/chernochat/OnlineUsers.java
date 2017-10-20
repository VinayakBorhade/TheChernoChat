package com.thecherno.chernochat;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JList;
import java.awt.GridBagConstraints;

public class OnlineUsers extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JList list;
	ListAction la; 

	public OnlineUsers() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(200,320);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		setTitle("Online Users");
		
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0};
		gbl_contentPane.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		list = new JList<String>();
		GridBagConstraints gbc_list = new GridBagConstraints();
		gbc_list.fill = GridBagConstraints.BOTH;
		gbc_list.gridx = 0;
		gbc_list.gridy = 0;
		JScrollPane p=new JScrollPane();
		p.setViewportView(list);
		contentPane.add(p, gbc_list);
		list.setFont(new Font("verdana",0,24));
		
		la=new ListAction(list);
	}

	public void update(String[] users){
		list.setListData(users);
		
		list.addListSelectionListener(la);
		
		// ListAction la=new ListAction(list);
		/*list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				System.out.println("list item selected: "+e.getLastIndex());
				//System.out.println(list.getModel().getElementAt(e.getFirstIndex()));
				new Prompt( list.getModel().getElementAt(e.getFirstIndex()).toString() );
			}
		});*/
	}
	
}