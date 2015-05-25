package smth;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Myroslav Rudnytskyi
 * @version 0.1 16 трав. 2015
 */
public class HardwareSystem {

	public enum State {
		TSK_FPGA_UNUSE, TSK_FPGA_USE, TSK_MEM, TSK_LIB
	}

	private static List<Integer> FPGA = new ArrayList<Integer>();

	private static List<Integer> memory = new ArrayList<Integer>();

	public static State findConfiguration(Task task) {
		if (HardwareSystem.FPGA.contains(task.getHwN())) {
			if (false) {//TODO
				return State.TSK_FPGA_USE;
			} else {
				return State.TSK_FPGA_UNUSE;
			}
		} else {
			if (HardwareSystem.memory.contains(task.getHwN())) {
				return State.TSK_MEM;
			}
		}
		return State.TSK_LIB;
	}

	private static int getIndexOfSmallestBonus() {
		//TODO
		return 0;
	}

	public static void load(int hwTsk) {
		if (HardwareSystem.FPGA.size() == 9) {
			HardwareSystem.memory.add(HardwareSystem.FPGA.remove(HardwareSystem
					.getIndexOfSmallestBonus()));
		}
		HardwareSystem.FPGA.add(hwTsk);
		HardwareSystem.recalculateBonus();
	}

	public static void recalculateBonus() {
		//TODO
	}
}