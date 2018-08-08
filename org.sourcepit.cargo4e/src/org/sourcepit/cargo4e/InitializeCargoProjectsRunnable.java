package org.sourcepit.cargo4e;

import java.io.File;
import java.io.IOException;
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
import org.eclipse.core.runtime.jobs.Job;
import org.sourcepit.cargo4j.exec.CargoMetadataCommand;
import org.sourcepit.cargo4j.model.Metadata;

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

		final List<Job> jobHandles = new ArrayList<>();

		final IResourceVisitor visitor = new IResourceVisitor() {
			@Override
			public boolean visit(IResource resource) throws CoreException {

				if (resource.getParent() == null) {
					return true;
				}

				final IProject eclipseProject = (IProject) resource;
				if (CargoCore.hasCargoNature(eclipseProject)) {

					final ICoreRunnable initProjectRunnable = new ICoreRunnable() {

						@Override
						public void run(IProgressMonitor monitor) throws CoreException {
							final File projectFolder = eclipseProject.getLocation().toFile();

							final Metadata metadata;
							try {
								metadata = new CargoMetadataCommand().execute(projectFolder);
							} catch (IOException e) {
								e.printStackTrace();
								return;
							}

							projectStateStore.setMetadata(eclipseProject, metadata);
						}
					};

					String initProjectJobName = resource.getName();
					CargoCoreJob initProjectJob = new CargoCoreJob(initProjectJobName, initProjectRunnable);
					jobHandles.add(initProjectJob);
				}
				return false;
			}
		};

		eclipseWorkspace.getRoot().accept(visitor, IResource.DEPTH_ONE, 0);

		final IProgressMonitor progressGroup = jobManager.createProgressGroup();
		progressGroup.beginTask("Initializing Cargo Projects", jobHandles.size());
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