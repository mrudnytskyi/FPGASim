package sim;

import com.thoughtworks.xstream.XStream;

import java.io.File;

/**
 * Class encapsulates way to get all necessary constants.
 *
 * @author Myroslav Rudnytskyi
 * @version 30.03.2016
 */
public class SettingsHolder {
	private static final int DEFAULT_FPGA_MAX_SIZE = 9;
	private static final int DEFAULT_MAX_BONUS = 10;
	private static final int DEFAULT_MEMORY_ACCESS_TIME = 1;
	private static final int DEFAULT_LOAD_LAST_TIME = 10;
	private static final int DEFAULT_LOAD_DATUM_TIME = 2;
	private static final int DEFAULT_NETWORK_MAX_RANDOM_TIME = 5;
	private Settings settings = new Settings();

	public SettingsHolder(File file) {
		try {
			XStream stream = new XStream();
			settings = (Settings) stream.fromXML(file);
		} catch (Exception e) {
			e.printStackTrace();
			// forget, using defaults
		}
	}

	public int getLoadLastWordTime() {
		return settings.loadLastWordTime;
	}

	public int getFpgaSize() {
		return settings.fpgaSize;
	}

	public int getBonus() {
		return settings.bonus;
	}

	public int getMemoryAccessTime() {
		return settings.memoryAccessTime;
	}

	public int getLoadDatumTime() {
		return settings.loadDatumTime;
	}

	public int getNetworkMaxRandomTime() {
		return settings.networkMaxRandomTime;
	}

	private static class Settings {
		final int fpgaSize;
		final int bonus;
		final int memoryAccessTime;
		final int loadDatumTime;
		final int networkMaxRandomTime;
		final int loadLastWordTime;

		private Settings() {
			this(
					DEFAULT_FPGA_MAX_SIZE,
					DEFAULT_MAX_BONUS,
					DEFAULT_MEMORY_ACCESS_TIME,
					DEFAULT_LOAD_DATUM_TIME,
					DEFAULT_NETWORK_MAX_RANDOM_TIME,
					DEFAULT_LOAD_LAST_TIME
			);
		}

		private Settings(int fpgaSize, int bonus, int memoryAccess, int loadDatum, int networkMaxRandom, int loadLast) {
			this.fpgaSize = fpgaSize;
			this.bonus = bonus;
			this.memoryAccessTime = memoryAccess;
			this.loadDatumTime = loadDatum;
			this.networkMaxRandomTime = networkMaxRandom;
			this.loadLastWordTime = loadLast;
		}
	}
}
