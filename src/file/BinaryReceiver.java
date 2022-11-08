package file;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class BinaryReceiver implements Runnable{
	private ServerSocket reciever;
	private volatile boolean goon;
	
	public BinaryReceiver(int port) {
		try {
			goon = true;
			this.reciever = new ServerSocket(port);
			new Thread(this, "接收端").start();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	private void shutdown() {
		if(goon == true || this.reciever == null && this.reciever.isClosed()) {
			System.out.println("x");
			return;
		}
		try {
			System.out.println("x");
			this.reciever.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
 
	@Override
	public void run() {
 
		while (goon) {
			try {
				Socket sender = reciever.accept();
                                //单独开辟一个内部类线程去完成接收任务
				new Reciever(sender);
			} catch (IOException e) {
				goon = false;
				shutdown();
			} 
		}
	}
	
	class Reciever implements Runnable {
		private Socket sender;
		private DataInputStream dis;
		public Reciever(Socket sender) {
			try {
				this.sender = sender;
				this.dis = new DataInputStream(sender.getInputStream());
				new Thread(this).start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		private void close() {
			if (sender.isClosed() || dis == null) {
				return;
			}
			try {
				sender.close();
				dis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		@Override
		public void run() {
			
			try {
				File file = new File("/Users/gilbertyou/Desktop/taget2.pdf");
				byte[] buffer = new byte[1024];
				int len = dis.read(buffer);
				System.out.println("本次接收字节数：" + len);
				
				System.out.println(BytesTransform.bytesToString(buffer));
				
				RandomAccessFile rafOutput = new RandomAccessFile(file, "rw");
				rafOutput.write(buffer);
				
				rafOutput.close();
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			close();
		}		
	}
}
