package gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.NumberFormatter;

import com.thoughtworks.xstream.XStream;

/**
 * 
 * @author Mir4ik
 * @version 0.1 1.04.2015
 */
public class GeneratorFrame extends Frame {
	
	private enum TokensType {
		IF, TASK, CLOSE, NO;
	}

	private class Ok extends Action {

		private static final long serialVersionUID = 4500170332575759090L;

		public Ok() {
			super("OK", "res\\ok.png", "res\\ok_big.png");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			//TODO rewrite?
			int count = 0;
			try {
				count = ((double[][]) new XStream().fromXML(
						new File("library.xml"))).length;
			} catch (Exception ex) {
				showError("Exception" + ex.getMessage());
			}
			JFileChooser chooser = new JFileChooser(".");
			chooser.setFileFilter(new TXTFileFilter());
			chooser.setSelectedFile(new File("program.txt"));
			int result = chooser.showSaveDialog(GeneratorFrame.this);
			if (result == JFileChooser.APPROVE_OPTION) {
				try (FileWriter fw = new FileWriter(chooser.getSelectedFile())) {
					fw.write(generate(count, Integer.parseInt(nodeCount.getText()),
							Integer.parseInt(ifCount.getText()),
							Integer.parseInt(maxDeep.getText())));
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
			setVisible(false);
		}
	}
	
	private class Cancel extends Action {

		private static final long serialVersionUID = 8047480767340815989L;

		public Cancel() {
			super("Cancel", "res\\cancel.png", "res\\cancel_big.png");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			setVisible(false);
		}
	}
	
	public class TXTFileFilter extends FileFilter {

		@Override
		public boolean accept(File file) {
			boolean isFile = file.isFile();
			boolean isDir = file.isDirectory();
			String fileName = file.getName();
			boolean filterNameLower = fileName.endsWith("TXT");
			boolean filterNameUpper = fileName.endsWith("txt");
			if (isDir || (isFile) && (filterNameLower || filterNameUpper)) {
				return true;
			}
			return false;
		}

		@Override
		public String getDescription() {
			return "Text files (TXT)";
		}
	}

	private static final long serialVersionUID = -3695903238098950565L;
	
	private final JButton ok = new JButton(new Ok());
	
	private final JButton cancel = new JButton(new Cancel());
	
	private final JFormattedTextField nodeCount = 
			new JFormattedTextField(new NumberFormatter());
	
	private final JFormattedTextField ifCount = 
			new JFormattedTextField(new NumberFormatter());
	
	private final JFormattedTextField maxDeep = 
			new JFormattedTextField(new NumberFormatter());

	public GeneratorFrame() {
		super("Generate");
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
		nodeCount.setText("30");
		ifCount.setText("10");
		maxDeep.setText("3");
	}

	private Component createContent() {
		JPanel content = new JPanel(new GridLayout(4, 2, 50, 20));
		content.add(new JLabel("Enter nodes count, please"));
		content.add(nodeCount);
		content.add(new JLabel("Enter conditions count, please"));
		content.add(ifCount);
		content.add(new JLabel("Enter maximum deepness, please"));
		content.add(maxDeep);
		content.add(ok);
		content.add(cancel);
		content.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
		return content;
	}
	
	private String generate(int tskMax, int nodeMax, int ifMax, int depthMax) {
		StringBuilder sb = new StringBuilder();
		int counter = 0;
		TokensType previous = TokensType.NO;
		int depth = 0;
		int ifs = 0;
		while (counter < nodeMax) {
			switch (previous) {
			case NO:	case IF:	case CLOSE:
				// can be task or if
				if (ifs < ifMax && depth < depthMax) {
					switch (new Random().nextInt(2)) {
					case 1:
						previous = TokensType.IF;
						for (int j = 0; j < depth; j++) sb.append("\t");
						sb.append("IF {\r\n");
						depth++;
						ifs++;
						break;
					case 0:
						previous = TokensType.TASK;
						for (int j = 0; j < depth; j++) sb.append("\t");
						sb.append("HWTASK");
						sb.append(new Random().nextInt(tskMax));
						sb.append("\r\n");
						counter++;
						break;
					}
				} else {
					previous = TokensType.TASK;
					for (int j = 0; j < depth; j++) sb.append("\t");
					sb.append("HWTASK");
					sb.append(new Random().nextInt(tskMax));
					sb.append("\r\n");
					counter++;
				}
				break;
			case TASK:
				// can be task or if or close
				if (depth != 0) {
					if (ifs < ifMax && depth < depthMax) {
						switch (new Random().nextInt(3)) {
						case 2:
							previous = TokensType.IF;
							for (int j = 0; j < depth; j++) sb.append("\t");
							sb.append("IF {\r\n");
							depth++;
							ifs++;
							break;
						case 1:
							previous = TokensType.TASK;
							for (int j = 0; j < depth; j++) sb.append("\t");
							sb.append("HWTASK");
							sb.append(new Random().nextInt(tskMax));
							sb.append("\r\n");
							counter++;
							break;
						case 0:
							previous = TokensType.CLOSE;
							for (int j = 0; j < depth - 1; j++) sb.append("\t");
							sb.append("}\r\n");
							depth--;
							break;
						}
					} else {
						switch (new Random().nextInt(2)) {
						case 1:
							previous = TokensType.TASK;
							for (int j = 0; j < depth; j++) sb.append("\t");
							sb.append("HWTASK");
							sb.append(new Random().nextInt(tskMax));
							sb.append("\r\n");
							counter++;
							break;
						case 0:
							previous = TokensType.CLOSE;
							for (int j = 0; j < depth - 1; j++) sb.append("\t");
							sb.append("}\r\n");
							depth--;
							break;
						}
					}
				} else {
					// can be task or if
					if (ifs < ifMax && depth < depthMax) {
						switch (new Random().nextInt(2)) {
						case 1:
							previous = TokensType.IF;
							for (int j = 0; j < depth; j++) sb.append("\t");
							sb.append("IF {\r\n");
							depth++;
							ifs++;
							break;
						case 0:
							previous = TokensType.TASK;
							for (int j = 0; j < depth; j++) sb.append("\t");
							sb.append("HWTASK");
							sb.append(new Random().nextInt(tskMax));
							sb.append("\r\n");
							counter++;
							break;
						}
					} else {
						previous = TokensType.TASK;
						for (int j = 0; j < depth; j++) sb.append("\t");
						sb.append("HWTASK");
						sb.append(new Random().nextInt(tskMax));
						sb.append("\r\n");
						counter++;
					}
				}
				break;
			}

		}
		while (depth != 0) {
			for (int j = 0; j < depth - 1; j++) sb.append("\t");
			sb.append("}\r\n");
			depth--;
		}
		return sb.toString();
	}
}