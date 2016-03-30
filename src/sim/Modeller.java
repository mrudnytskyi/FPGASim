package sim;

import gui.TimeTracks;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Class for modelling.
 * 
 * @author Mir4ik
 * @version 0.1 09.05.2015
 */
public class Modeller {
	private final HardwareSystem hardwareSystem;
	private final SettingsHolder settingsHolder;

	public Modeller(HardwareSystem hardwareSystem, SettingsHolder settingsHolder) {
		this.hardwareSystem = hardwareSystem;
		this.settingsHolder = settingsHolder;
	}

	public TimeTracks modell(List<Task>[] levelsTasks, List<Task> allTasks) {
		int memoryAccessTime = settingsHolder.getMemoryAccessTime();
		int networkMaxRandomTime = settingsHolder.getNetworkMaxRandomTime();
		int loadLastWordTime = settingsHolder.getLoadLastWordTime();
		int loadDatumTime = settingsHolder.getLoadDatumTime();

		int tasksCount = allTasks.size();
		int level = 0;
		List<Task> currentLevel = levelsTasks[level];
		TimeTracks time = new TimeTracks(tasksCount);
		BitSet finished = new BitSet(tasksCount);
		finished.set(0, tasksCount);

		while (!finished.isEmpty()) {
			// loading
			List<Task> working = new ArrayList<>();
			for (int i = 0; i < currentLevel.size(); i++) {
				Task t = currentLevel.get(i);

				time.addWaitingToLongestLoading(t.getId());

				switch (hardwareSystem.findConfiguration(t)) {
					case TSK_FPGA:
						System.out.println("F " + t);
						// TODO make when 2 same tasks at same time
						hardwareSystem.load(t);
						break;
					case TSK_LIB:
						System.out.println("L " + t);
						int libTime = t.getBytestreamWords() * memoryAccessTime;
						int randTime = new Random().nextInt(networkMaxRandomTime);
						time.addSearchingAndLoading(t.getId(), libTime + randTime);
						time.addLoadingLastWord(t.getId(), loadLastWordTime);
						hardwareSystem.load(t);
						break;
					case TSK_MEM:
						System.out.println("M " + t);
						int memTime = t.getBytestreamWords() * memoryAccessTime;
						time.addSearchingAndLoading(t.getId(), memTime);
						time.addLoadingLastWord(t.getId(), loadLastWordTime);
						hardwareSystem.load(t);
						break;
				}
				//TODO loading before start working?
				time.addLoadingData(t.getId(), t.getDataCount() * loadDatumTime);

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
				writeGantt(time);
			}
		}
		return time;
	}

	private void writeGantt(TimeTracks time) {
		try {
			Files.write(Paths.get("model.txt"), time.toString().getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}