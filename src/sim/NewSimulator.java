package sim;

import gui.TimeTracks;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * New class for simulating system work.
 */
public class NewSimulator {
	private final HardwareSystem hardwareSystem;
	private final SettingsHolder settingsHolder;

	public NewSimulator(HardwareSystem hardwareSystem, SettingsHolder settingsHolder) {
		this.hardwareSystem = hardwareSystem;
		this.settingsHolder = settingsHolder;
	}

	public TimeTracks simulate(List<Task>[] levelsTasks) {
		Objects.requireNonNull(levelsTasks, "Null levelsTasks!");
		if (levelsTasks.length < 2 || levelsTasks[0].size() != 1) {
			throw new IllegalArgumentException("Wrong algo!");
		}

		int memoryAccessTime = settingsHolder.getMemoryAccessTime();
		int networkMaxRandomTime = settingsHolder.getNetworkMaxRandomTime();
		int loadLastWordTime = settingsHolder.getLoadLastWordTime();
		int loadDatumTime = settingsHolder.getLoadDatumTime();

		System.out.printf("Start working with time: memoryAccess = %s loadLastWord = %s loadDatum = %s%n",
				memoryAccessTime, loadLastWordTime, loadDatumTime);

		List<Task> allTasks = new ArrayList<>();
		for (List<Task> levelsTask : levelsTasks) {
			allTasks.addAll(levelsTask);
		}
		int tasksCount = allTasks.size();
		int level = 0;
		List<Task> currentLevel = levelsTasks[level];
		TimeTracks time = new TimeTracks(tasksCount);
		Set<Task> finished = new HashSet<>();

		// first level
		Task first = currentLevel.get(0);
		int id1 = first.getId();
		int libTime1 = first.getBytestreamWords() * memoryAccessTime;
		int randTime1 = new Random().nextInt(networkMaxRandomTime);
		time.addSearchingAndLoading(id1, libTime1 + randTime1);
		time.addLoadingLastWord(id1, loadLastWordTime);
		hardwareSystem.load(first);
		System.out.printf("Load %s from library. LibTime = %s, randTime = %s%n", first, libTime1, randTime1);
		time.addLoadingData(id1, first.getDataCount() * loadDatumTime);
		time.addWaitingToLongest();
		time.addCounting(id1, first.getWorkingTime());
		finished.add(first);
		currentLevel = levelsTasks[++level];
		Collections.sort(currentLevel);

		while (tasksCount != finished.size()) {
			// loading configuration
			for (Task t : currentLevel) {
				int id = t.getId();
				switch (hardwareSystem.findConfiguration(t)) {
					case TSK_FPGA:
						hardwareSystem.load(t);
						System.out.printf("Load %s from FPGA%n", t);
						break;
					case TSK_LIB:
						int libTime = t.getBytestreamWords() * memoryAccessTime;
						int randTime = new Random().nextInt(networkMaxRandomTime);
						time.addSearchingAndLoading(id, libTime + randTime);
						time.addLoadingLastWord(id, loadLastWordTime);
						hardwareSystem.load(t);
						System.out.printf("Load %s from library. LibTime = %s, randTime = %s%n", t, libTime, randTime);
						break;
					case TSK_MEM:
						int memTime = t.getBytestreamWords() * memoryAccessTime;
						time.addSearchingAndLoading(id, memTime);
						time.addLoadingLastWord(id, loadLastWordTime);
						hardwareSystem.load(t);
						System.out.printf("Load %s from memory. MemTime = %s%n", t, memTime);
						break;
				}
				// loading data
				time.addLoadingData(id, t.getDataCount() * loadDatumTime);
				// TODO optimize
				time.addWaitingToLongest();
				// simulate working tasks
				time.addCounting(id, t.getWorkingTime());
				finished.add(t);
			}
			// prepare to next iteration
			if (level < (levelsTasks.length - 1)) {
				currentLevel = levelsTasks[++level];
				Collections.sort(currentLevel);
			} else {
				writeGantt(time);
			}
			// prediction
			for (Task t : currentLevel) {
				int predictedLoadingTime = 0;
				switch (hardwareSystem.findConfiguration(t)) {
					case TSK_MEM:
						predictedLoadingTime = (t.getBytestreamWords() * memoryAccessTime) + loadLastWordTime;
						break;
					case TSK_LIB:
						predictedLoadingTime = (t.getBytestreamWords() * memoryAccessTime) + loadLastWordTime + networkMaxRandomTime;
						break;
				}
				int timeToCalculationsEnd = time.getMaxLength() - time.getLength(t.getId());
				int delta = timeToCalculationsEnd - predictedLoadingTime;
				if (delta > 0) {
					for (int i = 0; i < time.getTracksCount(); i++) {
						boolean notMaxLengthTrack = time.getLength(i) != time.getMaxLength();
						if (notMaxLengthTrack) {
							time.addWaiting(i, delta);
						}
					}
				}
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