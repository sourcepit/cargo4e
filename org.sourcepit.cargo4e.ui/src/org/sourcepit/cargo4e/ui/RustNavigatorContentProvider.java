package org.sourcepit.cargo4e.ui;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Adapters;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.sourcepit.cargo4e.CargoCorePlugin;
import org.sourcepit.cargo4e.ICargoProject;
import org.sourcepit.cargo4e.IMetadataChangedListener;
import org.sourcepit.cargo4j.model.Metadata;
import org.sourcepit.cargo4j.model.Package;

public class RustNavigatorContentProvider implements ITreeContentProvider, IMetadataChangedListener {

	private TreeViewer viewer;

	public RustNavigatorContentProvider() {
		CargoCorePlugin.getCargoCore().addMetadataChangedListener(this);
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = (TreeViewer) viewer;
	}

	@Override
	public void dispose() {
		CargoCorePlugin.getCargoCore().removeMetadataChangedListener(this);
	}

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

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((cargoProject == null) ? 0 : cargoProject.hashCode());
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
			PackagesContainer other = (PackagesContainer) obj;
			if (cargoProject == null) {
				if (other.cargoProject != null) {
					return false;
				}
			} else if (!cargoProject.equals(other.cargoProject)) {
				return false;
			}
			return true;
		}
	}

	@Override
	public void onMetadataChanged(IProject project, Metadata oldMetadata, Metadata newMetadata) {
		viewer.getControl().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				if (viewer != null && !viewer.getControl().isDisposed()) {
					final ICargoProject cargoProject = Adapters.adapt(project, ICargoProject.class);
					if (cargoProject != null) {
						viewer.refresh(new PackagesContainer(cargoProject));
					}
				}
			}
		});
	}
}
