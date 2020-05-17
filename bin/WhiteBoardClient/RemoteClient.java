package WhiteBoardClient;

import WhiteBoardServer.SerializableBufferedImage;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class RemoteClient extends UnicastRemoteObject implements IRemoteClient {

    protected RemoteClient() throws RemoteException {


    }

    @Override
    public void say(String msg) throws RemoteException {
        System.out.println(msg);

    }

    @Override
    public void updateUserList(ArrayList<String> user_names) throws RemoteException {

    }

//    @Override
//    public void say(SerializableBufferedImage serializableBufferedImage) throws RemoteException {
//        System.out.println("Server sends msg successfully");
//    }
}
