package gui;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Abstract class for representing frames in application. Please, make sure,
 * that all of them use it as superclass.
 * <p>
 * Class extends class from the standard library - {@link JFrame}.
 * 
 * @author Mir4ik
 * @version 0.1 18.03.2015
 */
public abstract class Frame extends JFrame {

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
		 * @param name			action name
		 * @param smallIcon		path to small image
		 * @param largeIcon		path to large image
		 */
		public Action(String name, String smallIcon, String largeIcon) {
			putValue(NAME, name);
			putValue(SMALL_ICON, new ImageIcon(smallIcon));
			putValue(LARGE_ICON_KEY, new ImageIcon(largeIcon));
		}
	}
	
	private static final long serialVersionUID = 1581013918976729599L;

	public Frame(String title) {
		super(title);
	}

	public void showError(String msg) {
		String path = "res\\error_big.png";
		JOptionPane.showMessageDialog(this, msg, "Error",
				JOptionPane.ERROR_MESSAGE, new ImageIcon(path));
	}
	
	public void showInfo(String msg) {
		String path = "res\\info_big.png";
		JOptionPane.showMessageDialog(this, msg, "About",
				JOptionPane.INFORMATION_MESSAGE, new ImageIcon(path));
	}
	
	public boolean showQuestion(String msg) {
		String path = "res\\question_big.png";
		if (JOptionPane.showConfirmDialog(this, msg, "Question",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
				new ImageIcon(path)) == JOptionPane.YES_OPTION) {
					return true;
		}
		return false;
	}
	
	public void showWarning(String msg) {
		String path = "res\\warning_big.png";
		JOptionPane.showMessageDialog(this, msg, "Warning",
				JOptionPane.WARNING_MESSAGE, new ImageIcon(path));
	}
}