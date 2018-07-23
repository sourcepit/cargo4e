package org.sourcepit.cargo4e.ui.wizards;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ICoreRunnable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.sourcepit.cargo4e.RustNature;
import org.sourcepit.cargo4e.ui.Activator;
import org.sourcepit.cargo4j.exec.CargoNewProjectCommand;

public class NewRustProjectWizard extends Wizard implements INewWizard {
	private NewRustProjectWizardPage page;

	public NewRustProjectWizard() {
		super();
		setNeedsProgressMonitor(true);
	}

	@Override
	public void addPages() {
		page = new NewRustProjectWizardPage();
		addPage(page);
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

	@Override
	public boolean performFinish() {
		final String projectName = page.getProjectName();
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				try {
					doFinish(projectName, monitor);
				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				} finally {
					monitor.done();
				}
			}
		};
		try {
			getContainer().run(true, false, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error", realException.getMessage());
			return false;
		}
		return true;
	}

	private void doFinish(String projectName, IProgressMonitor pm) throws CoreException {

		final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		final IProject project = root.getProject(projectName);

		ResourcesPlugin.getWorkspace().run(new ICoreRunnable() {
			@Override
			public void run(IProgressMonitor pm) throws CoreException {
				SubMonitor monitor = SubMonitor.convert(pm, "Creating " + projectName, 100);

				IProjectDescription description = ResourcesPlugin.getWorkspace().newProjectDescription(projectName);

				String[] natures = description.getNatureIds();
				String[] newNatures = new String[natures.length + 1];
				System.arraycopy(natures, 0, newNatures, 0, natures.length);
				newNatures[natures.length] = RustNature.NATURE_ID;
				description.setNatureIds(newNatures);

				File projectFile = new File(ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile(),
						projectName);
				try {
					new CargoNewProjectCommand().execute(projectFile);
				} catch (IOException e) {
					throw new CoreException(
							new Status(IStatus.ERROR, Activator.BUNDLE_ID, "Failed to create Rust project", e));
				}
				monitor.split(25).done();
				project.create(description, monitor.split(25));
				project.open(monitor.split(50));
			}
		}, project, 0, pm);

	}
}