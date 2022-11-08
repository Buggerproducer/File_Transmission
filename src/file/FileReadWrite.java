package file;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class FileReadWrite {
	private int fileNo;
	private String filePath;
	private RandomAccessFile raf;
	private IFileReadWriteIntercepter fileReadWriteIntercept;

public FileReadWrite(int fileNo, String filePath) {
	this.fileNo = fileNo;
	this.filePath = filePath;
	//默认拦截，什么都没做
	this.fileReadWriteIntercept = new FileReadWriteAdapter();
}

public int getFileNo() {
	return fileNo;
}

public void setFileReadWriteIntercept(IFileReadWriteIntercepter fileReadWriteIntercept) {
	this.fileReadWriteIntercept = fileReadWriteIntercept;
}

public FileSectionInfo readSection(FileSectionInfo section) throws IOException{
	//保证raf单例
	this.fileReadWriteIntercept.beforeRead(section);
	if (raf == null) {
		raf = new RandomAccessFile(filePath, "r");
	}
	
	//文件读写指针偏移到指定位置
	raf.seek(section.getOffset());
	byte[] buffer = new byte[section.getLen()];
	
	//读的内容放入section里
	raf.read(buffer);
	section.setContent(buffer);
	
	return this.fileReadWriteIntercept.afterRead(section);
}

public boolean writeSection(FileSectionInfo section) {
	this.fileReadWriteIntercept.beforeWrite(filePath, section);
	
	//双重判断保证raf单例
	try {
		if (this.raf == null) {
			synchronized (this.filePath) {
				if (this.raf == null) {
					this.raf = new RandomAccessFile(this.filePath, "rw");
				}
			}
		}
	} catch (FileNotFoundException e) {
		e.printStackTrace();
		return false;
	}
	
	//加锁保证线程安全
	try {
		synchronized (this.filePath) {
			raf.seek(section.getOffset());
			raf.write(section.getContent());
			this.fileReadWriteIntercept.afterWritten(section);
		}
	} catch (IOException e) {
		e.printStackTrace();
		return false;
	}
	
	return true;
}

public void close() {
	try {
		this.raf.close();
	} catch (IOException e) {
		e.printStackTrace();
	}
}
}
