package WhiteBoardClient;

import RemoteInterface.IRemoteWhiteBoard;

import javax.swing.*;
import javax.swing.text.Position;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class LoginController extends UnicastRemoteObject implements IRemoteClient {
    private static LoginController loginController;

    static {
        try {
            loginController = new LoginController();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private LoginFrame loginFrame;
    private WhiteBoardClientGUI whiteBoardClientGUI = new WhiteBoardClientGUI();
    ;
    private IRemoteWhiteBoard remoteWhiteBoard;

    protected LoginController() throws RemoteException {
    }

    public static LoginController getLoginController() {
        return loginController;
    }

    public void init(LoginFrame loginFrame, IRemoteWhiteBoard remoteWhiteBoard) {
        this.loginFrame = loginFrame;
        this.remoteWhiteBoard = remoteWhiteBoard;
    }

    public boolean join(String username) {
        try {

            // communicate with the Remote White board

            // if could join
            if (this.remoteWhiteBoard.join(username, this)) {
                // get user lists from remote
//                System.out.println(this.remoteWhiteBoard.getUserList());

                // add it to the frame
//                ArrayList<String> users_info = this.remoteWhiteBoard.getUserList();
//                for (String user : users_info) {
//                    this.whiteBoardClientGUI.listModel.addElement(user);
//                }

                this.loginFrame.frame.dispose();
                this.whiteBoardClientGUI.frame.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(null, "The Name has already been taken!");
            }

            return true;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void say(String msg) throws RemoteException {

    }

    @Override
    public void updateUserList(ArrayList<String> user_names) throws RemoteException {
        // add it to the frame
        ArrayList<String> users_info = this.remoteWhiteBoard.getUserList();
        for (String user : users_info) {
//            int index = this.whiteBoardClientGUI.userList.getNextMatch(user, 0, Position.Bias.Forward);
//            if (index == -1) {
                this.whiteBoardClientGUI.listModel.addElement(user);
//            }
        }
    }

    @Override
    public void addUser(String username) throws RemoteException {
        this.whiteBoardClientGUI.listModel.addElement(username);
    }
}
