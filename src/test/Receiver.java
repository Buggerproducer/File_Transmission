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



public class Receiver extends Thread{
	private ServerSocket connectSocket = null;
	private Socket socket = null;
	private JFrame frame;
	private Container contentPanel;
	private JProgressBar progressBar;
	private DataInputStream dis;
	private DataOutputStream dos;
	private RandomAccessFile rad;
	private JLabel label;
	public Receiver() {
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
			dis.readUTF();
			int permit = JOptionPane.showConfirmDialog(frame, "Are you sure to accept the file","File Transmission Request",JOptionPane.YES_NO_OPTION);
			if (permit == JOptionPane.YES_OPTION) {
				String filename = dis.readUTF();
				dos.writeUTF("OK");
				dos.flush();
				File file = new File(filename+".temp");
				rad = new RandomAccessFile(filename+".temp", "rw");
				long size = 0;
				if (file.exists()&&file.isFile()) {
					size = file.length();
				}
				dos.writeLong(size);
				dos.flush();
				long allSize = dis.readLong();
				String rspString = dis.readUTF();
				int barSize = (int)(allSize/1024);
				int barOffset = (int)(size/1024);
				//传输界面
				frame.setSize(300,120);
				contentPanel = frame.getContentPane();
				contentPanel.setLayout(
						new BoxLayout(contentPanel, BoxLayout.Y_AXIS)
						);
				progressBar = new JProgressBar();
				//进度条
				label = new JLabel(filename+" Recieving...");
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
				//接收文件
				if(rspString.equals("OK")) {
					rad.seek(size);
					int length;
					byte[] buf = new byte[1024];
					while((length=dis.read(buf,0,buf.length) )!=-1) {
						rad.write(buf,0,length);
						progressBar.setValue(++barOffset);
					}
					System.out.println("File Receive end...");
					
				}
				label.setText(filename+"Finish Receieving");
				dis.close();
				dos.close();
				rad.close();
				frame.dispose();
				//文件重命名
				if(barOffset>=barSize) {
					file.renameTo(new File(filename));
				}
				
				
				
			}else {
				dis.close();
				dos.close();
				frame.dispose();
			}
		} catch (Exception e) {
			// TODO: handle exception
			label.setText("Sth Wrong!, Connection Lost");
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
