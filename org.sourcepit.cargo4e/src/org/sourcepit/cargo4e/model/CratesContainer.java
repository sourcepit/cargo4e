package org.sourcepit.cargo4e.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.sourcepit.cargo4e.ICargoProject;
import org.sourcepit.cargo4j.model.Metadata;
import org.sourcepit.cargo4j.model.Package;

public class CratesContainer implements ICratesContainer {

	private final ICargoProject project;

	public CratesContainer(ICargoProject project) {
		this.project = project;
	}

	@Override
	public List<ICrate> getCrates() {
		List<ICrate> crates = new ArrayList<>();
		for (Package pkg : getPackages()) {
			crates.add(new Crate(this, pkg.getId()));
		}
		return crates;
	}

	private List<Package> getPackages() {
		List<Package> packages = new ArrayList<>();
		Metadata metadata = project.getMetadata();
		if (metadata != null) {
			for (Package pkg : metadata.getPackages()) {
				IPath pkgLocation = new Path(pkg.getManifestPath()).removeLastSegments(1);
				if (!project.getProject().getLocation().equals(pkgLocation)) {
					packages.add(pkg);
				}
			}
		}
		return packages;
	}

	@Override
	public Package resolvePackage(String packageId) {
		for (Package pkg : getPackages()) {
			if (packageId.equals(pkg.getId())) {
				return pkg;
			}
		}
		return null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((project == null) ? 0 : project.hashCode());
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
		CratesContainer other = (CratesContainer) obj;
		if (project == null) {
			if (other.project != null) {
				return false;
			}
		} else if (!project.equals(other.project)) {
			return false;
		}
		return true;
	}

}
