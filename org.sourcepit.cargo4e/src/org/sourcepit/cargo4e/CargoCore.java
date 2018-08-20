package org.sourcepit.cargo4e;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ICoreRunnable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.sourcepit.cargo4j.model.Metadata;

public class CargoCore implements IResourceChangeListener {

	private final IWorkspace eclipseWorkspace;

	private final IJobManager jobManager;

	private final File stateLocation;

	private MetadataStore metadataStore;

	private boolean running = false;

	public CargoCore(IWorkspace eclipseWorkspace, IJobManager jobManager, File stateLocation) {
		this.eclipseWorkspace = eclipseWorkspace;
		this.jobManager = jobManager;
		this.stateLocation = stateLocation;

	}

	public synchronized void start() {
		if (running) {
			throw new IllegalStateException();
		}
		running = true;

		metadataStore = new MetadataStore(stateLocation);

		final InitializeCargoProjectsRunnable initRunnable = new InitializeCargoProjectsRunnable(eclipseWorkspace,
				jobManager, metadataStore);

		final CargoCoreJob initJob = new CargoCoreJob("Initialize Cargo Projects", initRunnable);
		initJob.setSystem(true);
		initJob.schedule();

		eclipseWorkspace.addResourceChangeListener(this,
				IResourceChangeEvent.POST_CHANGE | IResourceChangeEvent.PRE_CLOSE | IResourceChangeEvent.PRE_DELETE);
	}

	public synchronized void stop() {
		if (!running) {
			throw new IllegalStateException();
		}
		running = false;

		jobManager.cancel(CargoCoreJob.FAMILY);

		this.eclipseWorkspace.removeResourceChangeListener(this);

		metadataStore = null;
	}

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		switch (event.getType()) {
		case IResourceChangeEvent.PRE_CLOSE:
		case IResourceChangeEvent.PRE_DELETE:
			final IProject project = (IProject) event.getResource();
			if (hasCargoNature(project)) {
				metadataStore.setMetadata(project, null);
			}
			break;
		case IResourceChangeEvent.POST_CHANGE:

			final List<IProject> changedProjects = new ArrayList<>();

			final IResourceDeltaVisitor resourceDeltaVisitor = new IResourceDeltaVisitor() {
				@Override
				public boolean visit(IResourceDelta delta) throws CoreException {

					final IResource resource = delta.getResource();
					if (resource instanceof IWorkspaceRoot) {
						return true;
					}
					if (resource instanceof IProject) {
						return ((IProject) resource).hasNature(ICargoProject.NATURE_ID);
					}
					if (resource instanceof IFile && "Cargo.toml".equals(resource.getName())) {
						switch (delta.getKind()) {
						case IResourceDelta.ADDED:
						case IResourceDelta.CHANGED:
						case IResourceDelta.REMOVED:
							changedProjects.add(resource.getProject());
							break;
						default:
							break;
						}
					}
					return false;
				}
			};

			try {
				event.getDelta().accept(resourceDeltaVisitor);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (!changedProjects.isEmpty()) {
				final List<CargoCoreJob> updateJobs = new ArrayList<>();
				for (IProject changedProject : changedProjects) {
					updateJobs.add(CargoCoreJob.newUpdateMetadataJob(metadataStore, changedProject));
				}

				final CargoCoreJob updateJob = new CargoCoreJob("Update Cargo Projects", new ICoreRunnable() {
					@Override
					public void run(IProgressMonitor monitor) throws CoreException {
						CargoCoreJob.runJobsInProgressGroup(jobManager, "Updating Cargo Projects", updateJobs);
					}
				});
				updateJob.setSystem(true);
				updateJob.schedule();
			}

			break;
		default:
			throw new IllegalStateException();
		}
	}

	public Metadata getMetadata(IProject eclipseProject) {
		return hasCargoNature(eclipseProject) ? metadataStore.getMetadata(eclipseProject) : null;
	}

	static boolean hasCargoNature(IProject project) {
		try {
			return project.hasNature(ICargoProject.NATURE_ID);
		} catch (CoreException e) {
			return false;
		}
	}

}
