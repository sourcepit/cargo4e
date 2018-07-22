package org.sourcepit.cargo4e.ui;

import org.sourcepit.cargo4j.model.Metadata;

public class PackagesItem {

	private final Metadata metadata;

	public PackagesItem(Metadata metadata) {
		this.metadata = metadata;
	}
	
	public Metadata getMetadata() {
		return metadata;
	}

}
