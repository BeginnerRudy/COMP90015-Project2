package WhiteBoardClient;

import WhiteBoardClient.GUI.WhiteBoardLoginFrame;
import WhiteBoardServer.IRemoteWhiteBoard;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RemoteWhiteBoardClient2 {
    public static void main(String[] args) {
        try {
//            if (System.getSecurityManager() == null) {
//                System.setSecurityManager(new RMISecurityManager());
//            }




//            IRemoteShape remoteCanvas = (IRemoteShape) registry.lookup("WhiteBoard");
//            RemoteClient remoteClient = new RemoteClient();
//            remoteCanvas.sendRemoteClient(remoteClient);
//            System.out.println("1.7 + 2.8 = " + remoteCanvas.add(1.7, 2.8));
//            System.out.println("6.7 - 2.3 = " + remoteCanvas.subtract(6.7, 2.3));
//            new WhiteBoardGUI(remoteCanvas);

            WhiteBoardLoginFrame whiteBoardLoginFrame = new WhiteBoardLoginFrame();
            whiteBoardLoginFrame.frame.setVisible(true);

            ClientController.getClientController().init(whiteBoardLoginFrame);




//            remoteWhiteBoard.join("user1", remoteClient);
//
//            TimeUnit.SECONDS.sleep(10);
//            remoteWhiteBoard.close("user1");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
