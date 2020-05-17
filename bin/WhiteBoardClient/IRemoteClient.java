package WhiteBoardClient;

import WhiteBoardServer.SerializableBufferedImage;

import java.awt.*;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface IRemoteClient extends Remote {
//    void say(SerializableBufferedImage serializableBufferedImage) throws RemoteException;
    void say(String msg) throws RemoteException;

    void updateUserList(ArrayList<String> user_names) throws RemoteException;

    void addUser(String username) throws RemoteException;
    void removeUser(String username) throws RemoteException;
    void setToBeManager() throws RemoteException;
    void closeGUI() throws RemoteException;
    boolean allowJoins(String username) throws RemoteException;
    void createCanvas(SerializableBufferedImage canvas) throws RemoteException;
    void drawLine(MyPoint start, MyPoint end) throws RemoteException;
}
