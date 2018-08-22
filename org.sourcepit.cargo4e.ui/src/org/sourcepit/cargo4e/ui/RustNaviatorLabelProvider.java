package org.sourcepit.cargo4e.ui;

import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.sourcepit.cargo4e.model.ICrate;
import org.sourcepit.cargo4e.model.ICratesContainer;
import org.sourcepit.cargo4e.model.IRustFile;
import org.sourcepit.cargo4e.model.IRustFolder;
import org.sourcepit.cargo4e.model.IRustResource;

public class RustNaviatorLabelProvider implements IStyledLabelProvider, ILabelProvider {

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
		if (element instanceof ICratesContainer) {
			return CargoUiPlugin.getCargoUi().getImage(CargoUi.IMG_CRATES_CONTAINER);
		}
		if (element instanceof ICrate) {
			return CargoUiPlugin.getCargoUi().getImage(CargoUi.IMG_CRATE);
		}
		if (element instanceof IRustFile) {
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE);
		}
		if (element instanceof IRustFolder) {
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);
		}
		return null;
	}

	@Override
	public StyledString getStyledText(Object element) {
		if (element instanceof ICratesContainer) {
			return new StyledString("Cargo Dependencies");
		}
		if (element instanceof ICrate) {

			final ICrate crate = (ICrate) element;

			final StyledString text = new StyledString(crate.getName());
			text.append(" - " + crate.getLocation(), StyledString.QUALIFIER_STYLER);

			return text;
		}
		if (element instanceof IRustResource) {
			return new StyledString(((IRustResource) element).getName());
		}

		return null;
	}

	@Override
	public String getText(Object element) {
		if (element instanceof ICrate) {
			final ICrate crate = (ICrate) element;
			return crate.getName();
		}

		StyledString styledText = getStyledText(element);
		return styledText != null ? styledText.getString() : null;
	}

}
