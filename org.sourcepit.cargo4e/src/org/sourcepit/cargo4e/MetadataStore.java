package org.sourcepit.cargo4e;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.eclipse.core.resources.IProject;
import org.sourcepit.cargo4j.model.Metadata;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

public class MetadataStore {

	private final Map<IProject, Metadata> projectToMetadataMap = new HashMap<>();

	private final File stateLocation;

	private final ObjectMapper mapper;

	public MetadataStore(File stateLocation) {
		this.stateLocation = stateLocation;
		mapper = new ObjectMapper();
		mapper.registerModule(new Jdk8Module());
	}

	public void setMetadata(IProject eclipseProject, Metadata metadata) {
		final Metadata oldMetadata = getMetadata(eclipseProject);
		if (!ObjectUtils.equals(metadata, oldMetadata)) {
			synchronized (projectToMetadataMap) {
				final File projectStateFile = new File(stateLocation, eclipseProject.getName() + ".json");
				if (metadata == null) {
					projectStateFile.delete();
					projectToMetadataMap.remove(eclipseProject);
				} else {
					try (FileOutputStream out = new FileOutputStream(projectStateFile)) {
						mapper.writeValue(out, metadata);
					} catch (IOException e) {
						throw new IllegalStateException(e);
					}
					projectToMetadataMap.put(eclipseProject, metadata);
				}
			}
		}
	}

	public Metadata getMetadata(IProject eclipseProject) {
		synchronized (projectToMetadataMap) {
			Metadata metadata = projectToMetadataMap.get(eclipseProject);
			if (metadata == null) {
				final File projectStateFile = new File(stateLocation, eclipseProject.getName() + ".json");
				try (InputStream in = new FileInputStream(projectStateFile)) {
					metadata = mapper.readValue(in, Metadata.class);
				} catch (Exception e) {
					metadata = null;
				}
				projectToMetadataMap.put(eclipseProject, metadata);
			}
			return metadata;
		}
	}
}
