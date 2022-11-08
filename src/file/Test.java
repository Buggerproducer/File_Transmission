package file;

public class Test {
	
	public static void main(String[] args) {
		new BinaryReceiver(54188);
		new BinarySender("127.0.0.1", 54188);
	}
}
