package test;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.nio.file.attribute.DosFileAttributes;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;


public class Sender2 extends Thread{
	//socket连接
	private Socket socket = null;
	//输出流
	private DataOutputStream dos;
	//输入流
	private DataInputStream dis;
	//randomfile 用于断点传输
	private RandomAccessFile rad;
	//gui组件
	private Container contentPanel;
	private JFrame frame;
	private JProgressBar progressBar;
	private JLabel label;
	private InputStream is;
	public Sender2() {
		// TODO Auto-generated constructor stub
		//初始化窗口
		frame = new JFrame("File Transmission");
		try {
			//建立连接
			socket =new Socket("localhost",8080);
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public void run() {
		//显示文件选择窗口
		JFileChooser fChooser = new JFileChooser();
		fChooser.setMultiSelectionEnabled(true);
		int status = fChooser.showOpenDialog(null);
		if (status == JFileChooser.APPROVE_OPTION) {
			//点击确认后，获取文件路径
			File[] fileNameFiles = fChooser.getSelectedFiles();
			
			//实现多选操作，一个task可以传送多个文件
			
			try {
				dos = new DataOutputStream(socket.getOutputStream());
				dis = new DataInputStream(socket.getInputStream());
				dos.writeInt(0);
				dos.writeInt(fileNameFiles.length);
				is = new InputStream() {
					
					@Override
					public int read() throws IOException {
						// TODO Auto-generated method stub
						return 0;
					}
				};
				//
				File file;
				for(int i=0; i < fileNameFiles.length;i++) {
					String filePath = fileNameFiles[i].getAbsolutePath();
					file = new File(filePath);
			        int fileLength = (int)file.length();
			        FileInputStream fis = new FileInputStream(file);
			        //文件剩余长度	
			    	int restLen = (int) fileLength;
			                //每次读入的长度
			    	int len = 0;
			                //实际的读取长度
			    	int readLen = 0;
			                //此数组用来存储每次读入的字节流
			    	byte[] buffer = new byte[1024];
			        
			 
			        //写入文件头信息，要保证头文件信息长度一致！！记得规范一下头文件格式
			        System.out.println(file.length());
			        String fileMessage = String.format("Start--%s--%d",file.getName(),file.length());
			        
			        System.out.println("fileMessage "+fileMessage);
			        int head_len = fileMessage.getBytes().length;
			        dos.writeInt(head_len);
			        dos.write(fileMessage.getBytes(),0,fileMessage.getBytes().length);
			        dos.flush();
			 
			        //写入文件信息
			        byte[] bytes = new byte[1024];
			        int length = 0;
			        long progress = 0;
			        long max_length = file.length();
			        frame.setSize(380,120);
					contentPanel = frame.getContentPane();
					contentPanel.setLayout(
							new BoxLayout(contentPanel, BoxLayout.Y_AXIS)
							);
					progressBar = new JProgressBar();
					label = new JLabel(file.getName()+" Sending...");
					contentPanel.add(label);
					progressBar.setOrientation(JProgressBar.HORIZONTAL);
					progressBar.setMinimum(0);
					progressBar.setMaximum((int)max_length);
					progressBar.setValue((int)progress);
					progressBar.setStringPainted(true);
					progressBar.setPreferredSize(
						new Dimension(150,20)	
							);
					progressBar.setBorderPainted(true);
					progressBar.setBackground(Color.pink);
					JButton cancelButton = new JButton("cancel");
					JPanel barJPanel = new JPanel();
					barJPanel.setLayout(
							new FlowLayout(FlowLayout.LEFT));
					barJPanel.add(progressBar);
					barJPanel.add(cancelButton);
					contentPanel.add(barJPanel);
					cancelButton.addActionListener(
							new CanccelActionListener());
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					frame.setVisible(true);
//					while(restLen > 0) {
//						System.out.println(restLen);
//						len = (restLen > 1024 ? 1024 : restLen);
//						readLen = fis.read(buffer, 0, len);
//						dos.write(buffer, 0, readLen);
//						dos.flush();
//						System.out.println("readLen "+readLen);
//						restLen -= readLen;
//						progress += length;
//				        progressBar.setValue((int)progress);
//				        
//					}
//				       if(fis != null)
//					        fis.close();
					while((length = fis.read(bytes, 0, bytes.length)) != -1) {
				        if (length!=1024) System.out.println(length);
				        dos.write(bytes, 0, length);
				        dos.flush();
				        progress += length;
				        progressBar.setValue((int)progress);
				        
				        }
					if(fis != null)
				        fis.close();
					        
				}
				
				dis.close();
				dos.close();
			} catch (Exception e) {
				// TODO: handle exception
				label.setText("Sending Interrupt, Connection closed");
				e.printStackTrace();
			}
			finally {
				frame.dispose();
			}
		}
	}
	class CanccelActionListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			try {
				label.setText("Sending Interrupt, Connection closed");
				JOptionPane.showMessageDialog(frame, "Sending Interrupt, Connection closed","reminder",JOptionPane.INFORMATION_MESSAGE);
				dis.close();
				dos.close();
				frame.dispose();
				socket.close();
				
			} catch (Exception e2) {
				// TODO: handle exception
				e2.printStackTrace();
			}
		}
		
	}
}
