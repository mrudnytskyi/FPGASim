import com.thoughtworks.xstream.XStream;
import gui.GraphPanel;
import gui.TimeTracks;
import sim.HardwareSystem;
import sim.NewSimulator;
import sim.SettingsHolder;
import sim.Task;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class for automatic testing.
 */
@SuppressWarnings("unused")
public class Testbench {

	public static final int REPEAT_COUNT = 100;
	public static final int EXPERIMENTS_COUNT = 10;

	public static final int PROCESS_MIN = 0;
	public static final int PROCESS_AVG = 1;
	public static final int PROCESS_MAX = 2;
	public static final int WORK_AVG = 3;
	public static final int PROCESS_AVG_NO_PARALLEL = 4;

	static GraphPanel graph;

	public static void main(String[] args) {
		Double[] processTime = new Double[EXPERIMENTS_COUNT];
		Double[] workTime = new Double[EXPERIMENTS_COUNT];
		Double[] processTimeNoParallel = new Double[EXPERIMENTS_COUNT];
		String[] files = {"algo1.xml", "algo2.xml", "algo3.xml", "algo4.xml", "algo5.xml", "algo6.xml",
				"algo7.xml", "algo8.xml", "algo9.xml", "algo10.xml"};

		for (int i = 0; i < files.length; i++) {
			double[] res = experiment(files[i]);
			processTime[i] = res[PROCESS_AVG];
			workTime[i] = res[WORK_AVG];
			processTimeNoParallel[i] = res[PROCESS_AVG_NO_PARALLEL] + workTime[i];
		}

		System.out.println("Process time");
		for (Double aProcessTime : processTime) {
			System.out.println(aProcessTime.intValue());
		}
		System.out.println("Work time");
		for (Double aWorkTime : workTime) {
			System.out.println(aWorkTime.intValue());
		}
		System.out.println("Process without method time");
		for (Double aProcessTimeNoParallel : processTimeNoParallel) {
			System.out.println(aProcessTimeNoParallel.intValue());
		}
	}

	private static double[] experiment(String file) {
		TimeTracks[] repeating = new TimeTracks[REPEAT_COUNT];
		for (int i = 0; i < repeating.length; i++) {
			createEnvironment();
			open(file);
			repeating[i] = simulate(makeTaskLevels());
		}

		int sum = 0;
		int min = repeating[0].getMaxLength();
		int max = 0;
		int work = 0;
		int load = 0;
		for (TimeTracks r : repeating) {
			int maxLength = r.getMaxLength();
			sum += maxLength;
			min = Math.min(min, maxLength);
			max = Math.max(max, maxLength);
			work += r.getWorkingTime();
			load += r.getLoadingTime();
		}
		double allAvg = sum / repeating.length;
		double workAvg = work / repeating.length;
		double loadAvg = load / repeating.length;
		return new double[]{min, allAvg, max, workAvg, loadAvg};
	}

	private static void createEnvironment() {
		graph = new GraphPanel();
		Task.clearCounter();
	}

	private static void open(String file) {
		Object[] memento = (Object[]) new XStream().fromXML(new File(file));
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

	@SuppressWarnings("unchecked")
	private static List<Task>[] makeTaskLevels() {
		int[][] transitions = graph.createTransitions();
		int[] hwNumbers = graph.getPropertiesData();

		int levelsCounter = 0;
		int tasksCounter = 0;
		List<List<Task>> tasks = new ArrayList<>();

		List<Task> firstLevel = new ArrayList<>();
		firstLevel.add(new Task(hwNumbers[0]));
		tasksCounter++;
		levelsCounter++;
		tasks.add(firstLevel);

		while (tasksCounter != transitions.length) {
			List<Task> prevLevel = tasks.get(levelsCounter - 1);
			List<Task> level = new ArrayList<>();

			Set<Integer> visited = new HashSet<>();
			for (Task prevLevelTask : prevLevel) {
				int[] transitionsLine = transitions[prevLevelTask.getId()];

				for (int j = 0; j < transitionsLine.length; j++) {
					boolean notConnectedOnCurrentLevel = true;
					for (Task levelTask : level) {
						notConnectedOnCurrentLevel &= transitions[levelTask.getId()][j] != 1;
					}

					boolean notVisited = !visited.contains(j);
					boolean isConnected = transitionsLine[j] == 1;
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

	private static TimeTracks simulate(List<Task>[] levelsTasks) {
		SettingsHolder settingsHolder = new SettingsHolder(new File("settings.xml"));
		NewSimulator simulator = new NewSimulator(new HardwareSystem(settingsHolder), settingsHolder);
		return simulator.simulate(levelsTasks);
	}
}
