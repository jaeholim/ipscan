package net.azib.ipscan.gui.actions;

import net.azib.ipscan.config.MapItemConfig;
import net.azib.ipscan.gui.MapCanvas;
import net.azib.ipscan.gui.menu.MapItemMenu;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;

import java.util.Arrays;
import java.util.List;

public class MapItemMouseAction implements MouseListener, MouseMotionListener {
	private final Figure figure;
	private final String ip;
	private Point location;
	private final MapItemConfig mapItemConfig;
	private final MapCanvas canvas;

	public MapItemMouseAction(Figure figure, String ip, MapItemConfig mapItemConfig, MapCanvas canvas) {
		this.figure = figure;
		this.ip = ip;
		this.mapItemConfig = mapItemConfig;
		this.canvas = canvas;
		figure.addMouseListener(this);
		figure.addMouseMotionListener(this);
	}

	@Override
	public void mousePressed(MouseEvent me) {
		// left button
		if(me.button == 1) {
			location = me.getLocation();
			me.consume();
		}
		// right button
		else if(me.button == 3) {
			List<String> info = Arrays.asList(mapItemConfig.getItemInfo(ip, "name"), mapItemConfig.getItemInfo(ip, "ip"));
			MapItemMenu menu = new MapItemMenu(canvas.getShell(), new MapItemMenuActions.DeleteItem(mapItemConfig, canvas, ip), info);
			menu.setLocation(canvas.display.getCursorLocation());
			menu.setVisible(true);
		}

	}

	@Override
	public void mouseDragged(MouseEvent me) {
		Point newLocation = me.getLocation();
		if( location==null || newLocation == null)
			return;
		// calculate offset wrt last location
		Dimension offset = newLocation.getDifference( location );
		if( offset.width==0 && offset.height==0 )
			return;
		// exchange location
		location = newLocation;
		mapItemConfig.setItemPoint(ip, location);

		// old Bounds are dirty
		figure.getUpdateManager()
				.addDirtyRegion(figure.getParent(), figure.getBounds());

		// translate figure
		figure.translate( offset.width, offset.height );

		// new Bounds are dirty
		figure.getUpdateManager()
				.addDirtyRegion( figure.getParent(), figure.getBounds() );

		// new Bounds: set parent constraint
		figure.getParent().getLayoutManager()
				.setConstraint(figure, figure.getBounds() );
		//
		me.consume();
	}

	@Override
	public void mouseReleased(MouseEvent me) {
		// left button
		if(me.button == 1) {
			if( location==null )
				return;
			location = null;
			me.consume();
		}
		// right button
		else if(me.button == 3) {
			me.consume();
		}
	}

	@Override
	public void mouseEntered(MouseEvent me) {}

	@Override
	public void mouseExited(MouseEvent me) {}

	@Override
	public void mouseHover(MouseEvent me) {}

	@Override
	public void mouseMoved(MouseEvent me) {}

	@Override
	public void mouseDoubleClicked(MouseEvent me) {}

}
