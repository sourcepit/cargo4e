package org.sourcepit.cargo4e.ui;

import org.sourcepit.cargo4j.model.Metadata;

public class WorkspaceRootItem {

	private final Metadata metadata;

	public WorkspaceRootItem(Metadata metadata) {
		this.metadata = metadata;
	}
	
	public Metadata getMetadata() {
		return metadata;
	}

}
