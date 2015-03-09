package math;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;

/**
 * Utility class for reading files.
 * 
 * @author Mir4ik
 * @version 0.1 17.01.2015
 */
public final class FilesFacade {
	
	private FilesFacade() {}

	public static String readTXT(String file) throws Exception {
		return readTXT(new File(file));
	}
	
	public static String readTXT(File file) throws Exception {
		StringBuilder sb = new StringBuilder();
		try {
			FileReader txtInput = new FileReader(file);
			int c = 0;
			try {
				while (c != -1) {
					c = txtInput.read();
					sb.append((char) c);
				}
			} finally {
				txtInput.close();
			}
		} catch (IOException e) {
			throw new Exception(
					"Exception while reading " + file + " : ", e);
		}
		return sb.substring(0, sb.length() - 1).toString();
	}
	
	public static void writeTXT(String f, String str) throws Exception {
		writeTXT(new File(f), str);
	}
	
	public static void writeTXT(File file, String s) throws Exception {
		try {
			FileWriter writer = new FileWriter(file);
			try {
				writer.write(s);
			} finally {
				writer.close();
			}
		} catch (IOException e) {
			throw new Exception(
					"Exception while writing to " + file + " : ", e);
		}	
	}
	
	public static Object readXML(String file) throws Exception {
		return readXML(new File(file));
	}
	
	public static Object readXML(File file) throws Exception {
		try {
			XStream stream = new XStream();
			return stream.fromXML(file);
		} catch (XStreamException e) {
			throw new Exception(
					"Exception while reading " + file + " : ", e);
		}
	}
	
	public static void writeXML(String f, Object o) throws Exception {
		writeXML(new File(f), o);
	}
	
	public static void writeXML(File f, Object o) throws Exception {
		try {
			XStream stream = new XStream();
			stream.toXML(o, new FileWriter(f));
		} catch (Exception e) {
			throw new Exception(
					"Exception while writing to " + f + " : ", e);
		}
	}
}