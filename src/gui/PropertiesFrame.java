package gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;

/**
 * Frame for setting properties for vertexes in graph.
 * 
 * @author Mir4ik
 * @version 0.1 24.03.2015
 */
public class PropertiesFrame extends Frame {

	private class Ok extends Action {

		private static final long serialVersionUID = -8517570522823621277L;

		private final JTable table;

		public Ok(JTable table) {
			super("OK", "res\\ok.png", "res\\ok_big.png");
			this.table = table;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (((PropertiesModel) table.getModel()).isEmptyProperties()) {
				showWarning("Empty lines!");
			} else {
				observer.update(null,
						((PropertiesModel) table.getModel()).getData());
				setVisible(false);
			}
		}
	}

	private class Cancel extends Action {

		private static final long serialVersionUID = -5138288617849048408L;

		public Cancel() {
			super("Cancel", "res\\cancel.png", "res\\cancel_big.png");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			setVisible(false);
		}
	}

	private class PropertiesModel extends AbstractTableModel {

		private static final long serialVersionUID = -3160038264112570310L;

		private final int[] data;

		public PropertiesModel(int[] data) {
			this.data = data;
		}

		public boolean isEmptyProperties() {
			for (int i : data) {
				if (i == -1) {
					return true;
				}
			}
			return false;
		}

		public int[] getData() {
			return data;
		}

		@Override
		public int getRowCount() {
			return data.length;
		}

		@Override
		public int getColumnCount() {
			return 2;
		}

		@Override
		public String getColumnName(int columnIndex) {
			if (columnIndex == 0) {
				return "Vertex";
			} else {
				return "Hardware Task";
			}
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return columnIndex == 1 ? true : false;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			if (columnIndex == 0) {
				return rowIndex;
			} else {
				return data[rowIndex];
			}
		}

		@Override
		public void setValueAt(Object value, int rowIndex, int columnIndex) {
			if (columnIndex == 1) {
				data[rowIndex] = Integer.parseInt((String) value);
			}
		}
	}

	private static final long serialVersionUID = 5743609407800956841L;

	private final JTable table = new JTable();

	private final JButton ok = new JButton(new Ok(table));

	private final JButton cancel = new JButton(new Cancel());

	private final Observer observer;

	public PropertiesFrame(int tasksCount, int[] data, Observer o) {
		super("Properties");
		observer = o;
		setLayout(new BorderLayout());
		setResizable(false);
		setAlwaysOnTop(true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		add(createContent(), BorderLayout.CENTER);
		init(tasksCount, data);
		pack();
		moveToScreenCenter();
	}

	private void init(int tasksCount, int[] data) {
		table.setModel(new PropertiesModel(data));
		String[] items = new String[tasksCount];
		for (int i = 0; i < items.length; i++) {
			items[i] = String.valueOf(i);
		}
		JComboBox<String> comboBox = new JComboBox<String>(items);
		DefaultCellEditor editor = new DefaultCellEditor(comboBox);
		TableColumnModel tcm = table.getColumnModel();
		for (int i = 0; i < tcm.getColumnCount(); i++) {
			tcm.getColumn(i).setCellEditor(editor);
		}
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