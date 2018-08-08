package org.sourcepit.cargo4e;

import org.eclipse.core.resources.IProject;
import org.sourcepit.cargo4j.model.Metadata;

public interface ICargoProject {

	String NATURE_ID = CargoCorePlugin.BUNDLE_ID + ".CargoNature";

	IProject getProject();

	Metadata getMetadata();

}