package org.sourcepit.cargo4e.model;

import java.util.ArrayList;
import java.util.List;

import org.sourcepit.cargo4j.model.metadata.Package;

public abstract class AbstractCratesContainer implements ICratesContainer {

	@Override
	public List<ICrate> getCrates() {
		List<ICrate> crates = new ArrayList<>();
		for (Package pkg : getPackages()) {
			crates.add(new Crate(this, pkg.getId()));
		}
		return crates;
	}

	protected abstract List<Package> getPackages();

	@Override
	public Package resolvePackage(String packageId) {
		for (Package pkg : getPackages()) {
			if (packageId.equals(pkg.getId())) {
				return pkg;
			}
		}
		return null;
	}
}
