package file;

public interface IFileReadWriteIntercepter {
	void beforeRead(FileSectionInfo section);
	FileSectionInfo afterRead(FileSectionInfo section);
	void beforeWrite(String filePath, FileSectionInfo section);
	void afterWritten(FileSectionInfo section);
}
