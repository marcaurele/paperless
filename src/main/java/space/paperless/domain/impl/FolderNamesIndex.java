package space.paperless.domain.impl;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import space.paperless.domain.DescriptionIndex;
import space.paperless.domain.DescriptionType;
import space.paperless.repository.DocumentsRepository;
import space.paperless.repository.IdUtils;

public class FolderNamesIndex extends DescriptionIndex {

	private DocumentsRepository filesRepository;

	public FolderNamesIndex() {
		super();
	}

	public FolderNamesIndex(DescriptionType descriptionType, DocumentsRepository filesRepository) {
		super(descriptionType);
		this.filesRepository = filesRepository;
	}

	@Override
	@Cacheable(value = "dictionary", key = "#root.target.descriptionFieldType")
	public SortedSet<String> getElements() {
		return visitElements(filesRepository, null, new TreeSet<>());
	}

	@Override
	@CacheEvict(value = "dictionary", key = "#root.target.descriptionFieldType")
	public void add(Set<String> ids) {
		if (ids != null && !ids.isEmpty()) {
			for (String id : ids) {
				filesRepository.createFolder(id);
			}
		}
	}

	private SortedSet<String> visitElements(DocumentsRepository repository, String current,
			SortedSet<String> elements) {
		List<String> listFolders = repository.getFoldersList(current);

		for (String folder : listFolders) {
			String element = IdUtils.join('/', current, folder);

			elements.add(element);
			visitElements(repository, element, elements);
		}

		return elements;
	}
}
