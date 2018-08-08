package org.sourcepit.cargo4e;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ICoreRunnable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

public class CargoCoreJob extends Job {

	public static final Object FAMILY = new Object();

	private final ICoreRunnable runnable;

	public CargoCoreJob(String name, ICoreRunnable runnable) {
		super(name);
		this.runnable = runnable;
	}

	@Override
	public boolean belongsTo(Object family) {
		return family == FAMILY;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {
			runnable.run(monitor);
			return monitor.isCanceled() ? Status.CANCEL_STATUS : Status.OK_STATUS;
		} catch (CoreException e) {
			return e.getStatus();
		}
	}
}