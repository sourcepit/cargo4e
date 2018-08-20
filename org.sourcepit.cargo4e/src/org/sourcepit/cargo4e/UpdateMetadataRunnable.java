package org.sourcepit.cargo4e;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ICoreRunnable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.sourcepit.cargo4j.exec.CargoMetadataCommand;
import org.sourcepit.cargo4j.model.Metadata;

public class UpdateMetadataRunnable implements ICoreRunnable {
	private final MetadataStore projectStateStore;
	private final IProject eclipseProject;

	public UpdateMetadataRunnable(MetadataStore projectStateStore, IProject eclipseProject) {
		this.projectStateStore = projectStateStore;
		this.eclipseProject = eclipseProject;
	}

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
}