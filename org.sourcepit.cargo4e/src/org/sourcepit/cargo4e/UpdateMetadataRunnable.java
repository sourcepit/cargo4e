package org.sourcepit.cargo4e;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ICoreRunnable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.sourcepit.cargo4j.exec.CargoMetadataCommand;
import org.sourcepit.cargo4j.model.metadata.Metadata;
import org.sourcepit.cargo4j.model.toolchain.ToolchainIdentifier;

public class UpdateMetadataRunnable implements ICoreRunnable {
	private final MetadataStore projectStateStore;
	private final IProject eclipseProject;
	private final File rustupExecutable;
	private final ToolchainIdentifier toolchain;

	public UpdateMetadataRunnable(MetadataStore projectStateStore, IProject eclipseProject, File rustupExecutable,
			ToolchainIdentifier toolchain) {
		this.projectStateStore = projectStateStore;
		this.eclipseProject = eclipseProject;
		this.rustupExecutable = rustupExecutable;
		this.toolchain = toolchain;
	}

	@Override
	public void run(IProgressMonitor monitor) throws CoreException {
		final File projectFolder = eclipseProject.getLocation().toFile();
		final Metadata metadata;
		try {
			metadata = new CargoMetadataCommand(projectFolder, rustupExecutable, toolchain, true).execute();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		projectStateStore.setMetadata(eclipseProject, metadata);
	}
}