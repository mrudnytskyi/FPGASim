package math;

import java.io.File;

import com.thoughtworks.xstream.XStream;

/**
 * 
 * @author Mir4ik
 * @version 0.1 08.03.2015
 */
public class Library {
	
	private final double[][] data;
	
	public Library() {
		data = (double[][]) new XStream().fromXML(new File("library.xml"));
	}
	
	public double[] getTask(int task) {
		return data[task];
	}
	
	public double getSquare(int task) {
		return data[task][0];
	}

	public double getTime(int task) {
		return data[task][1];
	}
}