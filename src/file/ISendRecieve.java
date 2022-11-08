package file;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface ISendRecieve {
    void send(DataOutputStream dos, byte[] content) throws IOException;
    byte[] recieve(DataInputStream dis, int len) throws IOException;
}
