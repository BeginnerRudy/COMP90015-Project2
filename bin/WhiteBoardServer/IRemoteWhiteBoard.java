package WhiteBoardServer;

import Utils.*;
import WhiteBoardClient.IRemoteClient;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * This class defines the remote interface on the server side.
 */
public interface IRemoteWhiteBoard extends Remote {
    /*==============================user management apis==============================*/

    /**
     * This method handles the join request of the client
     * @param username The username provided by the user
     * @param remoteClient The remote interface of the client
     * @return Whether success or not indicates by the MessageType
     * @throws RemoteException
     */
    MessageType join(String username, IRemoteClient remoteClient) throws RemoteException;

    /**
     * This method is responsible for create a whiteboard on all the other clients excepts the manger.
     * @param canvas The canvas sent by the manager
     * @return
     * @throws RemoteException
     */
    SerializableBufferedImage create(SerializableBufferedImage canvas) throws RemoteException;

    /**
     * This method is used to close the whiteboard
     * @param username The username who wants to close the whiteboard
     * @param closeType The closeType to define the semantics of the close operation
     * @return
     * @throws RemoteException
     */
    boolean close(String username, CloseType closeType) throws RemoteException;

    /**
     * This method handles the quit requests of the user
     *
     * @param username The username of who wants to quit
     * @return
     * @throws RemoteException
     */
    boolean quit(String username) throws RemoteException;

    /**
     * This method handles the kick out requests from the manger
     *
     * @param username The username of the user to be kciked out
     * @return
     * @throws RemoteException
     */
    boolean kick(String username) throws RemoteException;

    /**
     * This method handles the request of asking for all the usernames.
     * @return All the users' usernames in the current whiteboard
     * @throws RemoteException
     */
    ArrayList<String> getUserList() throws RemoteException;

    /**
     * This method handles the transfer of ownership request
     *
     * @param username The username of who send this request
     * @return
     * @throws RemoteException
     */
    boolean transfer(String username) throws RemoteException;

    /*===========================user remote drawing apis=============================*/

    /**
     * This methods handles the broadcasting of the drawShape method
     * @param username The username of who calls this RMI
     * @param start The start point of the shape
     * @param end The end point of the shape
     * @param mode The drawing mode
     * @throws RemoteException
     */
    void drawShape(String username, MyPoint start, MyPoint end, Mode mode) throws RemoteException;

    /**
     * This method handles the drawing string request from the clilent
     * @param username The username of who sends this request
     * @param start The start point of the character to draw
     * @param c The char to draw
     * @throws RemoteException
     */
    void drawString(String username, MyPoint start, Character c) throws RemoteException;
}
