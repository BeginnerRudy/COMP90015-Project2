package WhiteBoardClient;

import Utils.*;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * This is the RMI interface on the client site, it defines how could the server interact with it.
 */
public interface IRemoteClient extends Remote {

    /**
     * This method is used for the server to print string on the server side
     *
     * @param msg The msg sent to the client
     * @throws RemoteException
     */
    void say(String msg) throws RemoteException;

    /*==============================user management apis==============================*/
    /**
     * This method is responsible for update the user list with all current users.
     * @param user_names The usernames of all current users
     * @throws RemoteException
     */
    void updateUserList(ArrayList<String> user_names) throws RemoteException;

    /**
     * This method is responsible for add new user to the user list on the client side
     * @param username The username of the new user
     * @throws RemoteException
     */
    void addUser(String username) throws RemoteException;

    /**
     * This method is responsible for remove the given username
     * @param username The username of the user who left the whiteboard
     * @throws RemoteException
     */
    void removeUser(String username) throws RemoteException;

    /**
     * This method set the user to be the manager by set its isManager fields to be true.
     *
     * @throws RemoteException
     */
    void setToBeManager() throws RemoteException;

    /**
     * This method is used to close the whiteboard
     * @param closeType This indicates the close type of the close operation
     * @throws RemoteException
     */
    void closeGUI(CloseType closeType) throws RemoteException;

    /**
     * This method is used to ask the manager whether allow the user with given username to join.
     *
     * @param username The username of the user who wants to join in.
     * @return true indicates the manager allows the join and false means join been refused
     * @throws RemoteException
     */
    boolean allowJoins(String username) throws RemoteException;

    /**
     * This method method is used to created the whiteboard for the client by the given canvas parameter.
     *
     * @param canvas This is the whiteboard object the server would send
     * @throws RemoteException
     */
    void createCanvas(SerializableBufferedImage canvas) throws RemoteException;

    /**
     * This method would delegate this client to be manager of the system.
     * @throws RemoteException
     */
    void becomeManager() throws RemoteException;

    /*===========================user remote drawing apis=============================*/
    /**
     * This method is used to draw shape on the remote client
     * @param start The start point of the shape
     * @param end The end point of the shape
     * @param mode The drawing mode
     * @throws RemoteException
     */
    void drawShape(MyPoint start, MyPoint end, Mode mode) throws RemoteException;

    /**
     * This method is used to draw character on to the remote client whiteboard on the given start point
     *
     * @param start The point for the char to start
     * @param c The char to draw
     * @throws RemoteException
     */
    void drawString(MyPoint start, Character c) throws RemoteException;
}
