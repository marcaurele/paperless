package space.paperless.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import space.paperless.domain.DescriptionIndex;
import space.paperless.domain.DescriptionType;

@RestController
@ExposesResourceFor(DescriptionType.class)
public class DescriptionTypeController {

	@Autowired
	private EntityLinks entityLinks;

	@RequestMapping("/descriptionTypes")
	public List<Resource<DescriptionType>> descriptionFields() {
		List<Resource<DescriptionType>> types = new ArrayList<>(DescriptionType.values().length);

		for (DescriptionType type : DescriptionType.values()) {
			Resource<DescriptionType> resource = new Resource<DescriptionType>(type);

			if (type.isIndexed()) {
				resource.add(entityLinks.linkToSingleResource(DescriptionIndex.class, type.getName()).withRel("index"));
			}

			types.add(resource);
		}

		return types;
	}
}
