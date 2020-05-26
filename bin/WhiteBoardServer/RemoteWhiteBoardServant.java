package WhiteBoardServer;

import Utils.*;
import WhiteBoardClient.IRemoteClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import static Utils.Util.getRadius;

public class RemoteWhiteBoardServant extends UnicastRemoteObject implements IRemoteWhiteBoard {
    private ConcurrentHashMap<String, IRemoteClient> users = new ConcurrentHashMap<>(); // stores a hash map between username and its remote interface
    private String manager; // The username of the manager
    private SerializableBufferedImage canvas; // This image stores the whiteboard image
    private ServerGUI serverGUI = new ServerGUI();

    protected RemoteWhiteBoardServant() throws RemoteException {
        serverGUI.getCloseButton().addActionListener(e -> {
            if (this.manager != null) {
                try {
                    this.close(manager, CloseType.SERVER_CLOSE);
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(this.serverGUI.frame, "There is no whiteboard!");
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

                if ( res == JOptionPane.YES_OPTION){
                    if (manager != null) {
                        try {
                            close(manager, CloseType.SERVER_CLOSE);
                        } catch (RemoteException ex) {
                            ex.printStackTrace();
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
    @Override
    public synchronized MessageType join(String username, IRemoteClient remoteClient) throws RemoteException {
        // check if the name has been taken
        if (!this.users.containsKey(username)) {
            // check if it is the first user
            if (manager == null) {
                // this is the first user -> promote it to be the manager
                users.put(username, remoteClient);
                manager = username;
                this.serverGUI.getManagerName().setText(username);
                try {
                    remoteClient.setToBeManager();
                    remoteClient.say("You are the manger now");
                } catch (RemoteException e) {
                    e.printStackTrace();
                    System.out.println("==Error= promote the first user to be the manager. ");
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
                    System.out.println(username);
                    try {

                        remoteClient.say("Joined successfully.");
                    } catch (RemoteException e) {
                        e.printStackTrace();
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
                    } catch (RemoteException e) {
                        e.printStackTrace();
                        System.out.println("==Error= name collision, failed to join");
                    }
                    return MessageType.REFUSED_JOIN;
                }

            }
        } else {
            // name already exits ! Failed to join.
            try {
                remoteClient.say("The name has already existed, please try another name. ");
                return MessageType.DUPLICATE_NAME;
            } catch (RemoteException e) {
                e.printStackTrace();
                System.out.println("==Error= name collision, failed to join");
            }
//            return Mes;
        }
        return MessageType.UNKNOWN_FAILURE;
    }

    @Override
    public synchronized boolean close(String username, CloseType closeType) throws RemoteException {

        // notify all the other user in the board that the board has been closed.
        if (!username.equals(manager)) {
            users.get(username).say("You are not the manager !");
            System.out.println(manager + username);
            return false;
        }

//        this.broadcastingRemoveUser(username);
//        this.users.remove(username);
        this.broadcasting("The white board is closed now");
        this.broadcastingClose(closeType);
        this.manager = null; // reset the manager
        this.serverGUI.getManagerName().setText("No manager");
        this.users.clear();
        return true;
    }

    @Override
    public synchronized boolean quit(String username) throws RemoteException {
        // notify all the other user in the board this user quits
        this.users.remove(username);
        this.broadcastingRemoveUser(username);
        this.broadcasting(username + "has quited the board");
        return true;
    }

    @Override
    public ArrayList<String> getUserList() throws RemoteException {
        ArrayList<String> users_info = new ArrayList<>();
        users_info.addAll(this.users.keySet());
        return users_info;
    }

    @Override
    public boolean kick(String username) throws RemoteException {
        this.users.get(username).say("you have been kicked");
        this.users.get(username).closeGUI(CloseType.KICKED);
        this.users.remove(username);
        broadcastingRemoveUser(username);
        return true;
    }

    @Override
    public boolean transfer(String username) throws RemoteException {
        System.out.println(username);
        if (this.users.get(username) != null) {
            this.users.get(username).becomeManager();
            this.manager = username;
            this.serverGUI.getManagerName().setText(username);
            return true;
        }
        return false;
    }

    @Override
    public SerializableBufferedImage create(SerializableBufferedImage canvas) throws RemoteException {
        this.canvas = canvas;
        broadcastingTheNewCanvas();
        return this.canvas;
    }


    /*==============================broadcasting user management action ==============================*/
    private void broadcastingTheNewCanvas() {
        for (String username : users.keySet()) {
            if (!username.equals(manager)) {
                new Thread(() -> {
                    try {
                        this.users.get(username).createCanvas(this.canvas);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        }
    }


    private void broadcastingAddUser(String username) {
        for (IRemoteClient remoteClient : users.values()) {
            new Thread(() -> {
                try {
                    remoteClient.addUser(username);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    private void broadcastingRemoveUser(String username) {
        for (IRemoteClient remoteClient : users.values()) {
            new Thread(() -> {
                try {
                    remoteClient.removeUser(username);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }


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
                        e.printStackTrace();
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
                        e.printStackTrace();
                    }
                }).start();
            }
        }

        this.users.clear();
        this.manager = null;
        this.serverGUI.getManagerName().setText("No manager");
        this.canvas = null;
    }

    private void broadcasting(String message) {
        for (IRemoteClient remoteClient : users.values()) {
            new Thread(() -> {
                try {
                    remoteClient.say(message);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    private void broadcasting(String message, String username) {
        for (String user : users.keySet()) {
            if (user != username) {
                new Thread(() -> {
                    try {
                        users.get(user).say(message);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        }
    }


    /*===========================user remote drawing apis=============================*/
    @Override
    public synchronized void drawShape(String username, MyPoint start, MyPoint end, Mode mode) throws RemoteException {
        System.out.println(String.format("Start: (%d, %d)", start.x, start.y));
        System.out.println(String.format("End: (%d, %d)", end.x, end.y));
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
            default:
                System.out.println("not support");
        }
        for (String user : users.keySet()) {
            if (!user.equals(username)) {
                if (mode != Mode.TEXT)
                    this.users.get(user).drawShape(start, end, mode);
            }
        }
    }

    @Override
    public void drawString(String username, MyPoint start, Character c) throws RemoteException {
        Graphics2D g = (Graphics2D) this.canvas.getWhiteBoard().getGraphics();
        g.setColor(Color.BLACK);
        g.drawString(String.valueOf(c), start.x, start.y);
        for (String user : users.keySet()) {
            if (!user.equals(username)) {
                this.users.get(user).drawString(start, c);
            }
        }
    }
}
