package org.sourcepit.cargo4e.model;

import java.util.List;

public interface IRustFolder extends IRustResource {
	List<IRustResource> getMembers();
}
