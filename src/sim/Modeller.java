package sim;

import gui.TimeTracks;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.thoughtworks.xstream.XStream;

/**
 * Class for modelling.
 * 
 * @author Mir4ik
 * @version 0.1 09.05.2015
 */
public class Modeller {

	private static List<Task>[] all;

	private static final int MEMORY_ACCESS_TIME = 1;
	private static final int LOAD_LAST_TIME = 10;
	private static final int LOAD_DATUM_TIME = 2;
	private static final int NETWORK_MAX_RANDOM_TIME = 5;

	public static void main(String[] args) {
		Modeller.modell();
	}

	public static void modell() {
		// get data
		List<Task>[] levelsTasks = Modeller.makeTasks();
		List<Task> allTasks = Modeller.makeAll();
		System.out.println(Modeller.makeTasks1());

		int tasksCount = allTasks.size();
		int level = 0;
		List<Task> currentLevel = levelsTasks[level];
		TimeTracks time = new TimeTracks(tasksCount);
		BitSet finished = new BitSet(tasksCount);
		finished.set(0, tasksCount);

		while (!finished.isEmpty()) {
			// loading
			List<Task> working = new ArrayList<Task>();
			for (int i = 0; i < currentLevel.size(); i++) {
				Task t = currentLevel.get(i);

				time.addWaitingToLongestLoading(t.getId());

				switch (HardwareSystem.findConfiguration(t)) {
				case TSK_FPGA:
					System.err.println("F");
					// TODO load based on time or on FPGA size?
					HardwareSystem.load(t.getHwN());
					break;
				case TSK_LIB:
					int libTime =
							t.getBytestreamWords()
									* Modeller.MEMORY_ACCESS_TIME;
					int randTime =
							new Random()
									.nextInt(Modeller.NETWORK_MAX_RANDOM_TIME);
					time.addSearchingAndLoading(t.getId(), libTime + randTime);
					time.addLoadingLastWord(t.getId(), Modeller.LOAD_LAST_TIME);
					HardwareSystem.load(t.getHwN());
					break;
				case TSK_MEM:
					System.err.println("M");
					int memTime =
							t.getBytestreamWords()
									* Modeller.MEMORY_ACCESS_TIME;
					time.addSearchingAndLoading(t.getId(), memTime);
					time.addLoadingLastWord(t.getId(), Modeller.LOAD_LAST_TIME);
					HardwareSystem.load(t.getHwN());
					break;
				}
				// TODO ask?
				time.addLoadingData(t.getId(), t.getDataCount()
						* Modeller.LOAD_DATUM_TIME);

				working.add(t);
			}
			for (Task t : allTasks) {
				// do not update finished
				if (!finished.get(t.getId())) {
					continue;
				}
				if (!currentLevel.contains(t)) {
					time.addWaitingToLongestLoading(t.getId());
				} else {
					time.addWaitingToLongestCounting(t.getId());
				}
			}
			// modeling working tasks
			for (Task t : working) {
				time.addCounting(t.getId(), t.getWorkingTime());
				finished.clear(t.getId());
			}
			// write step results
			Modeller.printDivider();
			System.out.println(time);
			// prepare to next iteration
			if (level < (levelsTasks.length - 3)) {
				currentLevel = levelsTasks[++level];
				Collections.sort(currentLevel);
			}
		}
		System.out.println("Simulation ended");
	}

	public static void printDivider() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 111; i++) {
			sb.append("=");
		}
		System.out.println(sb.toString());
	}

	//TODO remake data getting and integrate into GUI
	private static List<Task> makeAll() {
		List<Task> a = new ArrayList<Task>();
		a.addAll(Modeller.all[0]);
		a.addAll(Modeller.all[1]);
		a.addAll(Modeller.all[2]);
		return a;
	}

	@SuppressWarnings("unchecked")
	private static List<Task>[] makeTasks() {
		Modeller.all = new LinkedList[5];
		List<Task> level1 = new LinkedList<Task>();
		level1.add(new Task(0));
		Modeller.all[0] = level1;
		List<Task> level2 = new LinkedList<Task>();
		level2.add(new Task(1));
		level2.add(new Task(2));
		Modeller.all[1] = level2;
		List<Task> level3 = new LinkedList<Task>();
		level3.add(new Task(0));
		level3.add(new Task(1));
		Modeller.all[2] = level3;
		return Modeller.all;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static List<Task>[] makeTasks1() {
		Object[] memento =
				(Object[]) new XStream().fromXML(new File("file.xml"));
		int[][] trans = (int[][]) memento[0];
		int[] nam = (int[]) memento[1];

		List[] allFucking = new LinkedList[100];
		int coun = 0;
		for (int i = 0; i < allFucking.length; i++) {
			List<Task> level = new LinkedList<Task>();
			//for (int element : nam) {
			//System.out.println(new Task(element));
			for (int k = 0; k < trans.length; k++) {
				for (int j = k; j < trans[k].length; j++) {
					if (trans[k][j] == 1) {
						level.add(new Task(nam[k]));
					}
				}
			}

			//}
			allFucking[i] = level;
			coun++;
		}
		return Arrays.copyOfRange(allFucking, 0, coun);
	}
}