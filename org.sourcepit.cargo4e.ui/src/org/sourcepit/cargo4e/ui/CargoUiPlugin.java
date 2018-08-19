package org.sourcepit.cargo4e.ui;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class CargoUiPlugin extends AbstractUIPlugin {

	public static final String BUNDLE_ID = "org.sourcepit.cargo4e.ui";

	// The shared instance
	private static CargoUiPlugin plugin;

	private CargoUi cargoUi;

	/**
	 * The constructor
	 */
	public CargoUiPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.
	 * BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

		cargoUi = new CargoUi(context.getBundle());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		cargoUi.dispose();
		cargoUi = null;

		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static CargoUiPlugin getDefault() {
		return plugin;
	}

	public static CargoUi getCargoUi() {
		return plugin.cargoUi;
	}

}
