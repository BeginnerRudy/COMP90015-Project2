package WhiteBoardClient;

import Utils.Mode;
import Utils.MyPoint;
import Utils.SerializableBufferedImage;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface IRemoteClient extends Remote {
    void say(String msg) throws RemoteException;

    /*==============================user management apis==============================*/
    void updateUserList(ArrayList<String> user_names) throws RemoteException;
    void addUser(String username) throws RemoteException;
    void removeUser(String username) throws RemoteException;
    void setToBeManager() throws RemoteException;
    void closeGUI() throws RemoteException;
    boolean allowJoins(String username) throws RemoteException;
    void createCanvas(SerializableBufferedImage canvas) throws RemoteException;

    /*===========================user remote drawing apis=============================*/
    void drawShape(MyPoint start, MyPoint end, Mode mode) throws RemoteException;
    void drawString(MyPoint start, Character c) throws RemoteException;
}
