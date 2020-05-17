package WhiteBoardClient;

import RemoteInterface.IRemoteWhiteBoard;

import javax.swing.*;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class LoginController {
    private static LoginController loginController = new LoginController();
    private LoginFrame loginFrame;
    private IRemoteWhiteBoard remoteWhiteBoard;
    private IRemoteClient remoteClient;

    public static LoginController getLoginController() {
        return loginController;
    }

    public void init(LoginFrame loginFrame, IRemoteWhiteBoard remoteWhiteBoard, IRemoteClient remoteClient) {
        this.loginFrame = loginFrame;
        this.remoteWhiteBoard = remoteWhiteBoard;
        this.remoteClient = remoteClient;
    }

    public boolean join(String username) {
        try {

            // communicate with the Remote White board

            // if could join
            if (this.remoteWhiteBoard.join(username, this.remoteClient)) {
                WhiteBoardClientGUI frame = new WhiteBoardClientGUI();
                // get user lists from remote
                System.out.println(this.remoteWhiteBoard.getUserList());
                // add it to the frame
                ArrayList<String> users_info = this.remoteWhiteBoard.getUserList();
                for (String user : users_info) {
                    frame.listModel.addElement(user);
                }

                this.loginFrame.frame.dispose();
                frame.frame.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(null, "The Name has already been taken!");
            }

            return true;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }
}
