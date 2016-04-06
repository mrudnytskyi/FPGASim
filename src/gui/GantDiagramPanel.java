package gui;

import javax.swing.*;
import java.awt.*;

/**
 * Class represents panel for diagram drawing.
 */
public class GantDiagramPanel extends JPanel {

	private static final int SCALING = 10;

	private static final long serialVersionUID = -2465217224577309839L;

	private final TimeTracks data;

	public GantDiagramPanel(TimeTracks data) {
		this.data = data;
	}

	@Override
	public void paint(Graphics g) {
		for (int i = 0; i < data.getTracksCount(); i++) {
			String temp = data.getTrack(i);
			for (int j = 0; j < temp.length(); j++) {
				switch (temp.charAt(j)) {
					case ' ':
						drawEmpty(g, j * GantDiagramPanel.SCALING, i * GantDiagramPanel.SCALING);
						break;
					case '#':
						drawLoad(g, j * GantDiagramPanel.SCALING, i * GantDiagramPanel.SCALING);
						break;
					case '$':
						drawLoadLast(g, j * GantDiagramPanel.SCALING, i * GantDiagramPanel.SCALING);
						break;
					case '&':
						drawData(g, j * GantDiagramPanel.SCALING, i * GantDiagramPanel.SCALING);
						break;
					case '*':
						drawWork(g, j * GantDiagramPanel.SCALING, i * GantDiagramPanel.SCALING);
						break;
				}
			}
		}
	}

	private void drawEmpty(Graphics g, int x, int y) {
		g.setColor(Color.GRAY);
		draw(g, x, y);
	}

	private void drawLoad(Graphics g, int x, int y) {
		g.setColor(Color.DARK_GRAY);
		draw(g, x, y);
	}

	private void drawLoadLast(Graphics g, int x, int y) {
		g.setColor(Color.BLACK);
		draw(g, x, y);
	}

	private void drawData(Graphics g, int x, int y) {
		g.setColor(Color.RED);
		draw(g, x, y);
	}

	private void drawWork(Graphics g, int x, int y) {
		g.setColor(Color.GREEN);
		draw(g, x, y);
	}

	private void draw(Graphics g, int x, int y) {
		g.drawRect(x, y, GantDiagramPanel.SCALING, GantDiagramPanel.SCALING);
		g.fillRect(x, y, GantDiagramPanel.SCALING, GantDiagramPanel.SCALING);
	}
}