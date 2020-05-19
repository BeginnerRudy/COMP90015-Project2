package RemoteInterface;

import WhiteBoardClient.IRemoteClient;
import WhiteBoardClient.Mode;
import WhiteBoardClient.MyPoint;
import WhiteBoardServer.SerializableBufferedImage;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface IRemoteWhiteBoard extends Remote {
    boolean join(String username, IRemoteClient remoteClient) throws RemoteException;

    SerializableBufferedImage create(SerializableBufferedImage canvas) throws RemoteException;

    boolean close(String username) throws RemoteException;

    boolean quit(String username) throws RemoteException;

    boolean kick(String username) throws RemoteException;

    ArrayList<String> getUserList() throws RemoteException;

    void drawLine(String username, MyPoint start, MyPoint end, Mode mode) throws RemoteException;
}
