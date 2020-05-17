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
}
