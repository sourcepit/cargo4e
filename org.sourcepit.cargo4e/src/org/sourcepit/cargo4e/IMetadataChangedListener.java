package org.sourcepit.cargo4e;

import org.eclipse.core.resources.IProject;
import org.sourcepit.cargo4j.model.Metadata;

public interface IMetadataChangedListener {

	void onMetadataChanged(IProject project, Metadata oldMetadata, Metadata newMetadata);

}
