package WhiteBoardServer;

import Utils.*;
import WhiteBoardClient.IRemoteClient;

import javax.swing.*;
import java.awt.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import static Utils.Util.getRadius;
import static Utils.Util.serverPrinter;

/**
 * This class is responsible for the server side logic, it implements the IRemoteWhiteBoard interface.
 * The client would communicate the server with this interface.
 */
public class RemoteWhiteBoardServant extends UnicastRemoteObject implements IRemoteWhiteBoard {
    private ConcurrentHashMap<String, IRemoteClient> users = new ConcurrentHashMap<>(); // stores a hash map between username and its remote interface
    private String manager; // The username of the manager
    private SerializableBufferedImage canvas; // This image stores the whiteboard image
    private ServerGUI serverGUI = new ServerGUI();

    /**
     * The constructor of the class.
     *
     * @throws RemoteException
     */
    protected RemoteWhiteBoardServant() throws RemoteException {
        serverGUI.getCloseButton().addActionListener(e -> {
            if (this.manager != null) {
                try {
                    this.close(manager, CloseType.SERVER_CLOSE);
                } catch (RemoteException ex) {
//                    ex.printStackTrace();
                    serverPrinter("Error", "Server fail to close the white board. ");
                }
            } else {
                JOptionPane.showMessageDialog(this.serverGUI.frame, "There is no whiteboard!");
                serverPrinter("Error", "There is no whiteboard for server to close.");
            }
        });

        this.serverGUI.getManagerName().setText("No manager");
        this.serverGUI.frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.serverGUI.frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                int res = JOptionPane.showConfirmDialog(serverGUI.frame,
                        "Are you sure you want to close this whiteboard?", "Close Window?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);

                if (res == JOptionPane.YES_OPTION) {
                    if (manager != null) {
                        try {
                            close(manager, CloseType.SERVER_CLOSE);
                        } catch (RemoteException ex) {
//                            ex.printStackTrace();
                            serverPrinter("Error", "Server fail to close the white board. ");
                        }
                    }
                    new Thread(() -> {
                        System.out.print("Shutting down...");
                        System.out.println("done");
                        System.exit(0);
                    }).start();
                }
            }
        });

//        System.out.println("Closed");
    }

    /*==============================user management apis==============================*/
    /**
     * This method handles the join request of the client
     * @param username The username provided by the user
     * @param remoteClient The remote interface of the client
     * @return Whether success or not indicates by the MessageType
     * @throws RemoteException
     */
    @Override
    public synchronized MessageType join(String username, IRemoteClient remoteClient) throws RemoteException {
        // check if the name has been taken
        if (!this.users.containsKey(username)) {
            // check if it is the first user
            if (manager == null) {
                // this is the first user -> promote it to be the manager
                users.put(username, remoteClient);
                serverPrinter("Success", username + " has joined the white board. ");
                serverPrinter("Success", username + " is the manager now.");
                manager = username;
                this.serverGUI.getManagerName().setText(username);
                try {
                    remoteClient.setToBeManager();
                    remoteClient.say("You are the manger now");
                } catch (RemoteException e) {
//                    e.printStackTrace();
                    serverPrinter("Error", "Fail to set the uer as the manager");
//                    System.out.println("==Error= promote the first user to be the manager. ");
                }

                // send all user in the whiteboard
                broadcastingAddUser(username);
                return MessageType.SUCCESS_JOIN;

            } else {
                // ask whether the manager allow this new user to join
                if (this.users.get(manager).allowJoins(username)) {
                    ArrayList<String> users_list = new ArrayList<String>();
                    users_list.addAll(this.users.keySet());
                    remoteClient.updateUserList(users_list);
                    // add new peer to
                    users.put(username, remoteClient);
                    this.broadcasting(username + " jas joined the board!", username);
                    serverPrinter("Success", username + " has joined the white board. ");
                    try {
                        remoteClient.say("Joined successfully.");
                    } catch (RemoteException e) {
//                        e.printStackTrace();
                        serverPrinter("Error", "Failed to notify the " + username + " that it has joined the whiteboard. ");
                    }
                    // send all user in the whiteboard
                    broadcastingAddUser(username);
                    // send this user the whiteboard, if there is one.
                    if (this.canvas != null) {
                        remoteClient.createCanvas(this.canvas);
                    }
                    return MessageType.SUCCESS_JOIN;
                } else {

                    try {
                        remoteClient.say("You are not allowed to join.");
                        serverPrinter("Success", username + " has been refused to join the white board. ");
                    } catch (RemoteException e) {
                        serverPrinter("Error", "Fail to notify the " + username + " has joined the white board. ");
//                        e.printStackTrace();
//                        System.out.println("==Error= name collision, failed to join");
                    }
                    return MessageType.REFUSED_JOIN;
                }

            }
        } else {
            // name already exits ! Failed to join.
            try {
                remoteClient.say("The name has already existed, please try another name. ");
                serverPrinter("Success", username + " has already in the white board, not allowed to join. ");
                return MessageType.DUPLICATE_NAME;
            } catch (RemoteException e) {
                e.printStackTrace();
                serverPrinter("Error", " Fail to notify the username is duplicated.");
//                System.out.println("==Error= name collision, failed to join");
            }
//            return Mes;
        }
        return MessageType.UNKNOWN_FAILURE;
    }

    /**
     * This method is used to close the whiteboard
     * @param username The username who wants to close the whiteboard
     * @param closeType The closeType to define the semantics of the close operation
     * @return
     * @throws RemoteException
     */
    @Override
    public synchronized boolean close(String username, CloseType closeType) throws RemoteException {

        // notify all the other user in the board that the board has been closed.
        if (!username.equals(manager)) {
            users.get(username).say("You are not the manager !");
            serverPrinter("Success", username + " tries to close the whiteboard but it is not the manager! ");
//            System.out.println(manager + username);
            return false;
        }

//        this.broadcastingRemoveUser(username);
//        this.users.remove(username);
        this.broadcasting("The white board is closed now");
        this.broadcastingClose(closeType);
        this.manager = null; // reset the manager
        this.serverGUI.getManagerName().setText("No manager");
        this.users.clear();

        serverPrinter("Success", "Current whiteboard is closed.");
        return true;
    }

    /**
     * This method handles the quit requests of the user
     *
     * @param username The username of who wants to quit
     * @return
     * @throws RemoteException
     */
    @Override
    public synchronized boolean quit(String username) throws RemoteException {
        // notify all the other user in the board this user quits
        this.users.remove(username);
        this.broadcastingRemoveUser(username);
        this.broadcasting(username + "has quited the board");
        serverPrinter("Success", username + " has quited the white board. ");
        return true;
    }

    /**
     * This method handles the request of asking for all the usernames.
     * @return All the users' usernames in the current whiteboard
     * @throws RemoteException
     */
    @Override
    public ArrayList<String> getUserList() throws RemoteException {
        ArrayList<String> users_info = new ArrayList<>();
        users_info.addAll(this.users.keySet());
        return users_info;
    }

    /**
     * This method handles the kick out requests from the manger
     *
     * @param username The username of the user to be kciked out
     * @return
     * @throws RemoteException
     */
    @Override
    public boolean kick(String username) throws RemoteException {
        this.users.get(username).say("you have been kicked");
        serverPrinter("Success", username + " has been kicked out the whiteboard. ");
        this.users.get(username).closeGUI(CloseType.KICKED);
        this.users.remove(username);
        broadcastingRemoveUser(username);
        return true;
    }

    /**
     * This method handles the transfer of ownership request
     *
     * @param username The username of who send this request
     * @return
     * @throws RemoteException
     */
    @Override
    public boolean transfer(String username) throws RemoteException {
//        System.out.println(username);
        if (this.users.get(username) != null) {
            this.users.get(username).becomeManager();
            this.manager = username;
            this.serverGUI.getManagerName().setText(username);
            serverPrinter("Success", username + " has become the white board manager. ");
            return true;
        }
        return false;
    }

    /**
     * This method is responsible for create a whiteboard on all the other clients excepts the manger.
     * @param canvas The canvas sent by the manager
     * @return
     * @throws RemoteException
     */
    @Override
    public SerializableBufferedImage create(SerializableBufferedImage canvas) throws RemoteException {
        serverPrinter("Success", " new canvas is initialized. ");
        this.canvas = canvas;
        broadcastingTheNewCanvas();
        return this.canvas;
    }


    /*==============================broadcasting user management action ==============================*/
    /**
     * This method would concurrently create whiteboard for each client in the current whiteboard.
     */
    private void broadcastingTheNewCanvas() {
        for (String username : users.keySet()) {
            if (!username.equals(manager)) {
                new Thread(() -> {
                    try {
                        this.users.get(username).createCanvas(this.canvas);
                    } catch (RemoteException e) {
//                        e.printStackTrace();
                        serverPrinter("Error", "Failed to create whiteboard for " + username);
                    }
                }).start();
            }
        }
    }


    /**
     * This method would add the username to all the clients in the whiteboard concurrently
     *
     * @param username The username to be add
     */
    private void broadcastingAddUser(String username) {
        for (IRemoteClient remoteClient : users.values()) {
            new Thread(() -> {
                try {
                    remoteClient.addUser(username);
                } catch (RemoteException e) {
//                    e.printStackTrace();
                    serverPrinter("Error", "Failed to update user list. ");
                }
            }).start();
        }
    }

    /**
     * This method would remove the username to all the clients in the whiteboard concurrently
     *
     * @param username The username to be remove
     */
    private void broadcastingRemoveUser(String username) {
        for (IRemoteClient remoteClient : users.values()) {
            new Thread(() -> {
                try {
                    remoteClient.removeUser(username);
                } catch (RemoteException e) {
//                    e.printStackTrace();
                    serverPrinter("Error", "Failed to remove user from user list. ");
                }
            }).start();
        }
    }


    /**
     * This method is responsible for close the whiteboard concurrently depends on the closeType passed in.
     *
     * @param closeType The closeType of this close operation
     */
    private void broadcastingClose(CloseType closeType) {
        // remove all user from every user's user list
        for (String username : users.keySet()) {
            this.broadcastingRemoveUser(username);
        }

        if (closeType.equals(CloseType.SERVER_CLOSE)) {
            for (IRemoteClient remoteClient : users.values()) {
                new Thread(() -> {
                    try {
                        remoteClient.closeGUI(closeType);
                    } catch (RemoteException e) {
//                        e.printStackTrace();
                        serverPrinter("Error", "Failed to close client");
                    }
                }).start();
            }
        } else if (closeType.equals(CloseType.MANAGER_CLOSE)) {
            this.users.remove(manager);
            for (IRemoteClient remoteClient : users.values()) {
                new Thread(() -> {
                    try {
                        remoteClient.closeGUI(closeType);
                    } catch (RemoteException e) {
//                        e.printStackTrace();
                        serverPrinter("Error", "Failed to close client");
                    }
                }).start();
            }
        }

        this.users.clear();
        this.manager = null;
        this.serverGUI.getManagerName().setText("No manager");
        serverPrinter("Success", "The canvas has been removed.");
        this.canvas = null;
    }

    /**
     * This method is used to broadcasting the message to all the users.
     *
     * @param message The message to send to each client
     */
    private void broadcasting(String message) {
        for (IRemoteClient remoteClient : users.values()) {
            new Thread(() -> {
                try {
                    remoteClient.say(message);
                } catch (RemoteException e) {
//                    e.printStackTrace();
                    serverPrinter("Error", "Failed to send message to client by the say API.");
                }
            }).start();
        }
    }

    /**
     * This method is used for broadcasting message from one user to all the others.
     *
     * @param message The message to be broadcasting
     * @param username The user who wants to broadcast the message.
     */
    private void broadcasting(String message, String username) {
        for (String user : users.keySet()) {
            if (user != username) {
                new Thread(() -> {
                    try {
                        users.get(user).say(message);
                    } catch (RemoteException e) {
//                        e.printStackTrace();
                        serverPrinter("Error", "Failed to send message to client by the say API.");
                    }
                }).start();
            }
        }
    }


    /*===========================user remote drawing apis=============================*/
    /**
     * This methods handles the broadcasting of the drawShape method
     * @param username The username of who calls this RMI
     * @param start The start point of the shape
     * @param end The end point of the shape
     * @param mode The drawing mode
     * @throws RemoteException
     */
    @Override
    public synchronized void drawShape(String username, MyPoint start, MyPoint end, Mode mode) throws RemoteException {
//        System.out.println(String.format("Start: (%d, %d)", start.x, start.y));
//        System.out.println(String.format("End: (%d, %d)", end.x, end.y));
        Graphics2D g = (Graphics2D) this.canvas.getWhiteBoard().getGraphics();
        g.setColor(Color.BLACK);
        switch (mode) {
            case LINE:
            case FREEHAND:
                g.drawLine(start.x, start.y, end.x, end.y);
                break;
            case RECTANGLE:
                Util.PairOfPoints pairOfPoints = Util.getCorrectPoints(start, end);

                start = pairOfPoints.start;
                end = pairOfPoints.end;
                g.drawRect(start.x, start.y, end.x - start.x, end.y - start.y);
                break;
            case CIRCLE:
                int radius = getRadius(start, end);
                pairOfPoints = Util.getCorrectPoints(start, end);
                start = pairOfPoints.start;
                end = pairOfPoints.end;
                g.drawOval(start.x, start.y, radius, radius);
            case TEXT:
                break;
            default:
                System.out.println("not support");
        }
        for (String user : users.keySet()) {
            if (!user.equals(username)) {
                if (mode != Mode.TEXT)
                    this.users.get(user).drawShape(start, end, mode);
            }
        }
        serverPrinter("Success",username+ " has drawn a shape.") ;

    }

    /**
     * This method handles the drawing string request from the clilent
     * @param username The username of who sends this request
     * @param start The start point of the character to draw
     * @param c The char to draw
     * @throws RemoteException
     */
    @Override
    public void drawString(String username, MyPoint start, Character c) throws RemoteException {
        Graphics2D g = (Graphics2D) this.canvas.getWhiteBoard().getGraphics();
        g.setColor(Color.BLACK);
        g.drawString(String.valueOf(c), start.x, start.y);
        for (String user : users.keySet()) {
            if (!user.equals(username)) {
                this.users.get(user).drawString(start, c);
                serverPrinter("Success",username+ " has input a text.") ;

            }
        }
    }
}
