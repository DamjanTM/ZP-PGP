package etf.openpgp.pd170312duu170714d;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Window extends JFrame {
	
	String startingFolder = System.getProperty("user.home")+"\\Desktop";
	
	private WindowState state=new WindowHomeState(this);
	private Message msg= new Message();
	
	public Window() {
		super("PGP Simulator");
		setResizable(true);
		state.addCenterPanel(msg);
		addMenu();
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setLocationByPlatform(true);
		setVisible(true);
		
		pack();
		this.setVisible(true);
	}
	
	private void addMenu() {
		MenuBar bar = new MenuBar();
		Menu menu = new Menu("Opcije");
		Menu export = new Menu("Eksport");
		setMenuBar(bar); bar.add(menu); bar.add(export);
		
		MenuItem readMsg = new MenuItem(
				"Primi Poruku", new MenuShortcut('R'));
		menu.add(readMsg);
//		readMsg.addActionListener(
//			e->{
//				try {
//					msg = mf.read_composition();
//					if (play.getText().equals("Stop")) {piano.stop();play.setText("Play");}
//					add(comp, BorderLayout.NORTH);
//					piano.newComp(comp);
//					pack();
//				} catch (FileNotFoundException fnfe) {
//					System.out.println("Coldn't find file!");
//				};
//			});
				
		MenuItem txt = new MenuItem(
				"Export to TXT file");
		export.add(txt);
//		txt.addActionListener(e->{
//			String result = JOptionPane.showInputDialog(this, "Enter composition name:");
//			mf.output(comp,result+".txt","");
//		});
			
		menu.addSeparator();
		
		MenuItem close = new MenuItem(
		"Close",new MenuShortcut('Z'));
		menu.add(close);
		close.addActionListener(e->{dispose();});
	}

	void changeState(WindowState.States newState_) {
		switch(newState_) {
		case HOME:
			state = new WindowHomeState(this); 
			break;
		case RECIEVE:
			state = new WindowHomeState(this); 
			break;
		case SEND_1:
			state = new WindowSendState(this); 
			break;
		case SEND_2:
			state = new WindowHomeState(this); 
			break;
		}
		getContentPane().removeAll();
		state.addCenterPanel(msg);
		pack();
	}
}
