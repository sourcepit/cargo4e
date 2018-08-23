package org.sourcepit.cargo4e.ui;

import java.util.Comparator;

import org.eclipse.jface.viewers.ViewerComparator;
import org.sourcepit.cargo4e.model.IRustFile;
import org.sourcepit.cargo4e.model.IRustFolder;

public class RustNavigatorViewerComparator extends ViewerComparator {

	public RustNavigatorViewerComparator() {
		super();
	}

	public RustNavigatorViewerComparator(Comparator<? super String> comparator) {
		super(comparator);
	}

	@Override
	public int category(Object element) {
		if (element instanceof IRustFolder) {
			return 1;
		}
		if (element instanceof IRustFile) {
			return 2;
		}
		return super.category(element);
	}
}
