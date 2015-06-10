package sim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class represents state of system.
 * 
 * @author Mir4ik
 * @version 0.1 16.05.2015
 */
public class HardwareSystem {

	public enum State {
		TSK_FPGA, TSK_MEM, TSK_LIB;
	}

	public static final int FPGA_MAX_SIZE = 9;

	private static final int MAX_BONUS = 10;

	private static List<Integer> FPGA = new ArrayList<Integer>();

	private static Set<Integer> memory = new HashSet<Integer>();

	private static List<Integer> bonuses = new ArrayList<Integer>();

	public static State findConfiguration(Task task) {
		if (HardwareSystem.FPGA.contains(task.getHwN())) {
			return State.TSK_FPGA;
		} else {
			if (HardwareSystem.memory.contains(task.getHwN())) {
				return State.TSK_MEM;
			} else {
				return State.TSK_LIB;
			}
		}
	}

	public static void load(int hwTsk) {
		if (HardwareSystem.FPGA.size() == HardwareSystem.FPGA_MAX_SIZE) {
			int smallestBonus = Collections.min(HardwareSystem.bonuses);
			int smallestBonusIndex =
					Collections.binarySearch(HardwareSystem.bonuses,
							smallestBonus);
			int smallestBonusHwN = HardwareSystem.FPGA.get(smallestBonusIndex);
			HardwareSystem.FPGA.remove(new Integer(smallestBonusHwN));
			HardwareSystem.bonuses.remove(new Integer(smallestBonus));
			//TODO there were 2 same tasks. Ignore adding 2 same to memory?
			HardwareSystem.memory.add(smallestBonusHwN);
		}
		HardwareSystem.FPGA.add(hwTsk);
		for (int i = 0; i < HardwareSystem.bonuses.size(); i++) {
			HardwareSystem.bonuses.set(i, HardwareSystem.bonuses.get(i) - 1);
		}
		HardwareSystem.bonuses.add(HardwareSystem.MAX_BONUS);
	}
}