package org.sourcepit.cargo4e;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ICoreRunnable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.sourcepit.cargo4e.toolchain.IToolchain;
import org.sourcepit.cargo4e.toolchain.ToolchainManager;
import org.sourcepit.cargo4j.exec.GetRustupExecutableCommand;
import org.sourcepit.cargo4j.model.metadata.Metadata;
import org.sourcepit.cargo4j.model.metadata.Package;
import org.sourcepit.cargo4j.model.toolchain.ToolchainIdentifier;

public class CargoCore implements IResourceChangeListener, ICargoCore, IMetadataChangedListener {

	private final IWorkspace eclipseWorkspace;

	private final IJobManager jobManager;

	private final File stateLocation;

	private final List<IMetadataChangedListener> listeners = new CopyOnWriteArrayList<>();

	private File workingDirectory;

	private File rustupExecutable;

	private ToolchainIdentifier toolchain;

	private ToolchainManager toolchainManager;

	private MetadataStore metadataStore;

	private boolean running = false;

	public CargoCore(IWorkspace eclipseWorkspace, IJobManager jobManager, File stateLocation) {
		this.eclipseWorkspace = eclipseWorkspace;
		this.jobManager = jobManager;
		this.stateLocation = stateLocation;
	}

	public synchronized void start() {
		if (running) {
			throw new IllegalStateException();
		}
		running = true;

		workingDirectory = new File(System.getProperty("user.dir"));

		try {
			rustupExecutable = new GetRustupExecutableCommand(workingDirectory).execute();

			toolchain = ToolchainIdentifier.parse("stable");

			toolchainManager = new ToolchainManager(workingDirectory, rustupExecutable, toolchain);
			toolchainManager.start();

			metadataStore = new MetadataStore(stateLocation) {
				@Override
				protected void noifyMetadataChanged(IProject project, Metadata oldMetadata, Metadata newMetadata) {
					for (IMetadataChangedListener listener : listeners) {
						listener.onMetadataChanged(project, oldMetadata, newMetadata);
					}
				}
			};

			addMetadataChangedListener(this);

			final InitializeCargoProjectsRunnable initRunnable = new InitializeCargoProjectsRunnable(eclipseWorkspace,
					jobManager, metadataStore, rustupExecutable, toolchain);

			final CargoCoreJob initJob = new CargoCoreJob("Initialize Cargo Projects", initRunnable);
			initJob.setSystem(true);
			initJob.schedule();

			eclipseWorkspace.addResourceChangeListener(this,
					IResourceChangeEvent.POST_CHANGE | IResourceChangeEvent.PRE_DELETE);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void addMetadataChangedListener(IMetadataChangedListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeMetadataChangedListener(IMetadataChangedListener listener) {
		listeners.remove(listener);
	}

	public synchronized void stop() {
		if (!running) {
			throw new IllegalStateException();
		}
		running = false;

		jobManager.cancel(CargoCoreJob.FAMILY);

		this.eclipseWorkspace.removeResourceChangeListener(this);

		removeMetadataChangedListener(this);

		metadataStore = null;

		toolchainManager.stop();
		toolchainManager = null;

		toolchain = null;

		rustupExecutable = null;

		workingDirectory = null;
	}

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		switch (event.getType()) {
		case IResourceChangeEvent.PRE_DELETE:
			final IProject project = (IProject) event.getResource();
			if (metadataStore.getMetadata(project) != null) {
				metadataStore.setMetadata(project, null);
			}
			break;
		case IResourceChangeEvent.POST_CHANGE:

			final List<IProject> changedProjects = new ArrayList<>();

			final IResourceDeltaVisitor resourceDeltaVisitor = new IResourceDeltaVisitor() {
				@Override
				public boolean visit(IResourceDelta delta) throws CoreException {

					final IResource resource = delta.getResource();
					if (resource instanceof IWorkspaceRoot) {
						return true;
					}
					if (resource instanceof IProject) {
						final IProject project = (IProject) resource;
						return project.isOpen() && project.hasNature(ICargoProject.NATURE_ID);
					}
					if (resource instanceof IFile && "Cargo.toml".equals(resource.getName())) {
						switch (delta.getKind()) {
						case IResourceDelta.ADDED:
						case IResourceDelta.CHANGED:
						case IResourceDelta.REMOVED:
							changedProjects.add(resource.getProject());
							break;
						default:
							break;
						}
					}
					return false;
				}
			};

			try {
				event.getDelta().accept(resourceDeltaVisitor);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (!changedProjects.isEmpty()) {
				final List<CargoCoreJob> updateJobs = new ArrayList<>();
				for (IProject changedProject : changedProjects) {
					updateJobs.add(CargoCoreJob.newUpdateMetadataJob(metadataStore, changedProject, rustupExecutable,
							toolchain));
				}

				final CargoCoreJob updateJob = new CargoCoreJob("Update Cargo Projects", new ICoreRunnable() {
					@Override
					public void run(IProgressMonitor monitor) throws CoreException {
						CargoCoreJob.runJobsInProgressGroup(jobManager, "Updating Cargo Projects", updateJobs);
					}
				});
				updateJob.setSystem(true);
				updateJob.schedule();
			}

			break;
		default:
			throw new IllegalStateException();
		}
	}

	@Override
	public Metadata getMetadata(IProject eclipseProject) {
		return hasCargoNature(eclipseProject) ? metadataStore.getMetadata(eclipseProject) : null;
	}

	@Override
	public IToolchain getToolchain(IProject project) {
		return hasCargoNature(project) ? toolchainManager.getToolchain(project) : null;
	}

	static boolean hasCargoNature(IProject project) {
		try {
			return project.hasNature(ICargoProject.NATURE_ID);
		} catch (CoreException e) {
			return false;
		}
	}

	@Override
	public void onMetadataChanged(IProject project, Metadata oldMetadata, Metadata newMetadata) {

		try {
			final IFolder depsFolder = project.getFolder("Cargo Dependencies");
			if (!depsFolder.exists()) {
				depsFolder.create(IResource.VIRTUAL, true, null);
				depsFolder.setDerived(true, null);
			}

			final Map<String, IFolder> nameToFolderMap = new HashMap<>();
			for (IResource member : depsFolder.members()) {
				if (member.getType() == IResource.FOLDER) {
					nameToFolderMap.put(member.getName(), (IFolder) member);
				}
			}

			for (Package crate : newMetadata.getPackages()) {
				IPath location = new Path(crate.getManifestPath()).removeLastSegments(1);
				if (!project.getLocation().equals(location)) {
					String name = crate.getName();
					nameToFolderMap.remove(name);
					IFolder depFolder = depsFolder.getFolder(name);
					depFolder.createLink(location, IResource.REPLACE | IResource.ALLOW_MISSING_LOCAL, null);
					depFolder.setDerived(true, null);
				}
			}

			for (IFolder folder : nameToFolderMap.values()) {
				folder.delete(true, null);
			}

			depsFolder.refreshLocal(IResource.DEPTH_INFINITE, null);

		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
