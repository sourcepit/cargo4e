package org.sourcepit.cargo4e;

import org.eclipse.core.resources.IProject;
import org.sourcepit.cargo4j.model.metadata.Metadata;

public interface ICargoCore {

	void addMetadataChangedListener(IMetadataChangedListener listener);

	void removeMetadataChangedListener(IMetadataChangedListener listener);

	Metadata getMetadata(IProject project);

}