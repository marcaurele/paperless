package space.paperless.domain.impl;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Test;

import space.paperless.domain.DescriptionType;
import space.paperless.domain.RepositoryId;
import space.paperless.domain.impl.FolderNamesIndex;
import space.paperless.repository.DocumentsRepository;

/**
 * Created by vince on 22.01.2017.
 */
public class FolderNamesIndexTest {

	private static final File ROOT = new File("src/test/resources/archive");

	@After
	public void tearDown() throws Exception {
		File subType1 = new File(ROOT, "type2/subtype1");

		if (subType1.exists()) {
			subType1.delete();
		}
	}

	@Test
	public void create_assignementsAreCorrect() throws Exception {
		// when
		FolderNamesIndex folderNamesDictionary = new FolderNamesIndex(DescriptionType.TYPE,
				new DocumentsRepository(RepositoryId.ARCHIVE, ROOT, null, null));

		// then
		assertEquals(DescriptionType.TYPE, folderNamesDictionary.getDescriptionType());
	}

	@Test
	public void create_containsElements() throws Exception {
		// given
		FolderNamesIndex folderNamesDictionary = new FolderNamesIndex(DescriptionType.TYPE,
				new DocumentsRepository(RepositoryId.ARCHIVE, ROOT, null, null));

		// when
		Set<String> elements = folderNamesDictionary.getElements();

		// then
		assertThat(elements, hasItems("type1", "type1/subtype1", "type2"));
	}

	@Test
	public void update_absentElement_createFolder() throws Exception {
		// given
		FolderNamesIndex folderNamesDictionary = new FolderNamesIndex(DescriptionType.TYPE,
				new DocumentsRepository(RepositoryId.ARCHIVE, ROOT, null, null));
		Set<String> elements = new HashSet<>();

		elements.add("type2/subtype1");

		// when
		folderNamesDictionary.add(elements);
		Set<String> elementsAfterUpdate = folderNamesDictionary.getElements();

		// then
		assertThat(elementsAfterUpdate, hasItems("type1", "type1/subtype1", "type2", "type2/subtype1"));
	}
}