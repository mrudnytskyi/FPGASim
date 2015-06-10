package smth;

/**
 * 
 * @author Mir4ik
 * @version 0.1 09.05.2015
 */
public class TimeTracks {

	private final StringBuilder[] data;

	public TimeTracks(int tracksCount) {
		data = new StringBuilder[tracksCount];
		for (int i = 0; i < data.length; i++) {
			data[i] = new StringBuilder();
		}
	}

	public void addWaiting(int track, int time) {
		addSymbol(track, time, ' ');
	}

	public void addSearchingAndLoading(int track, int time) {
		addSymbol(track, time, '#');
	}

	public void addLoadingLastWord(int track, int time) {
		addSymbol(track, time, '$');
	}

	public void addLoadingData(int track, int time) {
		addSymbol(track, time, '&');
	}

	public void addCounting(int track, int time) {
		addSymbol(track, time, '*');
	}

	private void addSymbol(int track, int time, char symbol) {
		requiredNotNegative(time);
		checkBounds(track);
		for (int i = 0; i < time; i++) {
			data[track].append(symbol);
		}
	}

	private void requiredNotNegative(int i) {
		if (i < 0) {
			throw new IllegalArgumentException();
		}
	}

	private void checkBounds(int i) {
		requiredNotNegative(i);
		if (i >= data.length) {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < data.length; i++) {
			sb.append(i);
			sb.append(data[i]);
			sb.append(System.lineSeparator());
		}
		return sb.toString();
	}

	public void addWaitingToLongestLoading(int track) {
		checkBounds(track);
		int maxSize = 0;
		for (StringBuilder element : data) {
			int length = element.length();
			if ((length > 1) && (element.charAt(length - 1) == '&')) {
				maxSize = Math.max(maxSize, length);
			}
		}
		int count = maxSize - data[track].length();
		for (int i = 0; i < count; i++) {
			data[track].append(' ');
		}
	}

	public void addWaitingToLongestCounting(int track) {
		checkBounds(track);
		int maxSize = 0;
		for (StringBuilder element : data) {
			int length = element.length();
			if ((length > 1) && (element.charAt(length - 1) == '*')) {
				maxSize = Math.max(maxSize, length);
			}
		}
		int count = maxSize - data[track].length();
		for (int i = 0; i < count; i++) {
			data[track].append(' ');
		}
	}
}