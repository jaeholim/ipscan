package net.azib.ipscan.gui.actions;

import net.azib.ipscan.config.GUIConfig;
import net.azib.ipscan.config.MapItemConfig;
import net.azib.ipscan.gui.MapCanvas;
import org.eclipse.draw2d.*;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;

public class MapPaintListener implements PaintListener {
	private Display display;
	private MapCanvas canvas;
	private GUIConfig guiConfig;
	private ImageData imageData;
	private MapItemConfig mapItemConfig;


	public MapPaintListener(Display display, MapCanvas canvas, GUIConfig guiConfig, MapItemConfig mapItemConfig) {
		this.display = display;
		this.canvas = canvas;
		this.guiConfig = guiConfig;
		this.imageData = new ImageData(guiConfig.getMapPath());
		this.mapItemConfig = mapItemConfig;

	}

	@Override
	public void paintControl(PaintEvent e) {

		int width = e.width;
		int height = e.height;

		if (imageData != null) {
			float scale = 1.0f;
			if (width != imageData.width) {
				scale = width / (float) imageData.width;
			}
			if (height != imageData.height) {
				float tmpScale = height / (float) imageData.height;
				scale = scale < tmpScale ? scale : tmpScale;
			}
			if (scale > 1.0) {
				Image scaledImage = new Image(e.display, imageData.scaledTo(Math.round(imageData.width * scale), Math.round(imageData.height * scale)));
				int imgWidth = scaledImage.getImageData().width;
				int imgHeight = scaledImage.getImageData().height;
				int bx = height / 2 - imgWidth / 2;
				int by = height / 2 - imgHeight / 2;
				System.out.println("bx : "+bx+", by : "+by+", width : "+width+", height : "+height+", mgWidth : "+imgWidth+", imgHeight : "+imgHeight);

				// Draw2d 시작
				Figure root = new Figure();
				root.setLayoutManager( new XYLayout() );

				LightweightSystem lws = new LightweightSystem(canvas);
				lws.setContents(root);

				Figure imageFigure = new ImageFigure(scaledImage);
				root.add(imageFigure, new Rectangle(0,0,-1,-1));

				float finalScale = scale;
				mapItemConfig.getItems().forEach(v -> {
					String key = mapItemConfig.getItemInfo(v, "ip");
					Figure f = itemFigure(mapItemConfig.getItemInfo(v, "name"), key, ColorConstants.red);
					mapItemConfig.setFigure(key, f);
					Point p = mapItemConfig.getItemPoint(v);
					root.add(f,	new Rectangle((int)(p.x * finalScale), (int)(p.y * finalScale), -1, -1) );
				});
				mapItemConfig.getItems().forEach(v -> {
					Figure f1 = mapItemConfig.getFigure(mapItemConfig.getItemInfo(v, "ip"));
					if(f1 != null) {
						mapItemConfig.getItemConnections(mapItemConfig.getItemInfo(v, "ip")).forEach(vv -> {
							Figure f2 = mapItemConfig.getFigure(mapItemConfig.getItemInfo(vv, "ip"));
							if(f2 != null) root.add(myConnection(f1, f2));
						});
					}
				});
				// Draw2d 종료

			}
		}
	}

	public Figure itemFigure(String name, String ip, Color color){
		RectangleFigure f = new RectangleFigure();
		f.setBackgroundColor(color);
		f.setLayoutManager( new ToolbarLayout() );
		f.setPreferredSize( 20, 20 );
		f.add( new Label( name ) );
		new MapItemMouseAction(f, ip, mapItemConfig, canvas);
		return f;
	}

	public Connection myConnection(IFigure fig1, IFigure fig2){
		PolylineConnection conn = new PolylineConnection();
		conn.setSourceAnchor( new ChopboxAnchor( fig1 ) );
		conn.setTargetAnchor( new ChopboxAnchor( fig2 ) );
		return conn;
	}

}
