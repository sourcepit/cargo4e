package org.sourcepit.cargo4e;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class CargoCorePlugin implements BundleActivator {

	public static final String BUNDLE_ID = "org.sourcepit.cargo4e";

	private static CargoCorePlugin plugin;

	private static BundleContext context;

	private CargoCore cargoCore;

	static BundleContext getContext() {
		return context;
	}

	@Override
	public void start(BundleContext bundleContext) throws Exception {

		CargoCorePlugin.context = bundleContext;

		plugin = this;

		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final IJobManager jobManager = Job.getJobManager();

		final IPath stateLocation = Platform.getStateLocation(bundleContext.getBundle());
		cargoCore = new CargoCore(workspace, jobManager, stateLocation.toFile());
		cargoCore.start();
	}

	public static ICargoCore getCargoCore() {
		return plugin.cargoCore;
	}

	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		cargoCore.stop();
		cargoCore = null;
		plugin = null;
		CargoCorePlugin.context = null;
	}

}
