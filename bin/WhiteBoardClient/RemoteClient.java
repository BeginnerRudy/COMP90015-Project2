package WhiteBoardClient;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RemoteClient extends UnicastRemoteObject implements IRemoteClient {

    protected RemoteClient() throws RemoteException {
    }

    @Override
    public void say() throws RemoteException {
        System.out.println("Server sends msg successfully");
    }
}
