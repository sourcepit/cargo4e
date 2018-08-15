package org.sourcepit.cargo4e.ui;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.sourcepit.cargo4e.ui.RustNavigatorContentProvider.PackagesContainer;
import org.sourcepit.cargo4j.model.Metadata;
import org.sourcepit.cargo4j.model.Package;

public class RustNaviatorLaelProvider implements ILabelProvider {

	@Override
	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public Image getImage(Object element) {
		return null;
	}

	@Override
	public String getText(Object element) {
		if (element instanceof PackagesContainer) {
			return "Packages";
		}
		if (element instanceof Package) {
			return ((Package) element).getName() + " - " + ((Package) element).getManifestPath();
		}
		return null;
	}

}
