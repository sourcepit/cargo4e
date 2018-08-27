package org.sourcepit.cargo4e.model;

import java.util.List;

import org.sourcepit.cargo4j.model.metadata.Package;

public interface ICratesContainer {

	List<ICrate> getCrates();

	Package resolvePackage(String packageId);
}
