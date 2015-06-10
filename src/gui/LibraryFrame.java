package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.table.AbstractTableModel;

import com.thoughtworks.xstream.XStream;

/**
 * Frame for displaying library data.
 * 
 * @author Mir4ik
 * @version 0.1 17.03.2015
 */
public class LibraryFrame extends Frame {

	private class Ok extends Action {

		private static final long serialVersionUID = -2799992289456640097L;

		private final JTable table;

		public Ok(JTable table) {
			super("OK", "res\\ok.png", "res\\ok_big.png");
			this.table = table;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			try (FileWriter fw = new FileWriter(LibraryFrame.LIBRARY_FILE)) {
				new XStream().toXML(
						((LibraryModel) table.getModel()).getData(), fw);
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

	private class LibraryModel extends AbstractTableModel {

		private static final long serialVersionUID = -8149438991804788594L;

		private final int[][] data;

		public LibraryModel(String path) {
			data = (int[][]) new XStream().fromXML(new File(path));
			if ((data.length == 0) || (data[0].length != 3)) {
				throw new IllegalArgumentException("Wrong library file!");
			}
		}

		public int[][] getData() {
			return data;
		}

		@Override
		public int getRowCount() {
			return data.length;
		}

		@Override
		public int getColumnCount() {
			return 4;
		}

		@Override
		public String getColumnName(int columnIndex) {
			switch (columnIndex) {
			case 0:
				return "TASK";
			case 1:
				return "BYTESTREAM_WORDS";
			case 2:
				return "WORK_TIME";
			case 3:
				return "DATA_WORDS";
			}
			return null;
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return true;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			if (columnIndex == 0) {
				return rowIndex;
			}
			return data[rowIndex][columnIndex - 1];
		}

		@Override
		public void setValueAt(Object value, int row, int column) {
			if (column != 0) {
				try {
					data[row][column - 1] = Integer.valueOf((String) value);
				} catch (Exception e) {
					showError("Exception " + e.getMessage());
				}
			}
		}
	}

	private static final long serialVersionUID = 5416000952627581896L;

	private final JTable table = new JTable();

	private final JButton ok = new JButton(new Ok(table));

	private final JButton cancel = new JButton(new Cancel());

	public static final String LIBRARY_FILE = "library.xml";

	public LibraryFrame() {
		super("Library");
		setLayout(new BorderLayout());
		setResizable(false);
		setAlwaysOnTop(true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		add(createContent(), BorderLayout.CENTER);
		init();
		pack();
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		double x = (d.getWidth() - getWidth()) / 2;
		double y = (d.getHeight() - getHeight()) / 2;
		setLocation((int) x, (int) y);
	}

	private void init() {
		try {
			table.setModel(new LibraryModel(LibraryFrame.LIBRARY_FILE));
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
}