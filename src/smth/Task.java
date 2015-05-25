package smth;

/**
 * 
 * @author Mir4ik
 * @version 0.1 9 трав. 2015
 */
public class Task implements Comparable<Task> {

	private static int counter = 0;

	private final int id = Task.counter++;
	private final int hwN;

	private final int workTime;
	private final int bytestreamWords;
	private final int dataCount;

	public Task(int hwN) {
		this.hwN = hwN;
		//TODO temp
		switch (hwN) {
		case 0:
			workTime = 30;
			bytestreamWords = 7;
			dataCount = 2;
			break;
		case 1:
			workTime = 27;
			bytestreamWords = 9;
			dataCount = 2;
			break;
		case 2:
			workTime = 5;
			bytestreamWords = 8;
			dataCount = 2;
			break;
		case 3:
			workTime = 7;
			bytestreamWords = 11;
			dataCount = 2;
			break;
		default:
			workTime = 3;
			bytestreamWords = 10;
			dataCount = 2;
			break;
		}
	}

	public int getId() {
		return id;
	}

	public int getHwN() {
		return hwN;
	}

	public int getWorkingTime() {
		return workTime;
	}

	public int getBytestreamWords() {
		return bytestreamWords;
	}

	public int getDataCount() {
		return dataCount;
	}

	@Override
	public int compareTo(Task o) {
		if (o.workTime > workTime) {
			return 1;
		}
		if (o.workTime < workTime) {
			return -1;
		}
		return 0;
	}
}