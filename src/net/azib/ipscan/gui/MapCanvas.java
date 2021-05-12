package net.azib.ipscan.gui;

import net.azib.ipscan.config.GUIConfig;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;

public class MapCanvas extends Canvas {

	public Display display;
	public Shell shell;

	private Image image;
	private int width;
	private int height;
	private double scale = 1.0;
	private int ix = 0;
	private int iy = 0;

	public MapCanvas(Shell parent, GUIConfig guiConfig) {
		super(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.NO_REDRAW_RESIZE);
		display = parent.getDisplay();

		image = new Image(display, guiConfig.getMapPath());
		width = image.getImageData().width;
		height = image.getImageData().height;

		//Canvas에 스크롤바를 설정한다.
		ScrollBar horizontalBar = getHorizontalBar();
		horizontalBar.setVisible(true);
		horizontalBar.setMinimum(0);
		horizontalBar.setEnabled(false);
		horizontalBar.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				scrollHorizontally((ScrollBar)event.widget);
			}
		});

		ScrollBar verticalBar = getVerticalBar();
		verticalBar.setVisible(true);
		verticalBar.setMinimum(0);
		verticalBar.setEnabled(false);
		verticalBar.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				scrollVertically((ScrollBar)event.widget);
			}
		});

		shell = parent.getShell();
		shell.addControlListener(new ControlAdapter(){
			public void controlResized(ControlEvent e) {
				resizeShell(e);
			}

		});

		addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {

				int scaledWidth = (int) ((double) width * scale);
				int scaledHeight = (int) ((double) height * scale);
				System.out.println("scale : "+scale+", sw : "+scaledWidth+", sh : "+scaledHeight);
				e.gc.drawImage(
						image,
						0,
						0,
						width,
						height,
						ix,
						iy,
						scaledWidth,
						scaledHeight);

			}
		});
	}

	public void changeScale(double d) {
		scale = d;
		resizeScrollBars();
		redraw();
	}

	protected void resizeShell(ControlEvent e) {
		if (image == null || shell.isDisposed()){
			return;
		}
		resizeScrollBars();
	}

	private void resizeScrollBars() {
		ScrollBar horizontal = this.getHorizontalBar();
		ScrollBar vertical = this.getVerticalBar();
		Rectangle canvasBounds = this.getClientArea();
		int scaledWidth = (int)Math.round(width * scale);
		if (scaledWidth > canvasBounds.width) {
			// 이미지가 캔버스보다 큰경우
			horizontal.setEnabled(true);
			horizontal.setMaximum(scaledWidth);
			horizontal.setThumb(canvasBounds.width);
			horizontal.setPageIncrement(canvasBounds.width);
		} else {
			//  캔버스가 이미지보다 큰경우
			horizontal.setEnabled(false);
			if (ix != 0) {
				ix = 0;
				this.redraw();
			}
		}
		int scaledHeight = (int)Math.round(height * scale);
		if (scaledHeight > canvasBounds.height) {
			// 이미지가 캔버스보다 큰경우
			vertical.setEnabled(true);
			vertical.setMaximum(scaledHeight);
			vertical.setThumb(canvasBounds.height);
			vertical.setPageIncrement(canvasBounds.height);
		} else {
			// 캔버스가 이미지보다 큰경우
			vertical.setEnabled(false);
			if (iy != 0) {
				// Make sure the image is completely visible.
				iy = 0;
				this.redraw();
			}
		}
	}

	protected void scrollVertically(ScrollBar bar) {
		Rectangle canvasBounds = this.getClientArea();

		int scaledWidth = (int)Math.round(width * scale);
		int scaledHeight = (int)Math.round(height * scale);
		if (scaledHeight > canvasBounds.height) {
			int y = -bar.getSelection();
			if (y + scaledHeight < canvasBounds.height) {
				y = canvasBounds.height - scaledHeight;
			}
			this.scroll(ix, y, ix, iy, scaledWidth, scaledHeight, false);
			iy = y;
		}
	}

	protected void scrollHorizontally(ScrollBar bar) {
		Rectangle canvasBounds = this.getClientArea();
		int scaledWidth = (int)Math.round(width * scale);
		int scaledHeight = (int)Math.round(height * scale);
		if (scaledWidth > canvasBounds.width) {
			int x = -bar.getSelection();
			if (x + scaledWidth < canvasBounds.width) {
				x = canvasBounds.width - scaledWidth;
			}
			this.scroll(x, iy, ix, iy, scaledWidth, scaledHeight, false);
			ix = x;
		}
	}

	public void dispose() {
		image.dispose();
		super.dispose();
	}
}


