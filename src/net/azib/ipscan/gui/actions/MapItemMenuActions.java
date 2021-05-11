/*
  This file is a part of Angry IP Scanner source code,
  see http://www.angryip.org/ for more information.
  Licensed under GPLv2.
 */
package net.azib.ipscan.gui.actions;

import net.azib.ipscan.config.MapItemConfig;
import net.azib.ipscan.gui.MapCanvas;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class MapItemMenuActions {

	public static final class DeleteItem implements Listener {
		private MapItemConfig mapItemConfig;
		private String key;
		private MapCanvas canvas;
		public DeleteItem(MapItemConfig mapItemConfig, MapCanvas canvas, String key) {
			this.mapItemConfig = mapItemConfig;
			this.key = key;
			this.canvas = canvas;
		}
		public void handleEvent(Event event) {
			mapItemConfig.removeItem(key);
			canvas.redraw();
		}
	}

}
