package math;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

/**
 * 
 * @author Mir4ik
 * @version 0.1 08.03.2015
 */
@Parameters
public class ProgramGenerator {
	
	private enum TokensType {
		IF, TASK, CLOSE, NO;
	}
	
	@Parameter(names = "-f")
	private static String file = "program.txt";
	
	@Parameter(names = "-n")
	private static int nodeCount = 30;
	
	@Parameter(names = "-c")
	private static int ifCount = 10;
	
	@Parameter(names = "-d")
	private static int maxDeep = 3;

	public static String generate(int nodeMax, int ifMax, int depthMax) {
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
						sb.append("TASK" + new Random().nextInt(64) + "\r\n");
						counter++;
						break;
					}
				} else {
					previous = TokensType.TASK;
					for (int j = 0; j < depth; j++) sb.append("\t");
					sb.append("TASK" + new Random().nextInt(64) + "\r\n");
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
							sb.append("TASK" + new Random().nextInt(64) + "\r\n");
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
							sb.append("TASK" + new Random().nextInt(64) + "\r\n");
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
							sb.append("TASK" + new Random().nextInt(64) + "\r\n");
							counter++;
							break;
						}
					} else {
						previous = TokensType.TASK;
						for (int j = 0; j < depth; j++) sb.append("\t");
						sb.append("TASK" + new Random().nextInt(64) + "\r\n");
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
	
	public static void main(String[] args) {
		JCommander jc = new JCommander(new ProgramGenerator());
		jc.parse(args);
		try (FileWriter fw = new FileWriter(file)) {
			fw.write(generate(nodeCount, ifCount, maxDeep));
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Successfully writed to " + file);
	}
}