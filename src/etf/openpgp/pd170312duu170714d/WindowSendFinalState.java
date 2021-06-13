package etf.openpgp.pd170312duu170714d;

import java.awt.BorderLayout;
import java.awt.Font;
import java.security.GeneralSecurityException;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import etf.openpgp.pd170312duu170714d.WindowState.States;

public class WindowSendFinalState extends WindowState {
	private JLabel Title1 = new JLabel("Zip");
	private JCheckBox doZip = new JCheckBox();
	private JPanel panel1 = new JPanel();
	
	private JLabel Title2 = new JLabel("Enkripcija");
	private JCheckBox doEncrypt = new JCheckBox();
	private JPanel panel2 = new JPanel();
	
	private JLabel choose = new JLabel("Algoritam:");
	private JRadioButton rbutDES = new JRadioButton("DES");
	private JRadioButton rbutIDEA = new JRadioButton("IDEA");
	private ButtonGroup bGroup = new ButtonGroup();
	private JPanel panel3 = new JPanel();
	
	private JLabel Title3 = new JLabel("Serijalizacija");
	private JCheckBox doSerialize = new JCheckBox();
	private JPanel panel4 = new JPanel();
	
	private JButton nextBut = new JButton("Sacuvaj");
	
	private JPanel mainPanel = new JPanel();
	public WindowSendFinalState(Window win_) {
		super(win_);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void addCenterPanel(Message msg_) {
		
		// NEXT
				nextBut.addActionListener((o) -> {
					if (doZip.isSelected()) {
						msg_.data = myWindow.zip.compress(msg_.data, "message");
					}
					if (doEncrypt.isSelected()) {
						if (rbutDES.isSelected()) {
							msg_.data = myWindow.des.encrypt(key_, input_)
						} else {
							
						}
					}
					if (doSerialize.isSelected()) {
						
					}
					myWindow.changeState(States.HOME);
				});
			
				mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
				
				doZip.setSelected(true);
				doEncrypt.setSelected(true);
				doSerialize.setSelected(true);
				
				rbutDES.setSelected(true);
				bGroup.add(rbutDES);
				bGroup.add(rbutIDEA);
				
				Title1.setFont(new Font(Title1.getFont().getName(), Font.PLAIN, 24));
				Title2.setFont(new Font(Title2.getFont().getName(), Font.PLAIN, 24));
				Title3.setFont(new Font(Title3.getFont().getName(), Font.PLAIN, 24));
				
				panel1.add(Title1);
				panel1.add(doZip);
				
				panel2.add(Title2);
				panel2.add(doEncrypt);
				
				panel3.add(choose);
				panel3.add(rbutDES);
				panel3.add(rbutIDEA);
				
				panel4.add(Title3);
				panel4.add(doSerialize);
				
				
				mainPanel.add(panel1);
				mainPanel.add(panel2);
				mainPanel.add(panel3);
				mainPanel.add(panel4);
				mainPanel.add(nextBut);
				
				myWindow.add(mainPanel, BorderLayout.CENTER);
	}

}
