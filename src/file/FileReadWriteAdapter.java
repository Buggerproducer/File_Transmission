package file;

 
public class FileReadWriteAdapter implements IFileReadWriteIntercepter{

	@Override
	public void beforeRead(FileSectionInfo section) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public FileSectionInfo afterRead(FileSectionInfo section) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void beforeWrite(String filePath, FileSectionInfo section) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterWritten(FileSectionInfo section) {
		// TODO Auto-generated method stub
		
	}
 
	
 
}
