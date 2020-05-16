package RemoteInterface;

import WhiteBoardClient.WhiteBoard;
import WhiteBoardServer.SerializablePoint;

import java.awt.event.MouseListener;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IRemoteShape extends Remote {
    void drawLine() throws RemoteException;
    WhiteBoard getCanvas() throws RemoteException;
    void addMouseListener(MouseListener mouseListener) throws  RemoteException;
////    remoteShape.addMouseMotionListener(this);
////    remoteShape.addKeyListener(this);
    boolean requestFocusInWindow() throws RemoteException;
}
