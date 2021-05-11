package net.azib.ipscan.gui.menu;

import net.azib.ipscan.gui.actions.ColumnsActions;
import net.azib.ipscan.gui.actions.MapItemMenuActions;
import net.azib.ipscan.gui.actions.ToolsActions;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import java.util.List;

/**
 * ColumnsMenu wrapper for type-safety.
 * This is the menu when clicking on a column header.
 */
public class MapItemMenu extends ExtendableMenu {
	public MapItemMenu(Shell parent,
					   MapItemMenuActions.DeleteItem deleteItem,
					   List<String> metaInfo) {
		super(parent, SWT.POP_UP);

		metaInfo.forEach(v -> initMenuItem(this, v));
		initMenuItem(this, null, null, null, null);
		initMenuItem(this, "menu.map.item.delete", null, null, deleteItem);
		initMenuItem(this, null, null, null, null);
	}
}
