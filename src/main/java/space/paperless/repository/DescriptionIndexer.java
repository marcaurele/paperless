package space.paperless.repository;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import space.paperless.domain.DescriptionIndex;
import space.paperless.domain.DescriptionType;
import space.paperless.domain.Document;

@Component
public class DescriptionIndexer {

	private final Map<DescriptionType, DescriptionIndex> typeToIndex = new EnumMap<>(DescriptionType.class);

	@Autowired
	public DescriptionIndexer(List<DescriptionIndex> indexes) {
		super();

		for (DescriptionIndex index : indexes) {
			typeToIndex.put(index.getDescriptionType(), index);
		}
	}

	public void index(Document document) {
		for (DescriptionType descriptionType : DescriptionType.values()) {
			if (typeToIndex.containsKey(descriptionType)) {
				Set<String> values = document.getDescriptionValues(descriptionType);

				if (values != null && !values.isEmpty()) {
					typeToIndex.get(descriptionType).add(values);
				}
			}
		}
	}

	public void index(List<Document> documents) {
		if (documents == null) {
			return;
		}

		Map<String, Set<String>> allDescriptions = new HashMap<>();

		for (Document document : documents) {
			for (Entry<String, Set<String>> entry : document.getDescriptions().entrySet()) {
				Set<String> allValues = allDescriptions.get(entry.getKey());

				if (allValues == null) {
					allValues = new HashSet<>();
					allDescriptions.put(entry.getKey(), allValues);
				}

				allValues.addAll(entry.getValue());
			}
		}

		for (DescriptionType descriptionType : DescriptionType.values()) {
			if (typeToIndex.containsKey(descriptionType)) {
				Set<String> values = allDescriptions.get(descriptionType.getName());

				if (values != null && !values.isEmpty()) {
					typeToIndex.get(descriptionType).add(values);
				}
			}
		}
	}
}
