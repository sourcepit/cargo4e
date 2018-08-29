package org.sourcepit.cargo4e;

import java.io.File;
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
import org.sourcepit.cargo4j.model.toolchain.ToolchainIdentifier;

public class InitializeCargoProjectsRunnable implements ICoreRunnable {
	private final IWorkspace eclipseWorkspace;

	private final IJobManager jobManager;

	private final MetadataStore projectStateStore;

	private final File rustupExecutable;

	private final ToolchainIdentifier toolchain;

	public InitializeCargoProjectsRunnable(IWorkspace eclipseWorkspace, IJobManager jobManager,
			MetadataStore projectStateStore, File rustupExecutable, ToolchainIdentifier toolchain) {
		this.eclipseWorkspace = eclipseWorkspace;
		this.jobManager = jobManager;
		this.projectStateStore = projectStateStore;
		this.rustupExecutable = rustupExecutable;
		this.toolchain = toolchain;
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
					jobHandles.add(
							CargoCoreJob.newUpdateMetadataJob(projectStateStore, project, rustupExecutable, toolchain));
				}

				return false;
			}

		};

		eclipseWorkspace.getRoot().accept(visitor, IResource.DEPTH_ONE, 0);

		CargoCoreJob.runJobsInProgressGroup(jobManager, "Initializing Cargo Projects", jobHandles);
	}

}