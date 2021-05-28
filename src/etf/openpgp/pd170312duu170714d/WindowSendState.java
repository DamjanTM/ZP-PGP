package etf.openpgp.pd170312duu170714d;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import etf.openpgp.pd170312duu170714d.WindowState.States;

public class WindowSendState extends WindowState {
	
	private JPanel mainPanel = new JPanel();
	private JPanel topPanel = new JPanel();
	private JPanel centerPanel = new JPanel();
	private JPanel bottomPanel = new JPanel();
	private JButton importBut = new JButton("Procitaj tekst iz fajla");
	private JButton nextBut = new JButton("Dalje");
	private JLabel labelText = new JLabel("Text:");
	private JTextArea textArea = new JTextArea(30,50);
	private JScrollPane scroll = new JScrollPane(textArea);
	
	public WindowSendState(Window window) {
		super(window);
	}
	
	@Override
	public void addCenterPanel(Message msg_) {
		// IMPORT
		importBut.addActionListener((o) -> {
			
			try {
				Path directory;

				BufferedReader file;
				JFileChooser chooser = new JFileChooser(myWindow.startingFolder);
			    FileNameExtensionFilter filter = new FileNameExtensionFilter(
			        "TXT Files", "txt");
			    chooser.setFileFilter(filter);
			    //chooser.setCurrentDirectory(new File("C:\\\\Users\\\\Damjan\\\\Desktop\\\\input"));
			    int returnVal = chooser.showOpenDialog(new JPanel());
			    if(returnVal == JFileChooser.APPROVE_OPTION) {
			    	directory = Path.of(chooser.getSelectedFile().getPath());
			    	myWindow.startingFolder = chooser.getSelectedFile().getParentFile().getPath();
			    }
			    else throw new FileNotFoundException();
			    
			    msg_.text = Files.readString(directory);
			    textArea.setText(msg_.text);
			} catch (FileNotFoundException fnfe) {
				System.out.println("Coldn't find file!");
			} catch (IOException e) {
				System.out.println("Error converting String to Path!");
				e.printStackTrace();
			};
			});
		
		// NEXT
		nextBut.addActionListener((o) -> {
			//TO-DO Provera
			msg_.text = textArea.getText();
			myWindow.changeState(States.SEND_2);
		});
		
		//Finalize
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		topPanel.add(labelText);
		centerPanel.add(scroll);
		bottomPanel.add(importBut);
		bottomPanel.add(nextBut);
		
		mainPanel.add(topPanel, BorderLayout.NORTH);
		mainPanel.add(centerPanel, BorderLayout.CENTER);
		mainPanel.add(bottomPanel, BorderLayout.SOUTH);
		
		myWindow.add(mainPanel, BorderLayout.CENTER);
	}

}
