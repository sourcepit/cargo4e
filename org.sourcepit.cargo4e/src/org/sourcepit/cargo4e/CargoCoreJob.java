package org.sourcepit.cargo4e;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ICoreRunnable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobManager;
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
	
	public static CargoCoreJob newUpdateMetadataJob(MetadataStore projectStateStore, IProject eclipseProject) {
		String jobName = eclipseProject.getName();
		UpdateMetadataRunnable runnable = new UpdateMetadataRunnable(projectStateStore, eclipseProject);
		CargoCoreJob initProjectJob = new CargoCoreJob(jobName, runnable);
		return initProjectJob;
	}

	public static void runJobsInProgressGroup(IJobManager jobManager, String name, List<CargoCoreJob> jobHandles) {
		final IProgressMonitor progressGroup = jobManager.createProgressGroup();
		progressGroup.beginTask(name, jobHandles.size());
		try {
			for (Job job : jobHandles) {
				job.setProgressGroup(progressGroup, 1);
				job.schedule();
			}
			for (Job job : jobHandles) {
				try {
					job.join();
				} catch (InterruptedException e) {
				}
			}
		} finally {
			progressGroup.done();
		}
	}
}