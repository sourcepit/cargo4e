package org.sourcepit.cargo4e.ui;

import org.sourcepit.cargo4j.model.Metadata;

public class ResolveItem {

	private final Metadata metadata;

	public ResolveItem(Metadata metadata) {
		this.metadata = metadata;
	}
	
	public Metadata getMetadata() {
		return metadata;
	}

}
