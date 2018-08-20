package org.sourcepit.cargo4e;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ICoreRunnable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.IJobManager;

public class InitializeCargoProjectsRunnable implements ICoreRunnable {
	private final IWorkspace eclipseWorkspace;

	private final IJobManager jobManager;

	private final MetadataStore projectStateStore;

	public InitializeCargoProjectsRunnable(IWorkspace eclipseWorkspace, IJobManager jobManager,
			MetadataStore projectStateStore) {
		this.eclipseWorkspace = eclipseWorkspace;
		this.jobManager = jobManager;
		this.projectStateStore = projectStateStore;
	}

	@Override
	public void run(IProgressMonitor monitor) throws CoreException {

		final List<CargoCoreJob> jobHandles = new ArrayList<>();

		final IResourceVisitor visitor = new IResourceVisitor() {
			@Override
			public boolean visit(IResource resource) throws CoreException {

				if (resource.getParent() == null) {
					return true;
				}

				final IProject project = (IProject) resource;
				if (CargoCore.hasCargoNature(project)) {
					jobHandles.add(CargoCoreJob.newUpdateMetadataJob(projectStateStore, project));
				}

				return false;
			}

		};

		eclipseWorkspace.getRoot().accept(visitor, IResource.DEPTH_ONE, 0);

		CargoCoreJob.runJobsInProgressGroup(jobManager, "Initializing Cargo Projects", jobHandles);
	}

}