package space.paperless.repository;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import space.paperless.repository.IdUtils;

public class IdUtilsTest {

	@Test
	public void id_withBlanks_blanksAreIgnored() {
		String id = IdUtils.id("", "part1", "");
		assertEquals("part1", id);
	}

	@Test
	public void id_withPathSeparator_pathSeparatorTransformed() {
		String id = IdUtils.id("part1", "part2\\part3");
		assertEquals("part1:part2:part3", id);
	}

	@Test
	public void id_many_returnsJoined() {
		String id = IdUtils.id("part1", "part2", "part3");
		assertEquals("part1:part2:part3", id);
	}
}
