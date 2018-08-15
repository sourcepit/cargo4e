package org.sourcepit.cargo4e.ui;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Adapters;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.sourcepit.cargo4e.ICargoProject;
import org.sourcepit.cargo4j.model.Metadata;
import org.sourcepit.cargo4j.model.Package;

public class RustNavigatorContentProvider implements ITreeContentProvider {

	@Override
	public Object[] getElements(Object inputElement) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		final ICargoProject cargoProject = Adapters.adapt(parentElement, ICargoProject.class);
		if (cargoProject != null) {
			return new Object[] { new PackagesContainer(cargoProject) };
		}

		if (parentElement instanceof PackagesContainer) {
			return ((PackagesContainer) parentElement).getPackages().toArray();
		}

		return new Object[0];
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof PackagesContainer) {
			return ((PackagesContainer) element).getCargoProject().getProject();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;
	}

	public static class PackagesContainer {
		private final ICargoProject cargoProject;

		public PackagesContainer(ICargoProject cargoProject) {
			this.cargoProject = cargoProject;
		}

		public ICargoProject getCargoProject() {
			return cargoProject;
		}

		public List<Package> getPackages() {
			Metadata metadata = cargoProject.getMetadata();
			if (metadata != null) {
				return metadata.getPackages();
			}
			return Collections.emptyList();
		}

	}
}
