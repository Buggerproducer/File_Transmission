package file;

public interface INetSendRecieveSpeed {
	void afterSend(int sendBytes);
	void afterRecieve(int recieveBytes);
}
