/**
 * Automatically generated from WindowBuilder eclipse plugin
 */
package ca.masonx.jircc.gui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class ConfigWindowFrame extends JFrame {
	private static final long serialVersionUID = -4935986132307860192L;
	private boolean hasConfigured = false;
	private String nick;
	private String server;
	private JPanel contentPane;
	private JTextField usernameField;
	private JTextField serverField;

	public ConfigWindowFrame(String dserver, String dnick) {
		nick = dnick;
		server = dserver;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 230, 135);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new GridLayout(3, 3, 0, 0));
		
		usernameField = new JTextField();
		usernameField.setText(dnick);
		contentPane.add(usernameField);
		usernameField.setColumns(10);
		
		serverField = new JTextField();
		serverField.setText(dserver);
		contentPane.add(serverField);
		serverField.setColumns(10);
		
		JButton connectBtn = new JButton("Connect");
		
		connectBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				nick = usernameField.getText();
				server = serverField.getText();
				hasConfigured = true;
			}
		});
		
		contentPane.add(connectBtn);
	}
	
	public boolean hasConfigured() {
		return hasConfigured;
	}
	
	public String getConfiguredNick() {
		return nick;
	}
	
	public String getConfiguredServer() {
		return server;
	}
}
