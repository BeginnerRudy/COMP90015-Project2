package WhiteBoardClient;

import WhiteBoardServer.SerializableBufferedImage;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RemoteClient extends UnicastRemoteObject implements IRemoteClient {

    protected RemoteClient() throws RemoteException {


    }

    @Override
    public void say(String msg) throws RemoteException {
        System.out.println(msg);

    }

//    @Override
//    public void say(SerializableBufferedImage serializableBufferedImage) throws RemoteException {
//        System.out.println("Server sends msg successfully");
//    }
}
