package RemoteInterface;

import WhiteBoardClient.IRemoteClient;
import WhiteBoardClient.MyPoint;
import WhiteBoardServer.SerializableBufferedImage;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface IRemoteWhiteBoard extends Remote {
    boolean join(String username, IRemoteClient remoteClient) throws RemoteException;

    //    boolean create() throws RemoteException;
    boolean close(String username) throws RemoteException;

    boolean quit(String username) throws RemoteException;

    ArrayList<String> getUserList() throws RemoteException;

    boolean kick(String username) throws RemoteException;

    SerializableBufferedImage create() throws RemoteException;

    void drawLine(String username, MyPoint start, MyPoint end) throws RemoteException;
}
