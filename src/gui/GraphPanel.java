package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;

import com.mxgraph.layout.mxCompactTreeLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxICell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;

/**
 * Panel, containing graph of application. 
 * <p>
 * Note, that this class uses pattern <tt>Observer</tt>, so it implements
 * interface {@link Observer}.
 * 
 * @author Mir4ik
 * @version 0.1 16.03.2015
 */
public class GraphPanel extends JPanel implements Observer {

	private static final long serialVersionUID = 5462589354217696759L;
	
	private final mxGraph graph = new mxGraph();
	
	private mxGraphComponent graphComponent;
	
	private final List<mxICell> vertexes = new ArrayList<mxICell>();
	
	private final List<mxICell> edges = new ArrayList<mxICell>();
	
	private static int counter = 0;

	public GraphPanel() {
		setLayout(new BorderLayout());
		graph.getModel().beginUpdate();
		try {
			mxStylesheet styles = graph.getStylesheet();
			Map<String, Object> defVrt = styles.getDefaultVertexStyle();
			defVrt.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
			styles.setDefaultVertexStyle(defVrt);
			Hashtable<String, Object> cstArr = new Hashtable<String, Object>();
			cstArr.put(mxConstants.STYLE_STROKECOLOR, 
					mxUtils.hexString(Color.BLACK));
			cstArr.put(mxConstants.STYLE_EDGE, 
					mxConstants.EDGESTYLE_TOPTOBOTTOM);
			styles.putCellStyle("customArrow", cstArr);
			Hashtable<String, Object> cstVrt = new Hashtable<String, Object>();
			cstVrt.put(mxConstants.STYLE_FILLCOLOR, 
					mxUtils.hexString(Color.LIGHT_GRAY));
			cstVrt.put(mxConstants.STYLE_STROKECOLOR, 
					mxUtils.hexString(Color.BLACK));
			cstVrt.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
			cstVrt.put(mxConstants.STYLE_FONTCOLOR,
					mxUtils.hexString(Color.WHITE));
			styles.putCellStyle("customVertex", cstVrt);
		} finally {
			graph.getModel().endUpdate();
		}
		graph.setCellsEditable(false);
		graph.setCellsResizable(false);
		graph.setCellsBendable(false);
		graph.setConnectableEdges(false);
		graphComponent = new mxGraphComponent(graph);
		add(graphComponent, BorderLayout.CENTER);
	}
	
	public void addVertex() {
		graph.getModel().beginUpdate();
		try {
			mxICell vertex = (mxICell) graph.insertVertex(
					graph.getDefaultParent(), null, counter,
					60, 60, 60, 60, "customVertex");
			counter++;
			vertexes.add(vertex);
		} finally {
			graph.getModel().endUpdate();
		}
		repaint();
	}
	
	public void addEdge(int from, int to) {
		graph.getModel().beginUpdate();
		try {
			mxICell edge = (mxICell) graph.insertEdge(
					graph.getDefaultParent(), null, "", vertexes.get(from),
					vertexes.get(to), "customArrow");
			edges.add(edge);
		} finally {
			graph.getModel().endUpdate();
		}
		repaint();
	}
	
	public int[][] createTransitions() {
		int[][] transitions = new int[vertexes.size()][vertexes.size()];
		Iterator<mxICell> iterator = edges.iterator();
		while (iterator.hasNext()) {
			mxCell edge = (mxCell) iterator.next();
			transitions[vertexes.indexOf(edge.getSource())]
					[vertexes.indexOf(edge.getTarget())] = 1;
		}
		return transitions;
	}

	@Override
	public void update(Observable o, Object arg) {
		@SuppressWarnings("unchecked")
		//TODO rewrite!
		List<int[]> data = (List<int[]>) arg;
		Object[] edges = graph.getAllEdges(vertexes.toArray());
		for (Object edge : edges) {
			graph.getModel().remove(edge);
		}
		this.edges.clear();
		for (int[] cur : data) {
			addEdge(cur[0], cur[1]);
		}
		if (!vertexes.isEmpty()) {
			mxCompactTreeLayout layout = new mxCompactTreeLayout(graph, false);
			layout.execute(null, vertexes.get(0));
			repaint();
		}
	}
}