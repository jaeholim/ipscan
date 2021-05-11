package net.azib.ipscan.config;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.geometry.Point;

import java.util.*;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.stream.Stream;

public class MapItemConfig {
	private Preferences preferences;
	private final String CON_ID = "connections";
	private Map<String, Figure> figureMap;

	public MapItemConfig(Preferences preferences) {
		this.preferences = preferences.node("mapitems");
		this.figureMap = new HashMap<>();
	}

	public Point getItemPoint(String itemId) {
		Point point = new Point();
		Preferences p = preferences.node(itemId);
		point.setX(p.getInt("x", 10));
		point.setY(p.getInt("y", 10));
		return point;
	}

	public void setItemPoint(String itemId, Point point) {
		Preferences p = preferences.node(itemId);
		p.putInt("x", point.x());
		p.putInt("y", point.y());
	}

	public void removeItem(String itemId) {
		Preferences p = preferences.node(itemId);
		if(p != null) {
			try {
				p.removeNode();
				Stream.of(preferences.childrenNames()).forEach(v -> {
					Preferences tp = preferences.node(v);
					if(tp.get(CON_ID, "").contains(itemId))
						tp.put(CON_ID, tp.get(CON_ID, "").replace(itemId+",", ""));
				});
			}
			catch (BackingStoreException e) {
				e.printStackTrace();
			}
		}
	}

	public String getItemInfo(String itemId, String key) {
		Preferences p = preferences.node(itemId);
		return p.get(key, "");
	}
	public void setItemInfo(String itemId, String key, String value) {
		Preferences p = preferences.node(itemId);
		p.put(key, value);
	}

	public List<String> getItemConnections(String itemId) {
		Preferences p = preferences.node(itemId);
		return Arrays.asList(p.get(CON_ID, "").split(","));
	}

	public void addItemConnection(String itemId, String cItemId) {
		Preferences p = preferences.node(itemId);
		if(!p.get(CON_ID, "").contains(cItemId)){
			p.put(CON_ID, p.get(CON_ID, "")+cItemId+",");
		}
	}

	public List<String> getItems() {
		String [] childrenNames = null;
		try {
			childrenNames = preferences.childrenNames();
		}
		catch (BackingStoreException e) {
			e.printStackTrace();
		}
		finally {
			return childrenNames==null? Collections.emptyList():Arrays.asList(childrenNames);
		}
	}

	public void setFigure(String key, Figure f) {
		figureMap.put(key, f);
	}
	public Map<String, Figure> getAllFigure() {
		return figureMap;
	}
	public Figure getFigure(String key) {
		return figureMap.get(key);
	}
}
