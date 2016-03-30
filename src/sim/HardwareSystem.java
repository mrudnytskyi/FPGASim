package sim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

/**
 * Class represents state of system.
 * 
 * @author Mir4ik
 * @version 0.1 16.05.2015
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

	//TODO memory make max size, do not ignore adding
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

		if (FPGA.size() == fpgaMaxSize) {
			int smallestBonus = Collections.min(bonuses);
			int smallestBonusIndex = Collections.binarySearch(bonuses, smallestBonus);
			int smallestBonusHwN = FPGA.get(smallestBonusIndex);
			FPGA.remove(new Integer(smallestBonusHwN));
			bonuses.remove(smallestBonusIndex);
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