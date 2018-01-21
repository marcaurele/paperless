package space.paperless.domain.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import space.paperless.domain.DescriptionIndex;
import space.paperless.domain.DescriptionType;
import space.paperless.repository.IdUtils;

public class FlatFileIndex extends DescriptionIndex {

	private static final Logger LOG = LoggerFactory.getLogger(FlatFileIndex.class);

	private Path file;
	private SortedSet<String> elements = new TreeSet<>();

	public FlatFileIndex() {
		super();
	}

	public FlatFileIndex(DescriptionType descriptionType, File root) {
		super(descriptionType);
		this.file = new File(root, descriptionType.name() + ".idx").toPath();
	}

	@Override
	public SortedSet<String> getElements() {
		if (elements == null) {
			elements = new TreeSet<>();
		} else {
			elements.clear();
		}

		try {
			elements.addAll(Files.readAllLines(file));
		} catch (IOException e) {
			LOG.error("Unable to read file {}", file);
		}

		return elements;
	}

	@Override
	public void add(Set<String> ids) {
		if (elements.addAll(IdUtils.augmentWithParents(ids))) {
			try {
				Files.write(file, elements);
			} catch (IOException e) {
				LOG.error("Unable to write to file {}", file);
			}
		}
	}

	public Path getFile() {
		return file;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((elements == null) ? 0 : elements.hashCode());
		result = prime * result + ((file == null) ? 0 : file.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		FlatFileIndex other = (FlatFileIndex) obj;
		if (elements == null) {
			if (other.elements != null)
				return false;
		} else if (!elements.equals(other.elements))
			return false;
		if (file == null) {
			if (other.file != null)
				return false;
		} else if (!file.equals(other.file))
			return false;
		return true;
	}
}
