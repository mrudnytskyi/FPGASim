package sim;

import java.io.File;

import javax.swing.table.AbstractTableModel;

import com.thoughtworks.xstream.XStream;

/**
 * Class represents library of hardware tasks.
 * 
 * @author Mir4ik
 * @version 0.1 10.06.2015
 */
public class Library extends AbstractTableModel {

	private static final long serialVersionUID = -8149438991804788594L;

	public static final String LIBRARY_FILE = "library.xml";

	private final int[][] data;

	public Library() {
		this(Library.LIBRARY_FILE);
	}

	public Library(String path) {
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

	public int getSize() {
		return getRowCount();
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
				System.err.println("Exception " + e.getMessage());
			}
		}
	}
}