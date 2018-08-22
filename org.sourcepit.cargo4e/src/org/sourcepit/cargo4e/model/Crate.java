package org.sourcepit.cargo4e.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.sourcepit.cargo4j.model.Package;

public class Crate implements ICrate {

	private final ICratesContainer cratesContainer;

	private final String packageId;

	public Crate(ICratesContainer cratesContainer, String packageId) {
		this.cratesContainer = cratesContainer;
		this.packageId = packageId;
	}

	protected Package getPackage() {
		return cratesContainer.resolvePackage(packageId);
	}

	@Override
	public String getName() {
		return getPackage().getName();
	}

	@Override
	public IPath getLocation() {
		return new Path(getPackage().getManifestPath()).removeLastSegments(1);
	}

	@Override
	public List<IRustResource> getRustResources() {
		List<IRustResource> resources = new ArrayList<>();
		File crateFolder = getLocation().toFile();
		if (crateFolder.exists()) {
			File[] files = crateFolder.listFiles();
			for (File file : files) {
				if (file.isFile()) {
					resources.add(new RustFile(this, file));
				} else {
					resources.add(new RustFolder(this, file));
				}
			}
		}
		return resources;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cratesContainer == null) ? 0 : cratesContainer.hashCode());
		result = prime * result + ((packageId == null) ? 0 : packageId.hashCode());
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
		Crate other = (Crate) obj;
		if (cratesContainer == null) {
			if (other.cratesContainer != null) {
				return false;
			}
		} else if (!cratesContainer.equals(other.cratesContainer)) {
			return false;
		}
		if (packageId == null) {
			if (other.packageId != null) {
				return false;
			}
		} else if (!packageId.equals(other.packageId)) {
			return false;
		}
		return true;
	}

}
