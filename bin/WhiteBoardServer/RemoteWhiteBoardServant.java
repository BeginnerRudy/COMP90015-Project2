package WhiteBoardServer;

import RemoteInterface.IRemoteWhiteBoard;
import WhiteBoardClient.IRemoteClient;
import WhiteBoardClient.Mode;
import WhiteBoardClient.MyPoint;
import WhiteBoardClient.Util;

import java.awt.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import static WhiteBoardClient.Util.getRadius;

public class RemoteWhiteBoardServant extends UnicastRemoteObject implements IRemoteWhiteBoard {
    private ConcurrentHashMap<String, IRemoteClient> users = new ConcurrentHashMap<>(); // stores a hash map between username and its remote interface
    private String manager; // The username of the manager
    private SerializableBufferedImage canvas; // This image stores the whiteboard image

    protected RemoteWhiteBoardServant() throws RemoteException {

    }

    @Override
    public synchronized boolean join(String username, IRemoteClient remoteClient) throws RemoteException {
        // check if the name has been taken
        if (!this.users.containsKey(username)) {
            // check if it is the first user
            if (manager == null) {
                // this is the first user -> promote it to be the manager
                users.put(username, remoteClient);
                manager = username;
                try {
                    remoteClient.setToBeManager();
                    remoteClient.say("You are the manger now");
                } catch (RemoteException e) {
                    e.printStackTrace();
                    System.out.println("==Error= promote the first user to be the manager. ");
                }

                // send all user in the whiteboard
                add_user(username);
                return true;

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
                    add_user(username);
                    // send this user the whiteboard, if there is one.
                    if (this.canvas != null) {
                        remoteClient.createCanvas(this.canvas);
                    }
                    return true;
                } else {

                    // name already exits ! Failed to join.
                    try {
                        remoteClient.say("You are not allowed to join.");
                    } catch (RemoteException e) {
                        e.printStackTrace();
                        System.out.println("==Error= name collision, failed to join");
                    }
                    return false;
                }

            }
        } else {
            // name already exits ! Failed to join.
            try {
                remoteClient.say("The name has already existed, please try another name. ");
            } catch (RemoteException e) {
                e.printStackTrace();
                System.out.println("==Error= name collision, failed to join");
            }
            return false;
        }
    }

    @Override
    public synchronized boolean close(String username) throws RemoteException {

        // notify all the other user in the board that the board has been closed.
        if (!username.equals(manager)) {
            users.get(username).say("You are not the manager !");
            System.out.println(manager + username);
            return false;
        }
        this.users.remove(username);
        this.broadcasting("The white board is closed now");
        this.closeGUI();
        this.manager = null; // reset the manager
        this.users.clear();
        return true;
    }

    @Override
    public synchronized boolean quit(String username) throws RemoteException {
        // notify all the other user in the board this user quits
        this.users.remove(username);
        this.remove_user(username);
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
        this.users.get(username).closeGUI();
        this.users.remove(username);
        remove_user(username);
        return true;
    }

    @Override
    public SerializableBufferedImage create(SerializableBufferedImage canvas) throws RemoteException {
        this.canvas = canvas;
        broadcastWhiteBoard();
        return this.canvas;
    }

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
            case RECTANGLE: // TODO calculate the correct point
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


    private void broadcastWhiteBoard() {
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


    private void add_user(String username) throws RemoteException {
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

    private void remove_user(String username) throws RemoteException {
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


    private void closeGUI() throws RemoteException {
        for (IRemoteClient remoteClient : users.values()) {
            new Thread(() -> {
                try {
                    remoteClient.closeGUI();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }).start();
        }
        this.canvas = null;
    }

    private void broadcasting(String message) throws RemoteException {
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

    private void broadcasting(String message, String username) throws RemoteException {
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
}
