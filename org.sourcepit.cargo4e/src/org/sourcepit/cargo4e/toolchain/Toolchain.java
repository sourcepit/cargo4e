package org.sourcepit.cargo4e.toolchain;

import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.sourcepit.cargo4e.model.AbstractCratesContainer;
import org.sourcepit.cargo4e.model.ICratesContainer;
import org.sourcepit.cargo4j.model.metadata.Package;
import org.sourcepit.cargo4j.model.toolchain.ToolchainIdentifier;

public class Toolchain extends AbstractCratesContainer implements ICratesContainer, IToolchain {

	private final ToolchainIdentifier id;

	private final IPath sysroot;

	private final List<Package> packages;

	public Toolchain(ToolchainIdentifier id, IPath sysroot, List<Package> packages) {
		this.id = id;
		this.sysroot = sysroot;
		this.packages = packages;
	}

	@Override
	public ToolchainIdentifier getId() {
		return id;
	}

	@Override
	public IPath getSysroot() {
		return sysroot;
	}

	@Override
	protected List<Package> getPackages() {
		return packages;
	}
}
