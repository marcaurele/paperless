package space.paperless.domain.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import space.paperless.domain.DescriptionIndex;
import space.paperless.domain.DescriptionType;
import space.paperless.repository.IdUtils;

public class FlatFileIndex extends DescriptionIndex {

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
			e.printStackTrace();
		}

		return elements;
	}

	@Override
	public void add(Set<String> ids) {
		if (elements.addAll(IdUtils.augmentWithParents('/', ids))) {
			try {
				Files.write(file, elements);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public Path getFile() {
		return file;
	}
}
