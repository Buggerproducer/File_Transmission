package file;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class NetSendRecieve implements ISendRecieve {

	//默认文件切为32k
		public static final int DEFAULT_SECTION_LEN = 1 << 15;
		private int bufferSize;
		private INetSendRecieveSpeed speed;
		
		public NetSendRecieve() {
			this.speed = new NetSendRecieveSpeedAdapter();
			this.bufferSize = DEFAULT_SECTION_LEN;
		}
		
		//可设置测速的类
		public void setSpeed(INetSendRecieveSpeed speed) {
			this.speed = speed;
		}
		
		//设置bufferSize可以设置文件一次发送的长度
		public ISendRecieve setBufferSize(int bufferSize) {
			this.bufferSize = bufferSize;
			return this;
		}
		
		public void send(DataOutputStream dos, byte[] content) throws IOException {
			int len = content.length;
			int offset = 0;
			int curLen;
			
			while (len > 0) {
				curLen = len > bufferSize ? bufferSize : len;
				dos.write(content, offset, curLen);
				offset+=curLen;
				len-=curLen;
			}
			this.speed.afterSend(len);
		}
	 
		@Override
		public byte[] recieve(DataInputStream dis, int len) throws IOException {
			byte[] buffer = new byte[len];
			
			int curLen;
			int factLen;
			int offset = 0;
			
			while (len > 0) {
				curLen = len > bufferSize ? bufferSize : len;
				factLen = dis.read(buffer, offset, curLen);
				
				len-=factLen;
				offset+=factLen;
			}
			this.speed.afterRecieve(len);
			
			return buffer;
		}
	 
	}