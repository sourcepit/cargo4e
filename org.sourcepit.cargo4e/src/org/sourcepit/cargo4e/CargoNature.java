package org.sourcepit.cargo4e;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.sourcepit.cargo4j.model.Metadata;

public class CargoNature implements ICargoProject, IProjectNature {

	private IProject project;

	@Override
	public IProject getProject() {
		return project;
	}

	@Override
	public void setProject(IProject project) {
		this.project = project;
	}

	@Override
	public void configure() throws CoreException {
	}

	@Override
	public void deconfigure() throws CoreException {
	}

	@Override
	public Metadata getMetadata() {
		return CargoCorePlugin.getCargoCore().getMetadata(project);
	}
}