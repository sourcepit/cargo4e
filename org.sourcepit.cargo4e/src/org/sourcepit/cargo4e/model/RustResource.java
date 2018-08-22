package org.sourcepit.cargo4e.model;

import java.io.File;

public abstract class RustResource implements IRustResource {

	protected final ICrate crate;
	protected final File file;

	public RustResource(ICrate crate, File file) {
		this.crate = crate;
		this.file = file;
	}

	@Override
	public String getName() {
		return getFile().getName();
	}

	@Override
	public ICrate getCrate() {
		return crate;
	}

	@Override
	public File getFile() {
		return file;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((crate == null) ? 0 : crate.hashCode());
		result = prime * result + ((file == null) ? 0 : file.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		RustResource other = (RustResource) obj;
		if (crate == null) {
			if (other.crate != null) {
				return false;
			}
		} else if (!crate.equals(other.crate)) {
			return false;
		}
		if (file == null) {
			if (other.file != null) {
				return false;
			}
		} else if (!file.equals(other.file)) {
			return false;
		}
		return true;
	}

}
