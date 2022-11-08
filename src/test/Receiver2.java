package test;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;




public class Receiver2 extends Thread{
	private ServerSocket connectSocket = null;
	private Socket socket = null;
	private JFrame frame;
	private Container contentPanel;
	private JProgressBar progressBar;
	private DataInputStream dis;
	private DataOutputStream dos;
	private RandomAccessFile rad;
	private JLabel label;
	public Receiver2() {
		// TODO Auto-generated constructor stub
		frame = new JFrame("Receive Files");
		try {
			connectSocket = new ServerSocket(8080);
			socket = connectSocket.accept();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
	public void run() {
		try {
			dis = new DataInputStream(socket.getInputStream());
			dos = new DataOutputStream(socket.getOutputStream());
			int requestCode = dis.readInt();
			int numOfFiles = dis.readInt();
			for (int i=0; i<numOfFiles; i++){
				int permit = JOptionPane.showConfirmDialog(frame, "Are you sure to accept the file","File Transmission Request",JOptionPane.YES_NO_OPTION);
				if (permit == JOptionPane.YES_OPTION) {
				
			        //读取文件头信息
					int header_len= dis.readInt();
			        byte[] fileMessageByte = new byte[header_len];
			        int headerLength = dis.read(fileMessageByte,0,fileMessageByte.length);
			        String fileMessage = new String(fileMessageByte);
			        System.out.println(fileMessage);
			        for(int n = 0;n < fileMessage.split("--").length;n++ ) {
				        System.out.println(fileMessage.split("--")[n]);
			        	
			        }
			        //parse文件头信息，得到文件名称和文件长度
			        String fileNameWithSpace = fileMessage.split("--")[1];
			        String fileName = fileNameWithSpace.split(" ")[0];
			        long fileLength = Long.parseLong(fileMessage.split("--")[2]);
			        long fileLen = Long.parseLong(fileMessage.split("--")[2]);
			        //创建文件实例和文件输出流
			        
			        System.out.println(fileLength);
			        File file = new File(System.currentTimeMillis()+"-"+fileName);
			        FileOutputStream fos = null;
			        fos = new FileOutputStream(file);
			 
			        //开始接受文件主体
			        byte[] bytes = new byte[1024];
			        int length = 0;
			        long progress = 0;
			        //画框
			        frame.setSize(300,120);
					contentPanel = frame.getContentPane();
					contentPanel.setLayout(
							new BoxLayout(contentPanel, BoxLayout.Y_AXIS)
							);
					progressBar = new JProgressBar();
					progressBar.setValue(0);
					progressBar.setStringPainted(true);
					progressBar.setPreferredSize(
							new Dimension(150,20)
							);
					progressBar.setBorderPainted(true);
					progressBar.setBackground(Color.pink);
					JButton cancelButton = new JButton("Cancel");
					JPanel barPanel = new JPanel();
					barPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
					barPanel.add(progressBar);
					barPanel.add(cancelButton);
					contentPanel.add(barPanel);
					cancelButton.addActionListener(
							new CancelActionListener());
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					frame.setVisible(true);
					//进度条
					label = new JLabel(fileName+" Recieving...");
					contentPanel.add(label);
					progressBar.setOrientation(JProgressBar.HORIZONTAL);
					progressBar.setMinimum(0);
					progressBar.setMaximum((int)(fileLen));
			        //这个地方是关键，你输出的总byte必须和文件长度是一样的，所以当剩余的文件长度小于传输
			        //的bytes[]的长度的话，要把bytes[]的长度重新设置为小于等于剩余文件的长度。当fileLength
			        //为0的时候，所有数据传输完毕。
					
			        while(((length = dis.read(bytes, 0, bytes.length)) != -1)) {
			            fos.write(bytes, 0, length);
			            fos.flush();
			            progress += length;
			            fileLength -= length;
			           

			            if (fileLength == 0){
			                break;
			            }
			            if (fileLength < bytes.length){
			                bytes = new byte[(int)fileLength];
			            }
			            
			            progressBar.setValue((int) (progress));
						
			        }
			 
			        if(fos != null) {
			        fos.close();
			        }
				}else {
					dis.close();
					dos.close();
					frame.dispose();
				}
		        
		}
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally {
			frame.dispose();
		}
	}
	class CancelActionListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			try {
				dis.close();
				dos.close();
				rad.close();
				JOptionPane.showMessageDialog(frame, "Cancel Recieve, Connection Closed","Reminder",JOptionPane.INFORMATION_MESSAGE);
				label.setText("Cancel Recieve, Connection Closed");
				
			} catch (Exception e2) {
				// TODO: handle exception
				e2.printStackTrace();
			}
		}
		
	}
	

	
}
