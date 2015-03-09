package math;

import java.util.Stack;
import java.util.StringTokenizer;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * 
 * @author Mir4ik
 * @version 0.1 09.03.2015
 */
public class ProgramParser {
	
	public abstract static class Node extends DefaultMutableTreeNode {

		private static final long serialVersionUID = -4012225464508861076L;
		
		protected static int counter = 0;
		
		public static int getTasksCount() {
			return counter;
		}
	}
	
	public class Task extends Node {

		private static final long serialVersionUID = -5896458420485160191L;

		public final int number;
		
		public final int opType;
		
		public Task(int opType) {
			this.opType = opType;
			this.number = counter++;
		}
		
		@Override
		public String toString() {
			return number + " TASK" + opType;
		}
	}
	
	public class Block extends Node {

		private static final long serialVersionUID = -7472582801407395676L;
		
		@Override
		public String toString() {
			if (isRoot()) {
				return "ROOT";
			}
			return "IF";
		}
	}

	public Node parse(String program) {
		StringTokenizer st = new StringTokenizer(program);
		String[] tokens = new String[st.countTokens()];
		int i = 0;
		while (st.hasMoreTokens()) {
			tokens[i] = st.nextToken();
			i++;
		}
		Block currBlock = new Block();
		Block root = currBlock;
		Stack<Block> stack = new Stack<Block>();
		for (String s : tokens) {
			if (s.startsWith("TASK")) {
				int task = Integer.parseInt(s.substring(4, s.length()));
				Task t = new Task(task);
				currBlock.add(t);
				continue;
			}
			if (s.equals("{")) {
				Block b = new Block();
				stack.push(currBlock);
				currBlock = b;
				continue;
			}
			if (s.equals("}")) {
				Block parent = stack.pop();
				parent.add(currBlock);
				currBlock = parent;
				continue;
			}
		}
		return root;
	}
}