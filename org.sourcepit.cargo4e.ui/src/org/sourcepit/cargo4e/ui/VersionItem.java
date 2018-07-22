package org.sourcepit.cargo4e.ui;

import org.sourcepit.cargo4j.model.Metadata;

public class VersionItem {

	private final Metadata metadata;

	public VersionItem(Metadata metadata) {
		this.metadata = metadata;
	}
	
	public Metadata getMetadata() {
		return metadata;
	}

}
