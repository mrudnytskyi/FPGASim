package gui;

import com.thoughtworks.xstream.XStream;
import sim.Library;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.FileWriter;

/**
 * Frame for displaying library data.
 */
public class LibraryFrame extends Frame {

	private static final long serialVersionUID = 5416000952627581896L;
	private final JTable table = new JTable();
	private final JButton ok = new JButton(new Ok(table));
	private final JButton cancel = new JButton(new Cancel());

	public LibraryFrame() {
		super("Library");
		setLayout(new BorderLayout());
		setResizable(false);
		setAlwaysOnTop(true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		add(createContent(), BorderLayout.CENTER);
		init();
		pack();
		moveToScreenCenter();
	}

	private void init() {
		try {
			table.setModel(new Library());
		} catch (Exception e) {
			showError("Exception" + e.getMessage());
		}
	}

	private JPanel createContent() {
		JScrollPane pane = new JScrollPane(table);
		JPanel bottom = new JPanel(new GridLayout(1, 2, 50, 0));
		bottom.add(ok);
		bottom.add(cancel);
		bottom.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
		JPanel content = new JPanel(new BorderLayout());
		content.add(pane, BorderLayout.CENTER);
		content.add(bottom, BorderLayout.SOUTH);
		return content;
	}

	private class Ok extends Action {

		private static final long serialVersionUID = -2799992289456640097L;

		private final JTable table;

		public Ok(JTable table) {
			super("OK", "res\\ok.png", "res\\ok_big.png");
			this.table = table;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			try (FileWriter fw = new FileWriter(Library.LIBRARY_FILE)) {
				new XStream().toXML(((Library) table.getModel()).getData(), fw);
			} catch (Exception ex) {
				showError("Exception " + ex.getMessage());
			}
			setVisible(false);
		}
	}

	private class Cancel extends Action {

		private static final long serialVersionUID = -7291044240556796475L;

		public Cancel() {
			super("Cancel", "res\\cancel.png", "res\\cancel_big.png");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			setVisible(false);
		}
	}
}