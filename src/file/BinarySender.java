package file;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.net.UnknownHostException;

public class BinarySender implements Runnable{
	private Socket socket;
	private DataOutputStream dos;
	
	public BinarySender(String ip, int port) {
		try {
			this.socket = new Socket(ip, port);
			this.dos = new DataOutputStream(socket.getOutputStream());
			new Thread(this,"发送端").start();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
				e.printStackTrace();
		}
		
	}
	
	private byte[] getSection(RandomAccessFile raf, int offset, int len) throws IOException {
		byte[] buffer = new byte[len];
		
		raf.seek(offset);
		raf.read(buffer, offset, len);
		return buffer;
	}
	
	private void close() {
		try {
			dos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			this.socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
 
	@Override
	public void run() {
		try {
			File file = new File("/Users/gilbertyou/Desktop/3月雅思作文大范围预测.pdf");
			RandomAccessFile rafInput = new RandomAccessFile(file, "r");
			int len = (int) rafInput.length();
			
			byte[] buffer = getSection(rafInput, 0, len);
			System.out.println(BytesTransform.bytesToString(buffer));
			
			dos.write(buffer);
			rafInput.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		close();
	}
}
