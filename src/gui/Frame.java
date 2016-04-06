package gui;

import javax.swing.*;
import java.awt.*;

import static javax.swing.JOptionPane.*;

/**
 * Abstract class for representing frames in application. Please, make sure,
 * that all of them use it as superclass.
 * <p>
 * Class extends class from the standard library - {@link JFrame}.
 */
public abstract class Frame extends JFrame {

	private static final long serialVersionUID = 1581013918976729599L;

	public Frame(String title) {
		super(title);
	}

	public static void showError(Throwable e) {
		showMessageDialog(null, e.getMessage(), "Error " + e.getClass().getSimpleName(), ERROR_MESSAGE, icon("error_big"));
	}

	public static void showError(String msg) {
		showMessageDialog(null, msg, "Error", ERROR_MESSAGE, icon("error_big"));
	}

	public static void showInfo(String msg) {
		showMessageDialog(null, msg, "Info", INFORMATION_MESSAGE, icon("info_big"));
	}

	public static boolean showQuestion(String msg) {
		int result = showConfirmDialog(null, msg, "Question", YES_NO_OPTION, QUESTION_MESSAGE, icon("question_big"));
		return result == YES_OPTION;
	}

	public static void showWarning(String msg) {
		showMessageDialog(null, msg, "Warning", WARNING_MESSAGE, icon("warning_big"));
	}

	private static ImageIcon icon(String iconName) {
		return new ImageIcon("res\\" + iconName + ".png");
	}

	/**
	 * Moves this component to a screen center location.
	 */
	protected void moveToScreenCenter() {
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		double x = (d.getWidth() - getWidth()) / 2;
		double y = (d.getHeight() - getHeight()) / 2;
		setLocation((int) x, (int) y);
	}

	/**
	 * Abstract class for representing actions. Please, make sure, that all of
	 * them (in menu items, buttons and toolbar buttons) use it as superclass.
	 * <p>
	 * Class uses default implementation of {@link javax.swing.Action} interface
	 * - abstract class {@link AbstractAction}.
	 *
	 * @author Mir4ik
	 * @version 0.1 18.03.2015
	 */
	protected abstract class Action extends AbstractAction {

		private static final long serialVersionUID = 3675827226774345460L;

		/**
		 * Note, that constructor can take <code>null</code> values.
		 *
		 * @param name      action name
		 * @param smallIcon path to small image
		 * @param largeIcon path to large image
		 */
		public Action(String name, String smallIcon, String largeIcon) {
			putValue(javax.swing.Action.NAME, name);
			putValue(javax.swing.Action.SMALL_ICON, new ImageIcon(smallIcon));
			putValue(javax.swing.Action.LARGE_ICON_KEY, new ImageIcon(largeIcon));
		}
	}
}