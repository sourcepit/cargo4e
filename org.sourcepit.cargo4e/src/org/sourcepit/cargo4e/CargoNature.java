package org.sourcepit.cargo4e;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.sourcepit.cargo4e.model.CratesContainer;
import org.sourcepit.cargo4e.model.ICratesContainer;
import org.sourcepit.cargo4j.model.metadata.Metadata;

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
	public ICargoCore getCargoCore() {
		return CargoCorePlugin.getCargoCore();
	}

	@Override
	public Metadata getMetadata() {
		return getCargoCore().getMetadata(project);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((project == null) ? 0 : project.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		CargoNature other = (CargoNature) obj;
		if (project == null) {
			if (other.project != null) {
				return false;
			}
		} else if (!project.equals(other.project)) {
			return false;
		}
		return true;
	}
	
	@Override
	public ICratesContainer getCratesContainer() {
		return new CratesContainer(this);
	}
}
