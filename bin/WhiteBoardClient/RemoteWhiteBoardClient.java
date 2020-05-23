package WhiteBoardClient;

import WhiteBoardClient.GUI.WhiteBoardLoginFrame;
import WhiteBoardServer.IRemoteWhiteBoard;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RemoteWhiteBoardClient {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 50000);
            IRemoteWhiteBoard remoteWhiteBoard = (IRemoteWhiteBoard) registry.lookup("WhiteBoard");

            WhiteBoardLoginFrame whiteBoardLoginFrame = new WhiteBoardLoginFrame();
            whiteBoardLoginFrame.frame.setVisible(true);

            ClientController.getClientController().init(whiteBoardLoginFrame, remoteWhiteBoard);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
