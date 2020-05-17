package WhiteBoardClient;

import WhiteBoardServer.SerializableBufferedImage;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IRemoteClient extends Remote {
//    void say(SerializableBufferedImage serializableBufferedImage) throws RemoteException;
    void say(String msg) throws RemoteException;
}
