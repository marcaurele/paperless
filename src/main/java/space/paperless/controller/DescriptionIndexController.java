package space.paperless.controller;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import space.paperless.controller.resources.IndexResource;
import space.paperless.domain.DescriptionIndex;

@RestController
@ExposesResourceFor(DescriptionIndex.class)
@RequestMapping("/indexes")
public class DescriptionIndexController {

	@Autowired
	private EntityLinks entityLinks;
	private Map<String, DescriptionIndex> typeToIndex = new HashMap<>();

	@Autowired
	public DescriptionIndexController(List<DescriptionIndex> indexes) {
		super();

		for (DescriptionIndex index : indexes) {
			typeToIndex.put(index.getDescriptionType().getName(), index);
		}
	}

	@RequestMapping
	public List<IndexResource> getIndexes() {
		List<IndexResource> indexes = new LinkedList<>();

		for (DescriptionIndex index : typeToIndex.values()) {
			indexes.add(getIndexResource(index));
		}

		return indexes;
	}

	@RequestMapping("/{type}")
	public ResponseEntity<IndexResource> getIndex(@PathVariable String type) {
		DescriptionIndex index = typeToIndex.get(type);

		if (index == null) {
			return ResponseEntity.notFound().build();
		}

		return new ResponseEntity<>(getIndexResource(index), HttpStatus.OK);
	}

	private IndexResource getIndexResource(DescriptionIndex index) {
		IndexResource resource = new IndexResource(index);

		resource.add(entityLinks.linkToSingleResource(DescriptionIndex.class, index.getDescriptionType().getName()));

		return resource;
	}
}
