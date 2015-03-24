package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

/**
 * Frame for setting properties for vertexes in graph.
 * 
 * @author Mir4ik
 * @version 0.1 24.03.2015
 */
public class PropertiesFrame extends Frame {
	
	private class Ok extends Action {

		private static final long serialVersionUID = -8517570522823621277L;

		private JTable table;
		
		public Ok(JTable table) {
			super("OK", "res\\ok.png", "res\\ok_big.png");
			this.table = table;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO 
		}
	}
	
	private class Cancel extends Action {

		private static final long serialVersionUID = -5138288617849048408L;

		public Cancel() {
			super("Cancel", "res\\cancel.png", "res\\cancel_big.png");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO 
		}
	}

	private static final long serialVersionUID = 5743609407800956841L;
	
	private final JTable table = new JTable();
	
	private final JButton ok = new JButton(new Ok(table));
	
	private final JButton cancel = new JButton(new Cancel());

	public PropertiesFrame() {
		super("Properties");
		setLayout(new BorderLayout());
		setResizable(false);
		setAlwaysOnTop(true);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		add(createContent(), BorderLayout.CENTER);
		init();
		pack();
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		double x = (d.getWidth() - getWidth())/2;
		double y = (d.getHeight() - getHeight())/2;
		setLocation((int) x, (int) y);
	}
	
	private void init() {
		//TODO
	}
	
	private JPanel createContent() {
		JScrollPane pane = new JScrollPane(table);
		JPanel bottom = new JPanel(new GridLayout(1, 2, 50, 20));
		bottom.add(ok);
		bottom.add(cancel);
		bottom.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
		JPanel content = new JPanel(new BorderLayout());
		content.add(pane, BorderLayout.CENTER);
		content.add(bottom, BorderLayout.SOUTH);
		return content;
	}
}