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
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.Socket;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;


public class Sender extends Thread{
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
	public Sender() {
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
			String pathString = fChooser.getSelectedFile().getPath();
			
			//实现多选操作，一个task可以传送多个文件
			
			try {
				dos = new DataOutputStream(socket.getOutputStream());
				dis = new DataInputStream(socket.getInputStream());
				//
				dos.writeUTF("OK");
				rad = new RandomAccessFile(pathString, "r");
				File file = new File(pathString);
				byte[] buf = new byte[1024];
				dos.writeUTF(file.getName());
				dos.flush();
				String rspString = dis.readUTF();
				if (rspString.equals("OK")) {
					long size = dis.readLong();
					dos.writeLong(rad.length());
					dos.writeUTF("OK");
					dos.flush();
					long offset =size;
					int barSize = (int) (rad.length()/1024);
					int barOffset = (int) (offset/1024);
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
					progressBar.setMaximum(barSize);
					progressBar.setValue(barOffset);
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
					//从指定位置开始传输
					int length;
					if (offset < rad.length()) {
						rad.seek(offset);
						 while ((length=rad.read(buf))>0) {
						 dos.write(buf,0,length);
						 progressBar.setValue(++barOffset);
						 dos.flush();
					 }
					}
					
				label.setText(file.getName()+" Sended");
					
					
				}
				dis.close();
				dos.close();
				rad.close();
			} catch (Exception e) {
				// TODO: handle exception
				label.setText("Sending Interrupt, Connection closed");
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
				rad.close();
				frame.dispose();
				socket.close();
				
			} catch (Exception e2) {
				// TODO: handle exception
				e2.printStackTrace();
			}
		}
		
	}
}
