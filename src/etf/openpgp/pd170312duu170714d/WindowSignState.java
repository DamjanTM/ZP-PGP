package etf.openpgp.pd170312duu170714d;

import java.awt.BorderLayout;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;

import etf.openpgp.pd170312duu170714d.WindowState.States;

public class WindowSignState extends WindowState {
	private JLabel Title = new JLabel("Potpisivanje");
	private JCheckBox doSignature = new JCheckBox();
	private JPanel panel1_1 = new JPanel();
	private JLabel keyLabel = new JLabel("Kljuc: ");
	private JTextArea keyArea = new JTextArea(1,50);
	private JScrollPane scroll = new JScrollPane(keyArea);
	private JButton importBut = new JButton("Uvezi Kljuc");
	private JButton exportBut = new JButton("Izvezi Kljuc");
	private JButton genBut = new JButton("Generisi Kljuc");
	private JLabel sizeLabel = new JLabel("Velicina:");
	private JRadioButton rbutS1 = new JRadioButton("1024");
	private JRadioButton rbutS2 = new JRadioButton("2048");
	private ButtonGroup bGroup = new ButtonGroup();
	private JPanel panel1_2 = new JPanel();
	
	private JLabel Title2 = new JLabel("Enkripcija");
	private JCheckBox doEncrypt = new JCheckBox();
	private JPanel panel2_1 = new JPanel();
	private JTextArea keyArea2 = new JTextArea(1,50);
	private JScrollPane scroll2 = new JScrollPane(keyArea2);
	private JButton importBut2 = new JButton("Uvezi Kljuc");
	private JButton exportBut2 = new JButton("Izvezi Kljuc");
	private JButton genBut2 = new JButton("Generisi Kljuc");
	private JRadioButton rbutE1 = new JRadioButton("1024");
	private JRadioButton rbutE2 = new JRadioButton("2048");
	private JRadioButton rbutE3 = new JRadioButton("4096");
	private ButtonGroup bGroup2 = new ButtonGroup();
	private JPanel panel2_2 = new JPanel();
	
	private JButton nextBut = new JButton("Dalje");
	
	private JPanel topPanel = new JPanel();
	private JPanel botPanel = new JPanel();
	
	private JPanel mainPanel = new JPanel();
	
	public WindowSignState(Window win_) {
		super(win_);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void addCenterPanel(Message msg_) {
		// DSA
		// IMPORT KEY
		importBut.addActionListener((o) -> {
			try {
				Path directory;

				JFileChooser chooser = new JFileChooser(myWindow.startingFolder);
			    FileNameExtensionFilter filter = new FileNameExtensionFilter(
			        "TXT Files", "asc");
			    chooser.setFileFilter(filter);
			    int returnVal = chooser.showOpenDialog(new JPanel());
			    if(returnVal == JFileChooser.APPROVE_OPTION) {
			    	directory = Path.of(chooser.getSelectedFile().getPath());
			    	myWindow.startingFolder = chooser.getSelectedFile().getParentFile().getPath();
			    }
			    else throw new FileNotFoundException();
			    myWindow.dsa.import_keypair(directory.toString());
			} catch (FileNotFoundException fnfe) {
				System.out.println("Coldn't find file!");
			}
			
			keyArea.setText(myWindow.dsa.returnPrivateKey());
			exportBut.setEnabled(true);
			myWindow.pack();
		});
		
		// EXPORT KEY
		exportBut.addActionListener((o) -> {
			myWindow.dsa.export_keypair();
			exportBut.setEnabled(false);
		});
				
		// GENERATE KEY
		genBut.addActionListener((o) -> {
			int value = 1024;
			if (rbutS2.isSelected()) value = 2048;
			myWindow.dsa.generate_keypair(value);
			keyArea.setText(myWindow.dsa.returnPrivateKey());
			exportBut.setEnabled(true);
			myWindow.pack();
		});
		
		
		// ElGamal
		// IMPORT KEY
		importBut2.addActionListener((o) -> {
			try {
				Path directory;

				JFileChooser chooser = new JFileChooser(myWindow.startingFolder);
			    FileNameExtensionFilter filter = new FileNameExtensionFilter(
			        "TXT Files", "asc");
			    chooser.setFileFilter(filter);
			    int returnVal = chooser.showOpenDialog(new JPanel());
			    if(returnVal == JFileChooser.APPROVE_OPTION) {
			    	directory = Path.of(chooser.getSelectedFile().getPath());
			    	myWindow.startingFolder = chooser.getSelectedFile().getParentFile().getPath();
			    }
			    else throw new FileNotFoundException();
			    myWindow.elgamal.import_keypair(directory.toString());
			} catch (FileNotFoundException fnfe) {
				System.out.println("Coldn't find file!");
			}
			
			keyArea2.setText(myWindow.elgamal.returnPublicKey());
			exportBut2.setEnabled(true);
			myWindow.pack();
		});
		
		// EXPORT KEY
		exportBut2.addActionListener((o) -> {
			myWindow.elgamal.export_keypair();
			exportBut2.setEnabled(false);
		});
				
		// GENERATE KEY
		genBut2.addActionListener((o) -> {
			int value = 1024;
			if (rbutE2.isSelected()) value = 2048;
			else if (rbutE3.isSelected()) value = 4096;
			myWindow.elgamal.generate_keypair(value);
			keyArea2.setText(myWindow.elgamal.returnPublicKey());
			exportBut2.setEnabled(true);
			myWindow.pack();
		});
		
		// NEXT
		nextBut.addActionListener((o) -> {
			if (doSignature.isSelected()) {
				try {
					msg_.data=myWindow.dsa.generateSignature(msg_.data);
				} catch (GeneralSecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (doEncrypt.isSelected()) {
				try {
					msg_.data=myWindow.elgamal.encrypt(msg_.data);
				} catch (GeneralSecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			myWindow.changeState(States.SEND_FIN);
		});
	
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		
		doSignature.setSelected(true);
		rbutS1.setSelected(true);
		bGroup.add(rbutS1);
		bGroup.add(rbutS2);
		
		Title.setFont(new Font(Title.getFont().getName(), Font.PLAIN, 24));
		panel1_1.add(Title);
		panel1_1.add(doSignature);
		
		keyArea.setEditable(false);
		exportBut.setEnabled(false);
		panel1_2.add(keyLabel);
		panel1_2.add(scroll);
		panel1_2.add(importBut);
		panel1_2.add(exportBut);
		panel1_2.add(genBut);
		panel1_2.add(sizeLabel);
		panel1_2.add(rbutS1);
		panel1_2.add(rbutS2);
		
		
		doEncrypt.setSelected(true);
		rbutE1.setSelected(true);
		bGroup2.add(rbutE1);
		bGroup2.add(rbutE2);
		bGroup2.add(rbutE3);
		
		Title2.setFont(new Font(Title.getFont().getName(), Font.PLAIN, 24));
		panel2_1.add(Title2);
		panel2_1.add(doEncrypt);
		
		keyArea2.setEditable(false);
		exportBut2.setEnabled(false);
		panel2_2.add(keyLabel);
		panel2_2.add(scroll2);
		panel2_2.add(importBut2);
		panel2_2.add(exportBut2);
		panel2_2.add(genBut2);
		panel2_2.add(sizeLabel);
		panel2_2.add(rbutE1);
		panel2_2.add(rbutE2);
		panel2_2.add(rbutE3);
		
		topPanel.add(panel1_1, BorderLayout.NORTH);
		topPanel.add(panel1_2, BorderLayout.SOUTH);
		
		botPanel.add(panel2_1, BorderLayout.NORTH);
		botPanel.add(panel2_2, BorderLayout.SOUTH);
		
		mainPanel.add(topPanel);
		mainPanel.add(botPanel);
		mainPanel.add(nextBut);
		
		myWindow.add(mainPanel, BorderLayout.CENTER);
	}

}
