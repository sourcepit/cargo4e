package org.sourcepit.cargo4e.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.sourcepit.cargo4j.exec.CargoMetadataCommand;
import org.sourcepit.cargo4j.model.Metadata;
import org.sourcepit.cargo4j.model.Resolve;

public class CargoTreeContentProvider implements ITreeContentProvider {

	private IFile cargoFile;
	private Metadata metadata;

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (!equals(cargoFile, newInput)) {
			if (newInput == null) {
				setCargoFile(viewer, (IFile) newInput);
			} else if (newInput instanceof IFile) {
				final IFile file = (IFile) newInput;
				if (file.getName().equals("Cargo.toml")) {
					setCargoFile(viewer, file);
				}
			}
		}
	}

	private void setCargoFile(Viewer viewer, IFile newCargoFile) {

		this.cargoFile = newCargoFile;
		this.metadata = null;

		final Job job = new Job("Cargo Metadata") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				if (newCargoFile.isAccessible()) {
					try {
						final File cargoProject = newCargoFile.getParent().getLocation().toFile();
						final Metadata metadata = new CargoMetadataCommand().execute(cargoProject);
						viewer.getControl().getDisplay().asyncExec(new Runnable() {
							public void run() {
								if (!viewer.getControl().isDisposed()) {
									setMetadata(viewer, newCargoFile, metadata);
								}
							}
						});
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				return null;
			}
		};
		job.setRule(newCargoFile);
		job.schedule();
	}

	private void setMetadata(Viewer viewer, IFile cargoFile, Metadata metadata) {
		if (equals(this.cargoFile, cargoFile)) {
			this.metadata = metadata;
			viewer.refresh();
		}
	}

	public static boolean equals(Object o1, Object o2) {
		if (o1 == null) {
			return o2 == null;
		}
		return o1.equals(o2);
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if (metadata != null) {
			final List<Object> elements = new ArrayList<>();
			elements.add(new PackagesItem(metadata));
			elements.add(new ResolveItem(metadata));
			elements.add(new TargetDirectoryItem(metadata));
			elements.add(new VersionItem(metadata));
			elements.add(new WorkspaceMembersItem(metadata));
			elements.add(new WorkspaceRootItem(metadata));
			return elements.toArray();
		}
		return new Object[0];
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof PackagesItem) {
			return ((PackagesItem) parentElement).getMetadata().getPackages().toArray();
		}
		if (parentElement instanceof ResolveItem) {
			Optional<Resolve> resolve = ((ResolveItem) parentElement).getMetadata().getResolve();
			return resolve.isPresent() ? resolve.get().getNodes().toArray() : null;
		}
		return new Object[0];
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		Object[] children = getChildren(element);
		return children != null && children.length > 0;
	}

}
