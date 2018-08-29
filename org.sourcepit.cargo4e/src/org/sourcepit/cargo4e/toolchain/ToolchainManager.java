package org.sourcepit.cargo4e.toolchain;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Path;
import org.sourcepit.cargo4j.exec.CargoMetadataCommand;
import org.sourcepit.cargo4j.exec.GetSysrootCommand;
import org.sourcepit.cargo4j.exec.ListToolchainsCommand;
import org.sourcepit.cargo4j.model.metadata.Metadata;
import org.sourcepit.cargo4j.model.metadata.Package;
import org.sourcepit.cargo4j.model.toolchain.ToolchainIdentifier;

public class ToolchainManager {

	private final File workingDirectory;

	private final File rustupExecutable;

	private final ToolchainIdentifier toolchain;

	public ToolchainManager(File workingDirectory, File rustupExecutable, ToolchainIdentifier toolchain) {
		this.workingDirectory = workingDirectory;
		this.rustupExecutable = rustupExecutable;
		this.toolchain = toolchain;
	}

	private List<IToolchain> toolchains;

	public synchronized void start() {

		try {
			final ListToolchainsCommand listToolchainsCmd = new ListToolchainsCommand(workingDirectory,
					rustupExecutable);
			final List<ToolchainIdentifier> toolchainIds = listToolchainsCmd.execute();

			List<IToolchain> toolchains = new ArrayList<IToolchain>();

			for (ToolchainIdentifier toolchainId : toolchainIds) {
				final File sysroot = new GetSysrootCommand(workingDirectory, rustupExecutable, toolchainId).execute();

				final File rustSrcDir = new File(sysroot, "lib/rustlib/src/rust/src");
				if (rustSrcDir.exists()) {
					File[] libSrcDirs = rustSrcDir.listFiles(new FileFilter() {
						@Override
						public boolean accept(File member) {
							return member.isDirectory() && new File(member, "Cargo.toml").exists();
						}
					});

					List<Package> packages = new ArrayList<>();
					for (File libSrcDir : libSrcDirs) {
						Metadata metadata = new CargoMetadataCommand(libSrcDir, rustupExecutable, toolchain, false)
								.execute();

						Package pkg = metadata.getPackages().get(0);
						packages.add(pkg);
					}

					toolchains.add(new Toolchain(toolchainId, new Path(sysroot.getAbsolutePath()), packages));
				}
			}

			this.toolchains = toolchains;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public synchronized void stop() {
		toolchains = null;
	}

	public IToolchain getToolchain(IProject project) {
		return toolchains.isEmpty() ? null : toolchains.get(0);
	}
}
