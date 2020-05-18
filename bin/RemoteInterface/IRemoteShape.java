package RemoteInterface;

import WhiteBoardClient.IRemoteClient;
import WhiteBoardServer.SerializableBufferedImage;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IRemoteShape extends Remote {
    void drawLine() throws RemoteException;
    SerializableBufferedImage getCanvas() throws RemoteException;
//    void addMouseListener(MouseListener mouseListener) throws  RemoteException;
    void sendRemoteClient(IRemoteClient remoteClient) throws RemoteException;
////    remoteShape.addMouseMotionListener(this);
////    remoteShape.addKeyListener(this);
//    boolean requestFocusInWindow() throws RemoteException;
}
