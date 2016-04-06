package gui;

import com.thoughtworks.xstream.XStream;
import sim.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Main frame for application.
 */
public class MainFrame extends Frame {

	private static final long serialVersionUID = 8350407021970335634L;
	private final GraphPanel graph = new GraphPanel();
	private final Library library = new Library();
	private final JTabbedPane tabbed = new JTabbedPane();

	public MainFrame() {
		super("FPGA Sym");
		setLayout(new BorderLayout());
		setSize(Toolkit.getDefaultToolkit().getScreenSize());
		setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setJMenuBar(createMenu());
		add(createToolBar(), BorderLayout.NORTH);
		add(createContent(), BorderLayout.CENTER);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			new MainFrame().setVisible(true);
			Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
				showError(e);
				e.printStackTrace();
			});
		});
	}

	private JPanel createContent() {
		tabbed.addTab("Graph", new ImageIcon("res\\algo.png"), new JScrollPane(graph));
		tabbed.addTab("Gant", new ImageIcon("res\\gant.png"), null);
		JPanel content = new JPanel(new BorderLayout());
		content.add(tabbed, BorderLayout.CENTER);
		return content;
	}

	private JMenuBar createMenu() {
		JMenuBar menu = new JMenuBar();
		JMenu algorithm = new JMenu("Algorithm");
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
		JMenu calculations = new JMenu("Calculations");
		calculations.add(new Simulate());
		menu.add(calculations);
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
		toolBar.add(new Simulate());
		return toolBar;
	}

	@SuppressWarnings("unchecked")
	private List<Task>[] makeTaskLevels() {
		int[][] transitions = graph.createTransitions();
		int[] hwNumbers = graph.getPropertiesData();

		int tasksCounter = 0;
		int levelsCounter = 0;
		List<List<Task>> tasks = new ArrayList<>();

		List<Task> firstLevel = new ArrayList<>();
		firstLevel.add(new Task(hwNumbers[0]));
		tasksCounter++;
		tasks.add(firstLevel);
		levelsCounter++;

		while (tasksCounter != transitions.length) {
			List<Task> level = new ArrayList<>();
			List<Task> prevLevel = tasks.get(levelsCounter - 1);

			Set<Integer> visited = new HashSet<>();
			for (Task prevLevelTask : prevLevel) {
				int[] transitionsLine = transitions[prevLevelTask.getId()];

				for (int j = 0; j < transitionsLine.length; j++) {
					boolean notConnectedOnCurrentLevel = true;
					for (Task levelTask : level) {
						notConnectedOnCurrentLevel &= transitions[levelTask.getId()][j] != 1;
					}

					boolean isConnected = transitionsLine[j] == 1;
					boolean notVisited = !visited.contains(j);
					if (isConnected && notVisited && notConnectedOnCurrentLevel) {
						level.add(new Task(hwNumbers[j]));
						tasksCounter++;
						visited.add(j);
					}
				}
			}

			tasks.add(level);
			levelsCounter++;
		}

		List<Task>[] result = new ArrayList[tasks.size()];
		int i = 0;
		for (List<Task> lst : tasks) {
			result[i] = lst;
			i++;
		}
		return result;
	}

	private class Open extends Action {

		private static final long serialVersionUID = 8335479215032758045L;

		public Open() {
			super("Open", "res\\open.png", "res\\open_big.png");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			//TODO bug to clear all before opening
			JFileChooser chooser = new JFileChooser(".");
			chooser.setFileFilter(new FileNameExtensionFilter(
					"eXtensible Markup Language files (XML)", "xml", "XML"));
			chooser.setSelectedFile(new File("temp.xml"));
			int result = chooser.showOpenDialog(MainFrame.this);
			if (result == JFileChooser.APPROVE_OPTION) {
				Object[] memento = (Object[]) new XStream().fromXML(chooser.getSelectedFile());
				int[][] trans = (int[][]) memento[0];
				for (int[] tran : trans) {
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
			chooser.setFileFilter(new FileNameExtensionFilter("eXtensible Markup Language files (XML)", "xml", "XML"));
			chooser.setSelectedFile(new File("temp.xml"));
			int result = chooser.showSaveDialog(MainFrame.this);
			if (result == JFileChooser.APPROVE_OPTION) {
				int[][] trans = graph.createTransitions();
				int[] prop = graph.getPropertiesData();
				Object[] memento = new Object[2];
				memento[0] = trans;
				memento[1] = prop;
				try (FileWriter fw = new FileWriter(chooser.getSelectedFile())) {
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
			LinkerFrame lf = new LinkerFrame(graph.createTransitions(), graph);
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
			PropertiesFrame pf = new PropertiesFrame(library.getSize(), graph.getPropertiesData(), graph);
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

	private class Simulate extends Action {

		private static final long serialVersionUID = 7730347825572796898L;

		public Simulate() {
			super("Simulate", "res\\calc.png", "res\\calc_big.png");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			//TODO bug when empty, bug with scrollpane
			SettingsHolder settingsHolder = new SettingsHolder(new File("settings.xml"));
			NewSimulator simulator = new NewSimulator(new HardwareSystem(settingsHolder), settingsHolder);
			tabbed.setComponentAt(1, new JScrollPane(new GantDiagramPanel(simulator.simulate(makeTaskLevels()))));
		}
	}

	private class About extends Action {

		private static final long serialVersionUID = -1814964833144105128L;

		public About() {
			super("About", "res\\info.png", "res\\info_big.png");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			showInfo("Written by Myroslav Rudnytskyi, Kyiv Politechnic Institute, group IO-41m, 2015.");
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
}