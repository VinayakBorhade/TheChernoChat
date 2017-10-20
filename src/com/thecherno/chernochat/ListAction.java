package com.thecherno.chernochat;

import java.awt.List;
import java.util.ArrayList;

import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.thecherno.chernochat.Prompt;

public class ListAction implements ListSelectionListener{
	public boolean[] value;
	private JList list;
	private ArrayList<Prompt> prompts=new ArrayList<Prompt>();
	
	public ListAction(JList list) {
		value=new boolean[10];
		this.list=list;
		for(int i=0;i<10;i++)
			value[i]=false;
	}
	
	public void valueChanged(ListSelectionEvent e) {
		if(value[e.getFirstIndex()]==false) {
			value[e.getFirstIndex()]=true;
			System.out.println("list item selected: "+e.getLastIndex());
			prompts.add(new Prompt( list.getModel().getElementAt(e.getFirstIndex()).toString() ));
		}
		else if(value[e.getFirstIndex()]==true&&!prompts.get(e.getFirstIndex()).isVisible()) {
			value[e.getFirstIndex()]=false;
			prompts.remove(e.getFirstIndex());
		}
	}
	
}
