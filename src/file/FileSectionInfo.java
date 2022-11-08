package file;
import file.BytesTransform;

public class FileSectionInfo {private int fileNo;//文件编号
private long offset;//文件偏移量
private int len;//文件片段长度
private byte[] content;//实际的文件内容

FileSectionInfo() {
}

FileSectionInfo(int fileNo, long offset, int len) {
	this.fileNo = fileNo;
	this.offset = offset;
	this.len = len;
}

FileSectionInfo(byte[] bytes) {
	this.fileNo = BytesTransform.bytesToInt(bytes, 0);
	this.offset = BytesTransform.bytesToInt(bytes, 4);
	this.len = BytesTransform.bytesToInt(bytes, 12);	
}

//将文件信息转化为byte数组
byte[] toBytes() {
	byte[] res = new byte[16];
	BytesTransform.intToBytes(res, 0, fileNo);
	BytesTransform.longToBytes(res, 4, offset);
	BytesTransform.intToBytes(res, 12, len);
	
	return res;
}

int getFileNo() {
	return fileNo;
}

void setFileNo(int fileNo) {
	this.fileNo = fileNo;
}

long getOffset() {
	return offset;
}

void setOffset(long offset) {
	this.offset = offset;
}

int getLen() {
	return len;
}

void setLen(int len) {
	this.len = len;
}

byte[] getContent() {
	return content;
}

void setContent(byte[] content) {
	this.content = content;
}

@Override
public String toString() {
	return fileNo + ": " + offset  + ", " + len;
}	
}
