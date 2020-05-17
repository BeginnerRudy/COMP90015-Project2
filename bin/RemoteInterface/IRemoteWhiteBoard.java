package RemoteInterface;

import WhiteBoardClient.IRemoteClient;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IRemoteWhiteBoard extends Remote {
    boolean join(String username, IRemoteClient remoteClient) throws RemoteException;
//    boolean create() throws RemoteException;
    boolean close(String username) throws RemoteException;
    boolean quit(String username) throws RemoteException;
//    boolean kick() throws RemoteException;
}
