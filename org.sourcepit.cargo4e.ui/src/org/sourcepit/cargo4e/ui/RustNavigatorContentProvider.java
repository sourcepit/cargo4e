package org.sourcepit.cargo4e.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Adapters;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.sourcepit.cargo4e.CargoCorePlugin;
import org.sourcepit.cargo4e.ICargoCore;
import org.sourcepit.cargo4e.ICargoProject;
import org.sourcepit.cargo4e.IMetadataChangedListener;
import org.sourcepit.cargo4e.model.ICrate;
import org.sourcepit.cargo4e.model.ICratesContainer;
import org.sourcepit.cargo4e.model.IRustFolder;
import org.sourcepit.cargo4e.toolchain.IToolchain;
import org.sourcepit.cargo4j.model.metadata.Metadata;

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

			final List<Object> cratesContainers = new ArrayList<>();

			cratesContainers.add(cargoProject.getCratesContainer());
			
			final ICargoCore cargoCore = cargoProject.getCargoCore();
			final IToolchain toolchain = cargoCore.getToolchain(cargoProject.getProject());
			if (toolchain != null) {
				cratesContainers.add(toolchain);
			}
			
			return cratesContainers.toArray();
		}

		if (parentElement instanceof ICratesContainer) {
			return ((ICratesContainer) parentElement).getCrates().toArray();
		}

		if (parentElement instanceof ICrate) {
			return ((ICrate) parentElement).getRustResources().toArray();
		}

		if (parentElement instanceof IRustFolder) {
			return ((IRustFolder) parentElement).getMembers().toArray();
		}

		return new Object[0];
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;
	}

	@Override
	public void onMetadataChanged(IProject project, Metadata oldMetadata, Metadata newMetadata) {
		viewer.getControl().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				if (viewer != null && !viewer.getControl().isDisposed()) {
					final ICargoProject cargoProject = Adapters.adapt(project, ICargoProject.class);
					if (cargoProject != null) {
						viewer.refresh(cargoProject.getCratesContainer());
					}
				}
			}
		});
	}
}
