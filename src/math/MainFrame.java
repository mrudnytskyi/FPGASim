package math;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Enumeration;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import math.ProgramParser.Node;
import math.ProgramParser.Task;

/**
 * 
 * @author Mir4ik
 * @version 0.1 07.03.2015
 */
public class MainFrame extends JFrame {

	private static final long serialVersionUID = -1616367561107980353L;

	private JTree tree;

	private int[][] transitions;

	public MainFrame() {
		setSize(Toolkit.getDefaultToolkit().getScreenSize());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		init();
		add(new JScrollPane(tree), BorderLayout.CENTER);
		expandAll(new TreePath(tree.getModel().getRoot()));
		pack();
	}

	private void expandAll(TreePath parent) {
		TreeNode node = (TreeNode) parent.getLastPathComponent();
		if (node.getChildCount() >= 0) {
			for (@SuppressWarnings("unchecked")
			Enumeration<TreeNode> e = node.children(); e.hasMoreElements();) {
				TreeNode treeNode = (TreeNode) e.nextElement();
				TreePath path = parent.pathByAddingChild(treeNode);
				expandAll(path);
			}
		}
		tree.expandPath(parent);
	}

	private void init() {
		try {
			String program = FilesFacade.readTXT("program.txt");
			ProgramParser parser = new ProgramParser();
			Node n = parser.parse(program);
			tree = new JTree(n);
			transitions = new int[Node.getTasksCount()][Node.getTasksCount()];
			ArrayList<ArrayList<TreeNode>> paths = getPaths();
			for (ArrayList<TreeNode> path : paths) {
				for (int i = 0; i < path.size() - 1; i++) {
					int from = ((Task) path.get(i)).number;
					int to = ((Task) path.get(i + 1)).number;
					transitions[from][to] = 1;
					transitions[to][from] = 1;
				}
			}
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < transitions.length; i++) {
				for (int j = 0; j < transitions[i].length; j++) {
					sb.append(transitions[i][j] + " ");
				}
				sb.append("\r\n");
			}
			FilesFacade.writeTXT("matrix.txt", sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private ArrayList<ArrayList<TreeNode>> getPaths() {
		ArrayList<ArrayList<TreeNode>> paths = new ArrayList<ArrayList<TreeNode>>();
		getPath(paths, new ArrayList<TreeNode>(), (TreeNode) tree.getModel().getRoot());
		return paths;
	}

	private void getPath(ArrayList<ArrayList<TreeNode>> paths,
			ArrayList<TreeNode> path, TreeNode node) {
		for (@SuppressWarnings("unchecked")
		Enumeration<TreeNode> e = node.children(); e.hasMoreElements();) {
			TreeNode treeNode = (TreeNode) e.nextElement();
			if (((DefaultMutableTreeNode) treeNode).isLeaf()) {
				path.add((TreeNode) treeNode);
				paths.add(new ArrayList<TreeNode>(path));
			} else {
				getPath(paths, new ArrayList<TreeNode>(path),
						(TreeNode) treeNode);
			}
		}
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