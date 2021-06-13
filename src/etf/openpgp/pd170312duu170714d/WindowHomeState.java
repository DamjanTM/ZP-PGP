package etf.openpgp.pd170312duu170714d;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

public class WindowHomeState extends WindowState {
	
	private JPanel centerPanel = new JPanel();
	private JButton sendBut = new JButton("Posalji Poruku");
	private JButton recieveBut = new JButton("Primi Poruku");
	
	public WindowHomeState(Window window) {
		super(window);
	}

	@Override
	public void addCenterPanel(Message msg_) {
		// SEND
		sendBut.addActionListener((o) -> {
				myWindow.changeState(States.SEND_MSG);
		});
		
		// RECIEVE
		recieveBut.addActionListener((o) -> {
			
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
			    
			    msg_.data = Files.readString(directory).getBytes();
			} catch (FileNotFoundException fnfe) {
				System.out.println("Coldn't find file!");
			} catch (IOException e) {
				System.out.println("Error converting String to Path!");
				e.printStackTrace();
			};
			myWindow.changeState(States.RECIEVE);
			});
		
		//Finalize
		
		centerPanel.add(sendBut);
		centerPanel.add(recieveBut);
		
		myWindow.add(centerPanel, BorderLayout.CENTER);
	}

}
