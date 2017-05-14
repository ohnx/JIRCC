package ca.masonx.jircc.gui;

import java.awt.BorderLayout;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

public class RootWindowFrame extends JFrame {
	private static final long serialVersionUID = -3795903020865438478L;
	private JScrollPane mainPanel;
	private JList<String> messages;
	private DefaultListModel<String> demoList = new DefaultListModel<String>();
	
	public RootWindowFrame() {
		mainPanel = new JScrollPane();
		messages = new JList<String>(demoList);
		mainPanel.setViewportView(messages);
		
		/* JList Management */
		messages.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		messages.setLayoutOrientation(JList.VERTICAL);
		
		/* Main panel Management */
		mainPanel.setVisible(true);
		this.add(mainPanel, BorderLayout.CENTER);
    }
	
	public void updateBufferItems(String items[]) {
		for (String s : items) {
			demoList.addElement(s);
		}
		this.pack();
	}
}
