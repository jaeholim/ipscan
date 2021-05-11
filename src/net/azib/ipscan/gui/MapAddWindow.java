package net.azib.ipscan.gui;

import net.azib.ipscan.config.GUIConfig;
import net.azib.ipscan.config.Labels;
import net.azib.ipscan.config.MapItemConfig;
import net.azib.ipscan.core.ScanningResult;
import net.azib.ipscan.core.ScanningResultList;
import net.azib.ipscan.gui.util.LayoutHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.*;

import java.util.stream.Stream;

import static net.azib.ipscan.gui.util.LayoutHelper.*;

public class MapAddWindow extends AbstractModalDialog {
	private GUIConfig guiConfig;
	private MapItemConfig mapItemConfig;
	private ResultTable resultTable;
	private ScanningResultList scanningResults;
	private MapCanvas canvas;
	private List lastFocusList;
	private List selectedList;
	private List registeredList;

	int resultIndex;

	public MapAddWindow(GUIConfig guiConfig, MapItemConfig mapItemConfig, ResultTable resultTable, ScanningResultList scanningResults, MapCanvas canvas) {
		this.guiConfig = guiConfig;
		this.mapItemConfig = mapItemConfig;
		this.resultTable = resultTable;
		this.scanningResults = scanningResults;
		this.canvas = canvas;
	}
	
	@Override
	protected void populateShell() {
		resultIndex = resultTable.getSelectionIndex();
		ScanningResult result = resultTable.getSelectedResult();
		String curIp = result.getAddress().getHostAddress();
		java.util.List<String> aleadyConIps = mapItemConfig.getItemConnections(curIp);

		Display currentDisplay = Display.getCurrent();
		Shell parent = currentDisplay != null ? currentDisplay.getActiveShell() : null;
		shell = new Shell(parent, SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);

		shell.setText(Labels.getLabel("title.mapAdd"));
		shell.setLayout(LayoutHelper.formLayout(10, 10, 4));

		Label ipLabel = new Label(shell, SWT.WRAP);
		ipLabel.setText("등록 IP : "+curIp);

		Label messageLabel = new Label(shell, SWT.WRAP);
		messageLabel.setText(Labels.getLabel("text.mapAdd.info"));
		messageLabel.setLayoutData(formData(null, null, new FormAttachment(ipLabel, 5), null));

		Label selectedLabel = new Label(shell, SWT.NONE);
		selectedLabel.setText(Labels.getLabel("text.mapAdd.selectedList"));
		selectedLabel.setLayoutData(formData(null, null, new FormAttachment(messageLabel, 5), null));

		selectedList = lastFocusList = new List(shell, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		selectedList.setLayoutData(formData(160, 250, new FormAttachment(0), null, new FormAttachment(selectedLabel), null));

		// selected item
		mapItemConfig.getItems().stream()
				.filter(v -> !v.equals(curIp) && aleadyConIps.stream().noneMatch(vv -> vv.equals(v)))
				.forEach(v -> selectedList.add(v));

		Font iconFont = iconFont(shell);

		Button addButton = new Button(shell, SWT.NONE);
		addButton.setText(Labels.getLabel("button.left"));
		addButton.setToolTipText(Labels.getLabel("button.left.hint"));
		addButton.setFont(iconFont);

		Button removeButton = new Button(shell, SWT.NONE);
		removeButton.setText(Labels.getLabel("button.right"));
		removeButton.setToolTipText(Labels.getLabel("button.right.hint"));
		removeButton.setFont(iconFont);

		addButton.setLayoutData(formData(new FormAttachment(selectedList), null, new FormAttachment(selectedLabel, 16), null));
		removeButton.setLayoutData(formData(new FormAttachment(selectedList), null, new FormAttachment(addButton), null));

		Label registeredLabel = new Label(shell, SWT.NONE);
		registeredLabel.setText(Labels.getLabel("text.mapAdd.availableList"));
		registeredLabel.setLayoutData(formData(new FormAttachment(addButton, 10), null, new FormAttachment(messageLabel, 5), null));


		registeredList = new List(shell, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		registeredList.setLayoutData(formData(160, 250, new FormAttachment(addButton, 10), null, new FormAttachment(registeredLabel), null));

		aleadyConIps.forEach(v -> registeredList.add(v));

		Button okButton = new Button(shell, SWT.NONE);
		okButton.setText(Labels.getLabel("button.OK"));

		Button cancelButton = new Button(shell, SWT.NONE);
		cancelButton.setText(Labels.getLabel("button.cancel"));

		positionButtonsInFormLayout(okButton, cancelButton, registeredList);

		SelectFetchersDialog.AddRemoveButtonListener addButtonListener = new SelectFetchersDialog.AddRemoveButtonListener(registeredList, selectedList);
		addButton.addListener(SWT.Selection, addButtonListener);
		registeredList.addListener(SWT.MouseDoubleClick, addButtonListener);
		SelectFetchersDialog.AddRemoveButtonListener removeButtonListener = new SelectFetchersDialog.AddRemoveButtonListener(selectedList, registeredList);
		removeButton.addListener(SWT.Selection, removeButtonListener);
		selectedList.addListener(SWT.MouseDoubleClick, removeButtonListener);

		registeredList.addSelectionListener(new MapAddWindow.ListFocusListener());
		selectedList.addSelectionListener(new MapAddWindow.ListFocusListener());

		// this is a workaround for limitation of FormLayout to remove the extra edge below the form
		shell.layout();
		Rectangle bounds = registeredList.getBounds();
		messageLabel.setLayoutData(formData(bounds.x + bounds.width - 10, SWT.DEFAULT, new FormAttachment(0), null, null, null));

		shell.pack();

		cancelButton.addListener(SWT.Selection, e -> close());
		okButton.addListener(SWT.Selection, event -> {
			saveItemToMap((mapItemConfig.getItems().size()+1)+"_item" , result.getAddress().getHostAddress());
			close();
		});
	}

	class ListFocusListener extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			lastFocusList = (List) e.getSource();
		}
	}

	private void saveItemToMap(String name, String ip){
		mapItemConfig.setItemInfo(ip, "name", name);
		mapItemConfig.setItemInfo(ip, "ip", ip);
		Stream.of(registeredList.getItems())
				.forEach(v -> mapItemConfig.addItemConnection(ip, v));
		canvas.redraw();
	}
}
