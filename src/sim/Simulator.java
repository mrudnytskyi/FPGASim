package sim;

import gui.TimeTracks;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Class for simulating system work.
 */
public class Simulator {
	private final HardwareSystem hardwareSystem;
	private final SettingsHolder settingsHolder;

	public Simulator(HardwareSystem hardwareSystem, SettingsHolder settingsHolder) {
		this.hardwareSystem = hardwareSystem;
		this.settingsHolder = settingsHolder;
	}

	public TimeTracks simulate(List<Task>[] levelsTasks, List<Task> allTasks) {
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

		System.out.printf("Start working with time: memoryAccess = %s loadLastWord = %s loadDatum = %s%n",
				memoryAccessTime, loadLastWordTime, loadDatumTime);
		while (!finished.isEmpty()) {
			// loading
			List<Task> working = new ArrayList<>();
			for (Task t : currentLevel) {
				time.addWaitingToLongestLoading(t.getId());

				switch (hardwareSystem.findConfiguration(t)) {
					case TSK_FPGA:
						// TODO make when 2 same tasks at same time
						System.out.printf("Load %s from FPGA%n", t);
						hardwareSystem.load(t);
						break;
					case TSK_LIB:
						int libTime = t.getBytestreamWords() * memoryAccessTime;
						int randTime = new Random().nextInt(networkMaxRandomTime);
						time.addSearchingAndLoading(t.getId(), libTime + randTime);
						time.addLoadingLastWord(t.getId(), loadLastWordTime);
						System.out.printf("Load %s from library. LibTime = %s, randTime = %s%n", t, libTime, randTime);
						hardwareSystem.load(t);
						break;
					case TSK_MEM:
						int memTime = t.getBytestreamWords() * memoryAccessTime;
						time.addSearchingAndLoading(t.getId(), memTime);
						time.addLoadingLastWord(t.getId(), loadLastWordTime);
						System.out.printf("Load %s from memory. MemTime = %s%n", t, memTime);
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
			// simulate working tasks
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
			Files.write(Paths.get("simulated.txt"), time.toString().getBytes());
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}