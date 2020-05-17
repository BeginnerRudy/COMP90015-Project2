package WhiteBoardClient;

import RemoteInterface.IRemoteWhiteBoard;

import java.rmi.RemoteException;

public class LoginController {
    private static LoginController loginController = new LoginController();
    private LoginFrame loginFrame;
    private IRemoteWhiteBoard remoteWhiteBoard;
    private IRemoteClient remoteClient;

    public static LoginController getLoginController(){
        return loginController;
    }

    public void init(LoginFrame loginFrame, IRemoteWhiteBoard remoteWhiteBoard, IRemoteClient remoteClient){
        this.loginFrame = loginFrame;
        this.remoteWhiteBoard = remoteWhiteBoard;
        this.remoteClient = remoteClient;
    }

    public boolean join(String username){
        try {
            return this.remoteWhiteBoard.join(username, this.remoteClient);
        }catch (RemoteException e){
            e.printStackTrace();
        }
        return false;
    }
}
