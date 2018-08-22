package org.sourcepit.cargo4e.model;

import java.io.File;

public interface IRustResource {

	ICrate getCrate();

	File getFile();

	String getName();
}