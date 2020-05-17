package WhiteBoardServer;

import RemoteInterface.IRemoteWhiteBoard;
import WhiteBoardClient.IRemoteClient;

import java.awt.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class RemoteWhiteBoardServant extends UnicastRemoteObject implements IRemoteWhiteBoard {
    private HashMap<String, IRemoteClient> users = new HashMap<>();
    private String manager;
    private SerializableBufferedImage canvas;

    protected RemoteWhiteBoardServant() throws RemoteException {
    }

    @Override
    public boolean join(String username, IRemoteClient remoteClient) throws RemoteException {
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
                    if (this.canvas != null){
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
    public boolean close(String username) throws RemoteException {

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
    public boolean quit(String username) throws RemoteException {
        // notify all the other user in the board this user quits
        this.users.remove(username);
        this.remove_user(username);
        this.broadcasting(username + "has quited the board");
        return true;
    }

    @Override
    public ArrayList<String> getUserList() throws RemoteException {
        ArrayList<String> users_info = new ArrayList<>();
        for (String username : this.users.keySet()) {
            users_info.add(username);
        }
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
    public SerializableBufferedImage create() throws RemoteException {
        this.canvas = new SerializableBufferedImage(300, 300);
        Graphics2D g = (Graphics2D) this.canvas.getWhiteBoard().getGraphics();
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(2));
        Random random = new Random();
        g.drawLine(random.nextInt(200), random.nextInt(200), random.nextInt(200), random.nextInt(200));
        broadcastWhiteBoard();
        return this.canvas;
    }

    private void broadcastWhiteBoard() throws RemoteException{
        for (String username : users.keySet()) {
            if (!username.equals(manager)){
                this.users.get(username).createCanvas(this.canvas);
            }
        }
    }

    private void updateUserList() throws RemoteException {
        ArrayList<String> users_list = new ArrayList<String>();
        users_list.addAll(this.users.keySet());
        for (IRemoteClient remoteClient : users.values()) {
            remoteClient.updateUserList(users_list);
        }
    }

    private void add_user(String username) throws RemoteException {
        for (IRemoteClient remoteClient : users.values()) {
            remoteClient.addUser(username);
        }
    }

    private void remove_user(String username) throws RemoteException {
        for (IRemoteClient remoteClient : users.values()) {
            remoteClient.removeUser(username);
        }
    }


    private void closeGUI() throws RemoteException {
        for (IRemoteClient remoteClient : users.values()) {
            remoteClient.closeGUI();
        }
    }

    private void broadcasting(String message) throws RemoteException {
        for (IRemoteClient remoteClient : users.values()) {
            remoteClient.say(message);
        }
    }

    private void broadcasting(String message, String username) throws RemoteException {
        for (String user : users.keySet()) {
            if (user != username) {
                users.get(user).say(message);
            }
        }
    }
}
