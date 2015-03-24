package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;

/**
 * Frame for adding and removing edges in graph.
 * 
 * @author Mir4ik
 * @version 0.1 17.03.2015
 */
public class LinkerFrame extends Frame {
	
	private class Add extends Action {

		private static final long serialVersionUID = 6237327684509614305L;
		
		private final JTable table; 

		public Add(JTable table) {
			super("Add", "res\\add.png", "res\\add_big.png");
			this.table = table;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			LinkerModel model = (LinkerModel) table.getModel();
			model.addRow();
			model.fireTableRowsInserted(0, model.getRowCount());
		}
	}
	
	private class Remove extends Action {

		private static final long serialVersionUID = -8727965290316548686L;
		
		private final JTable table; 
		
		public Remove(JTable table) {
			super("Remove", "res\\remove.png", "res\\remove_big.png");
			this.table = table;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			LinkerModel model = (LinkerModel) table.getModel();
			model.removeRow(table.getSelectedRow());
			model.fireTableRowsDeleted(0, model.getRowCount());
		}
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
			if (((LinkerModel) table.getModel()).isEmptyTransitions()) {
				showWarning("Ignoring changes because of empty lines!");
			} else {
				LinkerFrame.this.observer.update(null, 
						((LinkerModel) table.getModel()).getData());
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
	
	private class LinkerModel extends AbstractTableModel {
		//TODO add one more column

		private static final long serialVersionUID = 490187158106065021L;
		
		private final List<int[]> data = new ArrayList<int[]>();

		public LinkerModel(int[][] transitions) {
			for (int i = 0; i < transitions.length; i++) {
				for (int j = i; j < transitions[i].length; j++) {
					if (transitions[i][j] == 1) {
						data.add(new int[] {i, j});
					}
				}
			}
		}
		
		public List<int[]> getData() {
			return data;
		}
		
		public boolean isEmptyTransitions() {
			for (int[] i : data) {
				if (i.length == 0) {
					return true;
				}
			}
			return false;
		}
		
		@Override
		public int getRowCount() {
			return data.size();
		}

		@Override
		public int getColumnCount() {
			return 2;
		}

		@Override
		public String getColumnName(int columnIndex) {
			if (columnIndex == 0) {
				return "From";
			} else {
				return "To";
			}
		}

		@Override
		public boolean isCellEditable(int rowIndex, int colunIndex) {
			return true;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			int[] row = data.get(rowIndex);
			if (row.length == 0) {
				return "";
			}
			return row[columnIndex];
		}

		@Override
		public void setValueAt(Object value, int rowIndex, int columnIndex) {
			if (value != null) {
				int[] row = data.get(rowIndex);
				if (row.length == 0) {
					row = new int[2];
				}
				row[columnIndex] = Integer.parseInt((String) value);
				data.set(rowIndex, row); 
			}
		}
		
		public void addRow() {
			data.add(new int[] {});
		}
		
		public void removeRow(int index) {
			if (index >= 0 && index < getRowCount()) {
				data.remove(index);
			}
		}
	}
	
	private static final long serialVersionUID = 2071440340003245390L;
	
	private final JTable table = new JTable();
	
	private final JButton add = new JButton(new Add(table));
	
	private final JButton remove = new JButton(new Remove(table));
	
	private final JButton ok = new JButton(new Ok(table));
	
	private final JButton cancel = new JButton(new Cancel());
	
	private final Observer observer;

	/**
	 * TODO vertexCount delete?
	 */
	public LinkerFrame(int vertexesCount, int[][] transtions, Observer o) {
		super("Links");
		this.observer = o;
		setLayout(new BorderLayout());
		setResizable(false);
		setAlwaysOnTop(true);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		add(createContent(), BorderLayout.CENTER);
		init(vertexesCount, transtions);
		pack();
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		double x = (d.getWidth() - getWidth())/2;
		double y = (d.getHeight() - getHeight())/2;
		setLocation((int) x, (int) y);
	}

	private void init(int vertexesCount, int[][] transtions) {
		table.setModel(new LinkerModel(transtions));
		String[] items = new String[vertexesCount];
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
		JPanel bottom = new JPanel(new GridLayout(2, 2, 50, 20));
		bottom.add(add);
		bottom.add(remove);
		bottom.add(ok);
		bottom.add(cancel);
		bottom.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
		JPanel content = new JPanel(new BorderLayout());
		content.add(pane, BorderLayout.CENTER);
		content.add(bottom, BorderLayout.SOUTH);
		return content;
	}
}