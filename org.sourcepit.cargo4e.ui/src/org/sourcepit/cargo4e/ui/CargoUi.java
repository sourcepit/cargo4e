package org.sourcepit.cargo4e.ui;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.osgi.framework.Bundle;

public class CargoUi {
	public static final String IMG_CRATES_CONTAINER = "library_obj.png";

	public static final String IMG_CRATE = "jar_obj.png";

	private final ImageRegistry imageRegistry = newImageRegistry();

	private final Bundle bundle;

	public CargoUi(Bundle bundle) {
		this.bundle = bundle;
	}

	public Image getImage(String key) {
		Image image = imageRegistry.get(key);
		if (image == null || image.isDisposed()) {
			ImageDescriptor descriptor = getImageDescriptor(key);
			if (descriptor != null) {
				image = imageRegistry.get(key);
			}
		}
		return image;
	}

	private ImageDescriptor getImageDescriptor(String key) {
		ImageDescriptor descriptor = imageRegistry.getDescriptor(key);
		if (descriptor == null) {
			final URL url = FileLocator.find(bundle, new Path("icons/" + key), null);
			if (url != null) {
				descriptor = ImageDescriptor.createFromURL(url);
				imageRegistry.put(key, descriptor);
			}
		}
		return descriptor;
	}

	public synchronized void dispose() {
		imageRegistry.dispose();
	}
	
	private static ImageRegistry newImageRegistry() {
		final Display display = getDisplay();
		return new ImageRegistry(display);
	}

	private static Display getDisplay() {
		Display display = Display.getCurrent();
		if (display == null) {
			for (Thread thread : Thread.getAllStackTraces().keySet()) {
				if (thread.getId() == 1) {
					display = Display.findDisplay(thread);
					break;
				}
			}
		}
		return display;
	}

}
