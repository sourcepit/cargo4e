package org.sourcepit.cargo4e.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ICoreRunnable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.sourcepit.cargo4e.CargoCoreJob;
import org.sourcepit.cargo4e.CargoCorePlugin;
import org.sourcepit.cargo4e.ICargoProject;
import org.sourcepit.cargo4e.NewRustProjectCoreRunnable;
import org.sourcepit.cargo4j.model.Metadata;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;

public class CargoCoreTest {

	@Rule
	public TestName testName = new TestName();

	@BeforeClass
	public static void setUp() throws CoreException {

		final ICoreRunnable deleteAllProjectsRunnable = new ICoreRunnable() {
			@Override
			public void run(IProgressMonitor monitor) throws CoreException {
				final IWorkspace workspace = ResourcesPlugin.getWorkspace();
				for (IProject project : workspace.getRoot().getProjects()) {
					project.close(null);
				}
				workspace.getRoot().delete(true, null);
			}
		};

		runInWs(deleteAllProjectsRunnable);
	}

	@Test
	public void testCreateProject() throws Exception {

		String projectName = testName.getMethodName();

		IProject project = createCargoProject(projectName);
		assertTrue(project.exists());

		ICargoProject cargoProject = (ICargoProject) project.getNature(ICargoProject.NATURE_ID);
		assertNotNull(cargoProject);

		Job.getJobManager().join(CargoCoreJob.FAMILY, null);

		Metadata metadata = cargoProject.getMetadata();
		assertNotNull(metadata);
	}

	@Test
	public void addDependency() throws Exception {
		String projectName = testName.getMethodName();

		IProject project = createCargoProject(projectName);
		assertTrue(project.exists());

		ICargoProject cargoProject = (ICargoProject) project.getNature(ICargoProject.NATURE_ID);
		assertNotNull(cargoProject);

		Job.getJobManager().join(CargoCoreJob.FAMILY, null);

		Metadata metadata = cargoProject.getMetadata();
		assertNotNull(metadata);
		assertEquals(1, metadata.getPackages().size());

		addDependency(project, "libc", "0.2.43");

		Job.getJobManager().join(CargoCoreJob.FAMILY, null);

		Metadata newMetadata = cargoProject.getMetadata();
		assertEquals(1, metadata.getPackages().size());
		assertEquals(2, newMetadata.getPackages().size());
	}

	@Test
	public void testCloseDeleteProjet() throws Exception {
		File stateLocation = Platform.getStateLocation(Platform.getBundle(CargoCorePlugin.BUNDLE_ID)).toFile();
		assertTrue(stateLocation.exists());

		String projectName = testName.getMethodName();

		File projectStateFile = new File(stateLocation, projectName + ".json");
		assertFalse(projectStateFile.exists());

		IProject project = createCargoProject(projectName);
		assertTrue(project.exists());

		Job.getJobManager().join(CargoCoreJob.FAMILY, null);

		assertTrue(projectStateFile.exists());

		project.close(null);

		Job.getJobManager().join(CargoCoreJob.FAMILY, null);

		assertTrue(projectStateFile.exists());

		project.delete(true, null);

		Job.getJobManager().join(CargoCoreJob.FAMILY, null);

		assertFalse(projectStateFile.exists());
	}

	private static void addDependency(IProject project, String name, String version) throws CoreException {

		final IFile tomlFile = project.getFile("Cargo.toml");

		final ICoreRunnable addDependencyRunnable = new ICoreRunnable() {

			@Override
			public void run(IProgressMonitor monitor) throws CoreException {
				final Map<String, Object> cargoToml;
				try (InputStream contents = tomlFile.getContents()) {
					cargoToml = new Toml().read(contents).toMap();
				} catch (IOException e) {
					throw new CoreException(new Status(IStatus.ERROR, CargoCorePlugin.BUNDLE_ID,
							"Failed to read " + tomlFile.getLocation(), e));
				}

				@SuppressWarnings("unchecked")
				Map<String, Object> dependenciesMap = (Map<String, Object>) cargoToml.get("dependencies");
				dependenciesMap.put(name, version);

				ByteArrayOutputStream bytes = new ByteArrayOutputStream();
				try {
					new TomlWriter().write(cargoToml, bytes);
				} catch (IOException e) {
					throw new CoreException(new Status(IStatus.ERROR, CargoCorePlugin.BUNDLE_ID,
							"Failed to save " + tomlFile.getLocation(), e));
				}

				tomlFile.setContents(new ByteArrayInputStream(bytes.toByteArray()), false, false, null);
			}
		};

		runInWs(addDependencyRunnable, ResourcesPlugin.getWorkspace().getRuleFactory().modifyRule(tomlFile));
	}

	private static IProject createCargoProject(String projectName) throws CoreException {
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();

		final IProject project = workspace.getRoot().getProject(projectName);

		final ICoreRunnable newProjectRunnable = new NewRustProjectCoreRunnable(workspace, projectName);

		runInWs(newProjectRunnable, project);

		return project;
	}

	private static void runInWs(ICoreRunnable runnable) throws CoreException {
		runInWs(runnable, ResourcesPlugin.getWorkspace().getRoot());
	}

	private static void runInWs(ICoreRunnable runnable, ISchedulingRule schedulingRule) throws CoreException {
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		workspace.run(runnable, schedulingRule, IWorkspace.AVOID_UPDATE, null);
	}

}
