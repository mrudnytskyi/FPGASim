package sim;

import gui.TimeTracks;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Class for modelling.
 * 
 * @author Mir4ik
 * @version 0.1 09.05.2015
 */
public class Modeller {

	private static final int MEMORY_ACCESS_TIME = 1;
	private static final int LOAD_LAST_TIME = 10;
	private static final int LOAD_DATUM_TIME = 2;
	private static final int NETWORK_MAX_RANDOM_TIME = 5;

	public static TimeTracks modell(List<Task>[] levelsTasks,
			List<Task> allTasks) {

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
					System.out.println("F " + t);
					// TODO make when 2 same tasks at same time
					HardwareSystem.load(t);
					break;
				case TSK_LIB:
					System.out.println("L " + t);
					int libTime =
							t.getBytestreamWords()
									* Modeller.MEMORY_ACCESS_TIME;
					int randTime =
							new Random()
									.nextInt(Modeller.NETWORK_MAX_RANDOM_TIME);
					time.addSearchingAndLoading(t.getId(), libTime + randTime);
					time.addLoadingLastWord(t.getId(), Modeller.LOAD_LAST_TIME);
					HardwareSystem.load(t);
					break;
				case TSK_MEM:
					System.out.println("M " + t);
					int memTime =
							t.getBytestreamWords()
									* Modeller.MEMORY_ACCESS_TIME;
					time.addSearchingAndLoading(t.getId(), memTime);
					time.addLoadingLastWord(t.getId(), Modeller.LOAD_LAST_TIME);
					HardwareSystem.load(t);
					break;
				}
				//TODO loading before start working?
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
			// prepare to next iteration
			if (level < (levelsTasks.length - 1)) {
				currentLevel = levelsTasks[++level];
				Collections.sort(currentLevel);
			} else {
				Modeller.writeGantt(time);
			}
		}
		return time;
	}

	private static void writeGantt(TimeTracks time) {
		try {
			Files.write(Paths.get("model.txt"), time.toString().getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}