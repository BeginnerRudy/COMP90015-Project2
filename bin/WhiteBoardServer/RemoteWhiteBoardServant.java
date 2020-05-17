package WhiteBoardServer;

import RemoteInterface.IRemoteWhiteBoard;
import WhiteBoardClient.IRemoteClient;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class RemoteWhiteBoardServant extends UnicastRemoteObject implements IRemoteWhiteBoard {
    private HashMap<String, IRemoteClient> users = new HashMap<>();
    private String manager;

    protected RemoteWhiteBoardServant() throws RemoteException {
    }

    @Override
    public boolean join(String username, IRemoteClient remoteClient) throws RemoteException{
        // check if the name has been taken
        if (!this.users.containsKey(username)) {
            // add new peer to
            users.put(username, remoteClient);
            this.broadcasting(username + " jas joined the board!", username);
            System.out.println(username);
            try {

                remoteClient.say("Joined successfully.");
            }catch (RemoteException e){
                e.printStackTrace();
            }

            // check if it is the first user
            if (manager == null) {
                // this is the first user -> promote it to be the manager
                manager = username;
                try {
                    remoteClient.say("You are the manger now");
                } catch (RemoteException e) {
                    e.printStackTrace();
                    System.out.println("==Error= promote the first user to be the manager. ");
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
        }
        return false;
    }

    @Override
    public boolean close(String username) throws RemoteException {

        // notify all the other user in the board that the board has been closed.
        if (!username.equals(manager)){
            users.get(username).say("You are not the manager !");
            System.out.println(manager + username);
            return false;
        }
        this.users.remove(username);
        this.broadcasting("The white board is closed now");
        return true;
    }

    @Override
    public boolean quit(String username) throws RemoteException {
        // notify all the other user in the board this user quits
        this.users.remove(username);
        this.broadcasting(username + "has quited the board");
        return true;
    }

    private void broadcasting(String message) throws RemoteException{
        for (IRemoteClient remoteClient : users.values()){
            remoteClient.say(message);
        }
    }

    private void broadcasting(String message, String username) throws RemoteException{
        for (String user : users.keySet()){
            if (user != username){
                users.get(user).say(message);
            }
        }
    }
}
