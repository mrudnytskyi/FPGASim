package gui;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import com.thoughtworks.xstream.XStream;

/**
 * Main frame for application.
 * 
 * @author Mir4ik
 * @version 0.1 16.03.2015
 */
public class MainFrame extends Frame {
	
	private class Generate extends Action {

		private static final long serialVersionUID = -7062237348840489138L;
		
		public Generate() {
			super("Generate", "res\\file.png", "res\\file_big.png");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			GeneratorFrame gf = new GeneratorFrame();
			gf.setVisible(true);
		}	
	}
	
	private class Open extends Action {

		private static final long serialVersionUID = 8335479215032758045L;

		public Open() {
			super("Open", "res\\open.png", "res\\open_big.png");
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser chooser = new JFileChooser(".");
			chooser.setFileFilter(new XMLFileFilter());
			chooser.setSelectedFile(new File("temp.xml"));
			int result = chooser.showOpenDialog(MainFrame.this);
			if (result == JFileChooser.APPROVE_OPTION) {
				Object[] memento = (Object[]) new XStream().fromXML(
						chooser.getSelectedFile());
				int[][] trans = (int[][]) memento[0];
				for (int i = 0; i < trans.length; i++) {
					graph.addVertex();
				}
				for (int i = 0; i < trans.length; i++) {
					for (int j = i; j < trans[i].length; j++) {
						if (trans[i][j] == 1) {
							graph.addEdge(i, j);
						}
					}
				}
				graph.update(null, memento[1]);
				graph.update();
			}
		}
	}
	
	private class Save extends Action {

		private static final long serialVersionUID = 4266151357634163782L;

		public Save() {
			super("Save", "res\\save.png", "res\\save_big.png");
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser chooser = new JFileChooser(".");
			chooser.setFileFilter(new XMLFileFilter());
			chooser.setSelectedFile(new File("temp.xml"));
			int result = chooser.showSaveDialog(MainFrame.this);
			if (result == JFileChooser.APPROVE_OPTION) {
				int[][] trans = graph.createTransitions();
				int[] prop = graph.getPropertiesData();
				Object[] memento = new Object[2];
				memento[0] = trans;
				memento[1] = prop;
				try (FileWriter fw = new FileWriter(
						chooser.getSelectedFile())) {
					new XStream().toXML(memento, fw);
				} catch (Exception ex) {
					showError("Exception " + ex.getMessage());
				}
			}
		}
	}
	
	private class AddVertex extends Action {

		private static final long serialVersionUID = 2641072448265871581L;

		public AddVertex() {
			super("Add vertex", "res\\vertex.png", "res\\vertex_big.png");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			graph.addVertex();
		}
	}
	
	private class AddEdge extends Action {

		private static final long serialVersionUID = 7707301510372295044L;

		public AddEdge() {
			super("Add edges", "res\\edge.png", "res\\edge_big.png");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			LinkerFrame lf = new LinkerFrame(graph.createTransitions(),
					MainFrame.this.graph);
			lf.setVisible(true);
		}
	}
	
	private class Properties extends Action {

		private static final long serialVersionUID = -8224726385156451096L;
		
		public Properties() {
			super("Properties", "res\\props.png", "res\\props_big.png");
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
			PropertiesFrame pf = new PropertiesFrame(count, 
					graph.getPropertiesData(), graph);
			pf.setVisible(true);
		}
	}
	
	private class Edit extends Action {

		private static final long serialVersionUID = 6367966811361386786L;
		
		public Edit() {
			super("Edit", "res\\lib.png", "res\\lib_big.png");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			LibraryFrame lf = new LibraryFrame();
			lf.setVisible(true);
		}
	}
	
	private class About extends Action {

		private static final long serialVersionUID = -1814964833144105128L;
		
		public About() {
			super("About", "res\\info.png", "res\\info_big.png");
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			showInfo("Written by Myroslav Rudnytskyi, Kyiv Politechnic "
					+ "Institute, group IO-41m, 2015.");
		}
	}
	
	private class Exit extends Action {

		private static final long serialVersionUID = -1523012579322514186L;

		public Exit() {
			super("Exit", "res\\exit.png", "res\\exit_big.png");
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if (showQuestion("Do you want to exit application?")) {
				System.exit(0);
			}
		}
	}
	
	private class XMLFileFilter extends FileFilter {

		@Override
		public boolean accept(File file) {
			boolean isFile = file.isFile();
			boolean isDir = file.isDirectory();
			String fileName = file.getName();
			boolean filterNameLower = fileName.endsWith("XML");
			boolean filterNameUpper = fileName.endsWith("xml");
			if (isDir || (isFile) && (filterNameLower || filterNameUpper)) {
				return true;
			}
			return false;
		}

		@Override
		public String getDescription() {
			return "eXtensible Markup Language files (XML)";
		}
	}
	
	private static final long serialVersionUID = 8350407021970335634L;
	
	private final GraphPanel graph = new GraphPanel();
	
	private final JPanel charts = new JPanel();
	
	public MainFrame() {
		super("FPGA Sym");
		setLayout(new BorderLayout());
		setSize(Toolkit.getDefaultToolkit().getScreenSize());
		setExtendedState(MAXIMIZED_BOTH);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setJMenuBar(createMenu());
		add(createToolBar(), BorderLayout.NORTH);
		add(createContent(), BorderLayout.CENTER);
	}
	
	private JPanel createContent() {
		JTabbedPane tabbed = new JTabbedPane();
		tabbed.addTab("Graph", new ImageIcon("res\\algo.png"),
				new JScrollPane(graph));
		tabbed.addTab("Chart", new ImageIcon("res\\chart.png"),
				new JScrollPane(charts));
		JPanel content = new JPanel(new BorderLayout());
		content.add(tabbed, BorderLayout.CENTER);
		return content;
	}
	
	private JMenuBar createMenu() {
		JMenuBar menu = new JMenuBar();
		JMenu algorithm = new JMenu("Algorithm");
		algorithm.add(new Generate());
		algorithm.add(new Open());
		algorithm.addSeparator();
		algorithm.add(new Save());
		menu.add(algorithm);
		JMenu graph = new JMenu("Graph");
		graph.add(new AddVertex());
		graph.add(new AddEdge());
		graph.addSeparator();
		graph.add(new Properties());
		menu.add(graph);
		JMenu library = new JMenu("Library");
		library.add(new Edit());
		menu.add(library);
		//TODO add charts building
		menu.add(Box.createHorizontalGlue());
		JMenu help = new JMenu("?");
		help.add(new About());
		help.add(new Exit());
		menu.add(help);
		return menu;
	}
	
	private JToolBar createToolBar() {
		JToolBar toolBar = new JToolBar("Tools");
		toolBar.add(new Open());
		toolBar.add(new Save());
		toolBar.addSeparator();
		toolBar.add(new AddVertex());
		toolBar.add(new AddEdge());
		toolBar.add(new Properties());
		toolBar.addSeparator();
		//TODO add charts building
		return toolBar;
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				new MainFrame().setVisible(true);
			}
		});
	}
}