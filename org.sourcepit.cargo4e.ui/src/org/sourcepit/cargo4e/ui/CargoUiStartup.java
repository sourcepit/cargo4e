package org.sourcepit.cargo4e.ui;

import org.eclipse.ui.IStartup;
import org.sourcepit.cargo4e.CargoCorePlugin;

public class CargoUiStartup implements IStartup {

	@Override
	public void earlyStartup() {
		CargoCorePlugin.getCargoCore();
	}

}
