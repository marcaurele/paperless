package space.paperless.repository;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

public class IdUtils {
	private static final char ID_SEPARATOR = ':';

	private IdUtils() {
		super();
	}

	public static final String id(String... parts) {
		if (parts == null) {
			return "";
		}
		return join(ID_SEPARATOR, Stream.of(parts).map(p -> pathToId(p)).toArray(String[]::new));
	}

	public static final String path(String... parts) {
		return join(File.separatorChar, Stream.of(parts).map(p -> idToPath(p)).toArray(String[]::new));
	}

	public static final String join(char separator, String... parts) {
		StringBuilder builder = new StringBuilder();

		for (String part : parts) {
			if (StringUtils.isBlank(part)) {
				continue;
			}

			if (builder.length() > 0) {
				builder.append(separator);
			}
			builder.append(part);
		}

		return builder.toString();
	}

	public static final Set<String> augmentWithParents(Set<String> elements) {
		Set<String> augmented = new HashSet<>(elements);

		if (elements != null && !elements.isEmpty()) {
			for (String element : elements) {
				int index = -1;

				while ((index = element.indexOf('/', index + 1)) > 0) {
					augmented.add(element.substring(0, index));
				}
			}
		}

		return augmented;
	}

	private static String idToPath(String documentId) {
		return documentId != null ? documentId.replace(ID_SEPARATOR, File.separatorChar) : null;
	}

	public static final String idToPath(String repositoryId, String documentId) {
		// must be in repository
		if (StringUtils.isBlank(documentId) || !documentId.startsWith(repositoryId + ID_SEPARATOR)) {
			// TODO: throw exception
			return null;
		}

		return documentId.substring(documentId.indexOf(ID_SEPARATOR) + 1).replace(ID_SEPARATOR, File.separatorChar);
	}

	public static final String idToFileName(String documentId) {
		if (StringUtils.isBlank(documentId)) {
			return documentId;
		}

		int lastIndexOf = documentId.lastIndexOf(ID_SEPARATOR) + 1;

		if (lastIndexOf >= 0) {
			return documentId.substring(lastIndexOf);
		} else {
			return documentId;
		}
	}

	public static final String pathToId(String path) {
		return path != null ? path.replace(File.separatorChar, ID_SEPARATOR).replace('/', ID_SEPARATOR) : null;
	}
}
