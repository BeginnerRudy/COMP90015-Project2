package RemoteInterface;

import WhiteBoardClient.IRemoteClient;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IRemoteWhiteBoard extends Remote {
    boolean join(String username, IRemoteClient remoteClient) throws RemoteException;
//    boolean create() throws RuntimeException;
//    boolean close() throws RuntimeException;
//    boolean quit() throws RuntimeException;
//    boolean kick() throws RuntimeException;
}
