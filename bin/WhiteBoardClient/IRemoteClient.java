package WhiteBoardClient;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IRemoteClient extends Remote {
    void say() throws RemoteException;
}
