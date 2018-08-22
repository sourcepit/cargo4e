package org.sourcepit.cargo4e.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RustFolder extends RustResource implements IRustFolder {

	public RustFolder(ICrate crate, File file) {
		super(crate, file);
	}

	@Override
	public List<IRustResource> getMembers() {
		List<IRustResource> resources = new ArrayList<>();
		if (this.file.exists()) {
			File[] files = file.listFiles();
			for (File file : files) {
				if (file.isFile()) {
					resources.add(new RustFile(this.crate, file));
				} else {
					resources.add(new RustFolder(this.crate, file));
				}
			}
		}
		return resources;
	}

}
