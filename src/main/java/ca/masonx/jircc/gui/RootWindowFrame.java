/**
 * RootWindowFrame.java
 * 
 * This class describes the main GUI.
 */
package ca.masonx.jircc.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

public class RootWindowFrame extends JFrame {
	private static final long serialVersionUID = -3795903020865438478L;
	private JSplitPane splitView;
	private JSplitPane splitViewTwo;
	private JScrollPane messagesScroll;
	private JScrollPane buffersScroll;
	private JScrollPane namesScroll;
	private JList<String> messages;
	private JList<String> buffers;
	private JList<String> names;
	private DefaultListModel<String> messagesModel = new DefaultListModel<String>();
	private DefaultListModel<String> buffersModel = new DefaultListModel<String>();
	private DefaultListModel<String> namesModel = new DefaultListModel<String>();
	private String monitoring = "*";
	private boolean hasMonitoringChanged = false;

	public RootWindowFrame() {
		Font mono = new Font("monospaced", Font.PLAIN, 13);
		/* messages Management */
		messagesScroll = new JScrollPane();
		messages = new JList<String>(messagesModel);
		messagesScroll.setViewportView(messages);
		messages.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		messages.setLayoutOrientation(JList.VERTICAL);
		messages.setFont(mono);
		
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
			        	System.out.println("Changed monitoring " + monitoring);
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
		
		/* Buffers + Messages Management */
		splitView = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buffersScroll, messagesScroll);
		splitView.setDividerLocation(150);
		splitView.setVisible(true);
		
		/* (Buffers + Messages) + Names Management */
		splitViewTwo = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, splitView, namesScroll);
		splitViewTwo.setDividerLocation(450);
		splitViewTwo.setVisible(true);
		
		/* panel minimum sizes */
		buffersScroll.setMinimumSize(new Dimension(150, 200));
		messagesScroll.setMinimumSize(new Dimension(300, 200));
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
}
