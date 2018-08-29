package org.sourcepit.cargo4e.toolchain;

import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.sourcepit.cargo4e.model.ICrate;
import org.sourcepit.cargo4j.model.toolchain.ToolchainIdentifier;

public interface IToolchain {

	ToolchainIdentifier getId();

	IPath getSysroot();

	List<ICrate> getCrates();

}