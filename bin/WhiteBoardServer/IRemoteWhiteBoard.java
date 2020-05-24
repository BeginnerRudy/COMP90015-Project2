package WhiteBoardServer;

import Utils.CloseType;
import WhiteBoardClient.IRemoteClient;
import Utils.Mode;
import Utils.MyPoint;
import Utils.SerializableBufferedImage;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface IRemoteWhiteBoard extends Remote {
    /*==============================user management apis==============================*/
    boolean join(String username, IRemoteClient remoteClient) throws RemoteException;
    SerializableBufferedImage create(SerializableBufferedImage canvas) throws RemoteException;
    boolean close(String username, CloseType closeType) throws RemoteException;
    boolean quit(String username) throws RemoteException;
    boolean kick(String username) throws RemoteException;
    ArrayList<String> getUserList() throws RemoteException;

    /*===========================user remote drawing apis=============================*/
    void drawShape(String username, MyPoint start, MyPoint end, Mode mode) throws RemoteException;
    void drawString(String username, MyPoint start, Character c) throws RemoteException;
}
