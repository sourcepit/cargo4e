package org.sourcepit.cargo4e;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdapterFactory;

public class CargoAdapterFactory implements IAdapterFactory {
	private final static Class<?>[] ADPTER_LIST = { ICargoProject.class };

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getAdapter(Object adaptableObject, Class<T> adapterType) {
		if (adaptableObject instanceof IProject && adapterType == ICargoProject.class) {
			final IProject project = (IProject) adaptableObject;
			try {
				return (T) project.getNature(CargoNature.NATURE_ID);
			} catch (CoreException e) {
			}
		}
		return null;
	}

	@Override
	public Class<?>[] getAdapterList() {
		return ADPTER_LIST;
	}

}
