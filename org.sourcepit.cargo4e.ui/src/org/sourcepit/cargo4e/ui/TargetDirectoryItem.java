package org.sourcepit.cargo4e.ui;

import org.sourcepit.cargo4j.model.Metadata;

public class TargetDirectoryItem {

	private final Metadata metadata;

	public TargetDirectoryItem(Metadata metadata) {
		this.metadata = metadata;
	}
	
	public Metadata getMetadata() {
		return metadata;
	}

}
