/**
 * Automatically generated from WindowBuilder eclipse plugin
 */
package ca.masonx.jircc.gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import java.awt.GridLayout;
import javax.swing.JButton;

public class ConfigWindowFrame2 extends JFrame {
	private static final long serialVersionUID = -4656935099023413900L;
	private JPanel contentPane;
	private JTextField usernameField;
	private JTextField serverField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ConfigWindowFrame2 frame = new ConfigWindowFrame2();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ConfigWindowFrame2() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 230, 135);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new GridLayout(3, 3, 0, 0));
		
		usernameField = new JTextField();
		usernameField.setText("Username");
		contentPane.add(usernameField);
		usernameField.setColumns(10);
		
		serverField = new JTextField();
		serverField.setText("Server");
		contentPane.add(serverField);
		serverField.setColumns(10);
		
		JButton connectBtn = new JButton("Connect");
		contentPane.add(connectBtn);
	}

}
