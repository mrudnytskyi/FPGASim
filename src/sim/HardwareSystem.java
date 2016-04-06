package sim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Class represents state of system.
 */
public class HardwareSystem {

	private final SettingsHolder settingsHolder;
	/**
	 * contains hwN
	 */
	private final ArrayList<Integer> FPGA = new ArrayList<>();
	/**
	 * contains hwN, set is used to ignore adding 2 same tasks
	 */
	private final HashSet<Integer> memory = new HashSet<>();
	/**
	 * contains bonus points. Index bonus = index hwN in FPGA
	 */
	private final ArrayList<Integer> bonuses = new ArrayList<>();

	public HardwareSystem(SettingsHolder settingsHolder) {
		this.settingsHolder = settingsHolder;
	}

	public State findConfiguration(Task task) {
		if (FPGA.contains(task.getHwN())) {
			return State.TSK_FPGA;
		} else {
			if (memory.contains(task.getHwN())) {
				return State.TSK_MEM;
			} else {
				return State.TSK_LIB;
			}
		}
	}

	public void load(Task task) {
		int fpgaMaxSize = settingsHolder.getFpgaSize();
		int maxBonus = settingsHolder.getBonus();
		int memorySize = settingsHolder.getMemorySize();

		if (FPGA.size() == fpgaMaxSize) {
			int smallestBonus = Collections.min(bonuses);
			int smallestBonusIndex = Collections.binarySearch(bonuses, smallestBonus);
			int smallestBonusHwN = FPGA.get(smallestBonusIndex);
			FPGA.remove(new Integer(smallestBonusHwN));
			bonuses.remove(smallestBonusIndex);
			if (memory.size() == memorySize) {
				Iterator<Integer> iterator = memory.iterator();
				iterator.next();
				iterator.remove();
			}
			memory.add(smallestBonusHwN);
		}
		FPGA.add(task.getHwN());
		for (int i = 0; i < bonuses.size(); i++) {
			bonuses.set(i, bonuses.get(i) - 1);
		}
		bonuses.add(maxBonus);
	}

	public enum State {
		TSK_FPGA, TSK_MEM, TSK_LIB
	}
}