package gui;

/**
 * Class represents command-line (text-based) version of Gant's diagram.
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

	public int getMaxLength() {
		int maxSize = 0;
		for (StringBuilder track : data) {
			maxSize = Math.max(maxSize, track.length());
		}
		return maxSize;
	}

	public void addWaitingToLongest() {
		int maxSize = getMaxLength();
		if (maxSize == 0) return;
		for (StringBuilder track : data) {
			while (track.length() != maxSize) {
				track.append(' ');
			}
		}
	}

	public int getLength(int track) {
		checkBounds(track);
		return data[track].length();
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

	public int getTracksCount() {
		return data.length;
	}

	public String getTrack(int track) {
		checkBounds(track);
		return data[track].toString();
	}

	public int getWorkingTime() {
		int counter = 0;
		for (StringBuilder aData : data) {
			for (int i = 0; i < aData.length(); i++) {
				if (aData.charAt(i) == '*') {
					counter++;
				}
			}
		}
		return counter;
	}

	public int getLoadingTime() {
		int counter = 0;
		for (StringBuilder aData : data) {
			for (int i = 0; i < aData.length(); i++) {
				if (aData.charAt(i) == '#' || aData.charAt(i) == '$' || aData.charAt(i) == '&') {
					counter++;
				}
			}
		}
		return counter;
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
}