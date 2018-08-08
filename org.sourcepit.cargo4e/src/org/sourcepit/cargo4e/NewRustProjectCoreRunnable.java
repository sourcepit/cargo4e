package org.sourcepit.cargo4e;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ICoreRunnable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.sourcepit.cargo4j.exec.CargoNewProjectCommand;

public class NewRustProjectCoreRunnable implements ICoreRunnable {
	private final String projectName;
	private final IWorkspace workspace;

	public NewRustProjectCoreRunnable(IWorkspace workspace, String projectName) {
		this.projectName = projectName;
		this.workspace = workspace;
	}

	@Override
	public void run(IProgressMonitor pm) throws CoreException {
		final SubMonitor monitor = SubMonitor.convert(pm, "Creating " + projectName, 100);

		final IProjectDescription description = workspace.newProjectDescription(projectName);

		final String[] natures = description.getNatureIds();
		final String[] newNatures = new String[natures.length + 1];
		System.arraycopy(natures, 0, newNatures, 0, natures.length);
		newNatures[natures.length] = ICargoProject.NATURE_ID;
		description.setNatureIds(newNatures);

		final File projectFile = new File(workspace.getRoot().getLocation().toFile(), projectName);
		try {
			new CargoNewProjectCommand().execute(projectFile);
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, CargoCorePlugin.BUNDLE_ID, "Failed to create Rust project", e));
		}
		monitor.split(25).done();

		final IProject project = workspace.getRoot().getProject(projectName);
		project.create(description, monitor.split(25));
		project.open(monitor.split(50));
	}
}