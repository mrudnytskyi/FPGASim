package sim;

/**
 * Class represents simple hardware task with properties: number, work time,
 * byte-stream words and data words.
 * 
 * @author Mir4ik
 * @version 0.1 09.05.2015
 */
public class Task implements Comparable<Task> {

	private static int counter = 0;
	private static Library lib = new Library();

	private final int id = Task.counter++;
	private final int hwN;

	private final int workTime;
	private final int bytestreamWords;
	private final int dataCount;

	public Task(int hwN) {
		this.hwN = hwN;
		int[][] data = Task.lib.getData();
		workTime = data[hwN][1];
		bytestreamWords = data[hwN][0];
		dataCount = data[hwN][2];
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