package org.sourcepit.cargo4e.model;

import java.util.List;

import org.eclipse.core.runtime.IPath;

public interface ICrate {
	List<IRustResource> getRustResources();

	String getName();

	IPath getLocation();

	String getVersion();
}
