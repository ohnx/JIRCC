/**
 * RootWindowFrame.java
 * 
 * This class describes the main GUI.
 * 
 * Note: some portions of this GUI were created using the WindowBuilder plugin in Eclipse.
 */
package ca.masonx.jircc.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

public class RootWindowFrame extends JFrame {
	private static final long serialVersionUID = -3795903020865438478L;
	private JSplitPane splitView;
	private JSplitPane splitViewTwo;
	private JPanel messagesAndSendContainer;
	private JScrollPane messagesScroll;
	private JScrollPane buffersScroll;
	private JScrollPane namesScroll;
	private JList<String> messages;
	private JList<String> buffers;
	private JList<String> names;
	private JTextField commandTextField;
	private DefaultListModel<String> messagesModel = new DefaultListModel<String>();
	private DefaultListModel<String> buffersModel = new DefaultListModel<String>();
	private DefaultListModel<String> namesModel = new DefaultListModel<String>();
	private String monitoring = "*";
	private boolean hasMonitoringChanged = false;
	private boolean hasRequestedSend = false;

	public RootWindowFrame() {
		Font mono = new Font("monospaced", Font.PLAIN, 13);
		Border none = BorderFactory.createEmptyBorder();
		
		/* messages Management */
		messagesScroll = new JScrollPane();
		messages = new JList<String>(messagesModel);
		messagesScroll.setViewportView(messages);
		messages.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		messages.setLayoutOrientation(JList.VERTICAL);
		messages.setFont(mono);
		
		/* messages and send container */
		messagesAndSendContainer = new JPanel();
		GridBagLayout gblMain = new GridBagLayout();
		gblMain.columnWidths = new int[] {300};
		gblMain.rowHeights = new int[] {420, 10};
		gblMain.columnWeights = new double[]{1.0};
		gblMain.rowWeights = new double[]{1.0, 0.0};
		messagesAndSendContainer.setLayout(gblMain);
		messagesAndSendContainer.setBorder(none);
		
		/* Grid bag for messagesScroll */
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 0;
		messagesAndSendContainer.add(messagesScroll, gbc);
		
		/* Panel for the input/button split */
		JPanel panel = new JPanel();
		gbc.gridy = 1;
		messagesAndSendContainer.add(panel, gbc);
		GridBagLayout gblSecondary = new GridBagLayout();
		gblSecondary.columnWidths = new int[] {250,50};
		gblSecondary.rowHeights = new int[] {10};
		gblSecondary.columnWeights = new double[]{1.0, 0.0};
		gblSecondary.rowWeights = new double[]{1.0};
		panel.setLayout(gblSecondary);
		panel.setBorder(none);
		panel.setBorder(none);
		
		/* input setup */
		commandTextField = new JTextField();
		gbc.gridx = 0;
		gbc.gridy = 0;
		panel.add(commandTextField, gbc);
		commandTextField.setColumns(10);
		/* enter press listener */
		commandTextField.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {}
			public void keyPressed(KeyEvent e) {}
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					hasRequestedSend = true;
				}
			}
		});
		
		/* button setup */
		JButton btnNewButton = new JButton("Send");
		/* button click listener */
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				hasRequestedSend = true;
			}
		});
		gbc.gridx = 1;
		gbc.gridy = 0;
		panel.add(btnNewButton, gbc);
		
		/* buffers Management */
		buffersScroll = new JScrollPane();
		buffers = new JList<String>(buffersModel);
		buffersScroll.setViewportView(buffers);
		buffers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		buffers.setLayoutOrientation(JList.VERTICAL);
		buffers.setFont(mono);
		
		/* buffers listening for a click */
		buffers.addMouseListener(new MouseAdapter() {
			@SuppressWarnings("rawtypes")
			public void mouseClicked(MouseEvent evt) {
				JList list = (JList)evt.getSource();
				if (evt.getClickCount() == 2) { /* double click */
			        int index = list.locationToIndex(evt.getPoint());
			        String nmonitoring = (String) list.getModel().getElementAt(index);
			        if (!nmonitoring.equals(monitoring)) {
			        	hasMonitoringChanged = true;
			        	monitoring = nmonitoring;
			        }
			    }
			}
		});
		
		/* names Management */
		namesScroll = new JScrollPane();
		names = new JList<String>(namesModel);
		namesScroll.setViewportView(names);
		names.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		names.setLayoutOrientation(JList.VERTICAL);
		names.setFont(mono);
		
		/* names listening for a click */
		names.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {/*
				JList list = (JList)evt.getSource();
				if (evt.getClickCount() == 2) { // double click
			        int index = list.locationToIndex(evt.getPoint());
			        String nmonitoring = (String) list.getModel().getElementAt(index);
			        if (!nmonitoring.equals(monitoring)) {
			        	hasMonitoringChanged = true;
			        	monitoring = nmonitoring;
			        	System.out.println("Changed monitoring " + monitoring);
			        }
			    }*/
			}
		});
		
		/* Buffers + Messages Management */
		splitView = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buffersScroll, messagesAndSendContainer);
		splitView.setDividerLocation(150);
		splitView.setVisible(true);
		splitView.setBorder(none);
		
		/* (Buffers + Messages) + Names Management */
		splitViewTwo = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, splitView, namesScroll);
		splitViewTwo.setDividerLocation(450);
		splitViewTwo.setVisible(true);
		splitViewTwo.setBorder(none);
		
		/* panel minimum sizes */
		buffersScroll.setMinimumSize(new Dimension(150, 200));
		messagesAndSendContainer.setMinimumSize(new Dimension(300, 200));
		namesScroll.setMinimumSize(new Dimension(150, 200));
		
		/* main panel management */
		this.setLayout(new GridLayout(1,2));
		this.add(splitViewTwo, BorderLayout.CENTER);
    }
	
	public void clearMessagesItems() {
		messagesModel.clear();
	}
	
	public void updateMessagesItems(final String items[]) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				for (String s : items) {
					messagesModel.addElement(s);
				}
			}
		});
	}
	
	public void setBuffersItems(final String items[]) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				buffersModel.clear();
				for (String s : items) {
					buffersModel.addElement(s);
				}
			}
		});
	}
	
	public void setParticipantsItems(final String[] participants) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				namesModel.clear();
				for (String s : participants) {
					namesModel.addElement(s);
				}
			}
		});
	}
	
	public String getMonitoring() {
		return monitoring;
	}
	
	public boolean monitoringChanged() {
		if (hasMonitoringChanged) {
			hasMonitoringChanged = false;
			return true;
		} else {
			return false;
		}
	}
	
	public boolean hasRequestedSend() {
		return hasRequestedSend;
	}
	
	public String getSendString() {
		hasRequestedSend = false;
		return commandTextField.getText();
	}
}
