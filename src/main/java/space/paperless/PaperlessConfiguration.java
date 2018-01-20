package space.paperless;

import java.io.File;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import space.paperless.domain.DescriptionType;
import space.paperless.domain.RepositoryId;
import space.paperless.domain.impl.FlatFileIndex;
import space.paperless.domain.impl.FolderNamesIndex;
import space.paperless.pdfmeta.PDFMetadata;
import space.paperless.pdfmeta.PDFMetadataPDFBox;
import space.paperless.repository.DocumentsRepository;
import space.paperless.repository.RepositoryIndex;

@Configuration
public class PaperlessConfiguration {

	@Bean
	public CacheManager cacheManager() {
		SimpleCacheManager cacheManager = new SimpleCacheManager();
		cacheManager.setCaches(Collections.singletonList(new ConcurrentMapCache("dictionary")));
		return cacheManager;
	}

	@Bean("archiveRoot")
	public File archiveRoot(@Value("${root.archive}") String root) {
		return new File(root);
	}

	@Bean("incomingRoot")
	public File incomingRoot(@Value("${root.incoming}") String root) {
		return new File(root);
	}

	@Bean(name = "scannerTool")
	public File scannerTool(@Value("${tool.naps2}") String tool) {
		return new File(tool);
	}

	@Bean(name = "pdfMetadata")
	public PDFMetadata PDFMetadata() {
		return new PDFMetadataPDFBox();
	}

	@Bean("dictionariesRoot")
	public File dictionariesRoot(@Value("${root.dictionaries}") String root) {
		return new File(root);
	}

	@Bean("indexRoot")
	public File indexRoot(@Value("${root.index}") String root) {
		return new File(root);
	}

	@Bean("archiveIndex")
	public RepositoryIndex archiveIndex(@Qualifier("indexRoot") File indexRoot) {
		return new RepositoryIndex(RepositoryId.ARCHIVE, indexRoot);
	}

	@Bean("incomingIndex")
	public RepositoryIndex incomingIndex(@Qualifier("indexRoot") File indexRoot) {
		return new RepositoryIndex(RepositoryId.INCOMING, indexRoot);
	}

	@Bean("archive")
	public DocumentsRepository archive(@Qualifier("archiveRoot") File root, PDFMetadata pdfMetadata,
			@Qualifier("archiveIndex") RepositoryIndex repositoryIndex) {
		return new DocumentsRepository(RepositoryId.ARCHIVE, root, pdfMetadata, repositoryIndex);
	}

	@Bean("incoming")
	public DocumentsRepository incoming(@Qualifier("incomingRoot") File root, PDFMetadata pdfMetadata,
			@Qualifier("incomingIndex") RepositoryIndex repositoryIndex) {
		return new DocumentsRepository(RepositoryId.INCOMING, root, pdfMetadata, repositoryIndex);
	}

	@Bean(name = "complement")
	public FolderNamesIndex complement(@Qualifier("archive") DocumentsRepository archive) {
		return new FolderNamesIndex(DescriptionType.COMPLEMENT, archive);
	}

	@Bean(name = "type")
	public FolderNamesIndex type(@Qualifier("archive") DocumentsRepository archive) {
		return new FolderNamesIndex(DescriptionType.TYPE, archive);
	}

	@Bean(name = "reference")
	public FlatFileIndex reference(@Qualifier("dictionariesRoot") File root) {
		return new FlatFileIndex(DescriptionType.REFERENCE, root);
	}

	@Bean(name = "thirdparty")
	public FlatFileIndex thirdparty(@Qualifier("dictionariesRoot") File root) {
		return new FlatFileIndex(DescriptionType.THIRDPARTY, root);
	}
}
