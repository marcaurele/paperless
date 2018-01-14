package space.paperless.domain.impl;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.After;
import org.junit.Test;

import space.paperless.domain.DescriptionType;
import space.paperless.domain.impl.FlatFileIndex;

/**
 * Created by vince on 25.01.2017.
 */
public class FlatFileIndexTest {

	private static final File ROOT = new File(new File("").getAbsolutePath(), "src/test/resources/resources");

	private FlatFileIndex index;

	@Test
	public void create_assignmentsAreCorrect() throws Exception {
		// when
		index = new FlatFileIndex(DescriptionType.REFERENCE, ROOT);

		// then
		assertEquals(DescriptionType.REFERENCE, index.getDescriptionType());
	}

	@Test
	public void update_addTwoRootElement_TwoRootElementsAreAdded() throws Exception {
		// given
		index = new FlatFileIndex(DescriptionType.REFERENCE, ROOT);
		Set<String> ids = new HashSet<>();
		String element1 = "toto1" + System.currentTimeMillis();
		String element2 = "toto2" + System.currentTimeMillis();

		// when
		ids.add(element1);
		ids.add(element2);
		index.add(ids);
		Set<String> elements = index.getElements();

		// then
		assertEquals(2, elements.size());
		assertThat(elements, hasItems(element1, element1));
	}

	@Test
	public void update_addComplexElement_complexElementIsAdded() throws Exception {
		// given
		index = new FlatFileIndex(DescriptionType.REFERENCE, ROOT);
		Set<String> ids = new HashSet<>();
		String root = "toto";
		String child = String.valueOf(System.currentTimeMillis());
		String element = root + '/' + child;

		// when
		ids.add(element);
		index.add(ids);
		Set<String> elements = index.getElements();

		// then
		assertEquals(2, elements.size());
		assertThat(elements, hasItems(root, element));
	}

	@Test
	public void update_addTwoComplexElement_twoComplexElementsAreAdded() throws Exception {
		// given
		index = new FlatFileIndex(DescriptionType.REFERENCE, ROOT);
		Set<String> ids = new HashSet<>();
		String root = "toto";
		String child1 = 1 + String.valueOf(System.currentTimeMillis());
		String child2 = 2 + String.valueOf(System.currentTimeMillis());
		String element1 = root + '/' + child1;
		String element2 = root + '/' + child2;

		// when
		ids.add(element1);
		ids.add(element2);
		index.add(ids);
		Set<String> elements = index.getElements();

		// then
		assertEquals(3, elements.size());
		assertThat(elements, hasItems(root, element1, element2));
	}

	@Test
	public void update_addTwice_indexFileIsTruncated() throws Exception {
		// given
		index = new FlatFileIndex(DescriptionType.REFERENCE, ROOT);
		String element1 = "element1";
		String element2 = "element2";

		// when
		index.add(Stream.of(element1).collect(Collectors.toSet()));
		index.add(Stream.of(element2).collect(Collectors.toSet()));
		Set<String> elements = index.getElements();

		// then
		assertEquals(2, elements.size());
		assertThat(elements, hasItems(element1, element2));
	}

	@After
	public void deleteIndex() throws IOException {
		Files.deleteIfExists(index.getFile());
	}
}