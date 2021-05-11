package net.azib.ipscan.gui;

import org.eclipse.draw2d.Figure;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapCanvas extends Canvas {

	public Display display;
	public Shell shell;


	public MapCanvas(Shell parent, Display display) {
		super(parent, SWT.NO_REDRAW_RESIZE);
		this.display = display;
		this.shell = parent;
	}

}


