package org.sourcepit.cargo4e.model;

import java.io.File;

public class RustFile extends RustResource implements IRustFile {

	public RustFile(ICrate crate, File file) {
		super(crate, file);
	}

}
