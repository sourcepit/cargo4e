package org.sourcepit.cargo4e;

import static org.junit.Assert.assertNotNull;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ICoreRunnable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.junit.Test;

public class ActivatorTest {

	@Test
	public void test() throws CoreException {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IJobManager jobManager = Job.getJobManager();

		String projectName = "foo";

		IProject project = workspace.getRoot().getProject(projectName);

		ICoreRunnable newProjectRunnable = new NoConcurrentResourceNotificationsRunnable(jobManager,
				new NewRustProjectCoreRunnable(workspace, projectName));

		workspace.run(newProjectRunnable, project, 0, null);

		assertNotNull(project.exists());

		ICargoProject cargoProject = (ICargoProject) project.getNature(ICargoProject.NATURE_ID);
		assertNotNull(cargoProject);

	}

	public static class NoConcurrentResourceNotificationsRunnable implements ICoreRunnable {

		private final IJobManager jobManager;

		private final ICoreRunnable coreRunnable;

		public NoConcurrentResourceNotificationsRunnable(IJobManager jobManager, ICoreRunnable coreRunnable) {
			this.jobManager = jobManager;
			this.coreRunnable = coreRunnable;
		}

		@Override
		public void run(IProgressMonitor monitor) throws CoreException {
			final IJobChangeListener notifyJobCanceler = new NotifyJobCanceler();
			jobManager.addJobChangeListener(notifyJobCanceler);
			try {
				coreRunnable.run(monitor);
			} finally {
				jobManager.removeJobChangeListener(notifyJobCanceler);
			}
		}

		private static final class NotifyJobCanceler extends JobChangeAdapter {
			@Override
			public void aboutToRun(IJobChangeEvent event) {
				final Job job = event.getJob();
				if ("org.eclipse.core.internal.events.NotificationManager$NotifyJob".equals(job.getClass().getName())) {
					job.cancel();
				}
			}
		}
	}

}
