package org.sourcepit.cargo4e.ui;

import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.sourcepit.cargo4e.ui.RustNavigatorContentProvider.PackagesContainer;
import org.sourcepit.cargo4j.model.Package;

public class RustNaviatorLaelProvider implements IStyledLabelProvider, ILabelProvider {

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
		if (element instanceof PackagesContainer) {
			return CargoUiPlugin.getCargoUi().getImage(CargoUi.IMG_PACKAGES_CONTAINER);
		}
		if (element instanceof Package) {
			return CargoUiPlugin.getCargoUi().getImage(CargoUi.IMG_PACKAGE);
		}
		return null;
	}

	@Override
	public StyledString getStyledText(Object element) {
		if (element instanceof PackagesContainer) {
			return new StyledString("Packages");
		}
		if (element instanceof Package) {

			final Package pkg = (Package) element;

			final StyledString text = new StyledString(pkg.getName());
			text.append(" - " + new Path(pkg.getManifestPath()).removeLastSegments(1), StyledString.QUALIFIER_STYLER);

			return text;
		}
		return null;
	}

	@Override
	public String getText(Object element) {
		if (element instanceof PackagesContainer) {
			return "Packages";
		}
		if (element instanceof Package) {
			final Package pkg = (Package) element;
			return pkg.getName();
		}
		return null;
	}

}
