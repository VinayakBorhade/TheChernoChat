package com.thecherno.chernochat;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;

public class Prompt extends JFrame {

	private JPanel contentPane;
	private int promptIndex;
	
	public Prompt(String name/*,int promptIndex*/) {
		setVisible(true);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setSize(400,200);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel label = new JLabel("");
		label.setText("Chat with "+name);
		label.setBounds(95, 35, 194, 14);
		contentPane.add(label);
		
		/*this.promptIndex=promptIndex;*/
	}
}
