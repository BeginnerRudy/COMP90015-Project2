package WhiteBoardClient;

import RemoteInterface.IRemoteWhiteBoard;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.TimeUnit;

public class RemoteWhiteBoardClient2 {
    public static void main(String[] args) {
        try {
//            if (System.getSecurityManager() == null) {
//                System.setSecurityManager(new RMISecurityManager());
//            }


            Registry registry = LocateRegistry.getRegistry("localhost", 50000);
//            IRemoteShape remoteCanvas = (IRemoteShape) registry.lookup("WhiteBoard");
            RemoteClient remoteClient = new RemoteClient();
//            remoteCanvas.sendRemoteClient(remoteClient);
//            System.out.println("1.7 + 2.8 = " + remoteCanvas.add(1.7, 2.8));
//            System.out.println("6.7 - 2.3 = " + remoteCanvas.subtract(6.7, 2.3));
//            new WhiteBoardGUI(remoteCanvas);
            IRemoteWhiteBoard remoteWhiteBoard = (IRemoteWhiteBoard) registry.lookup("WhiteBoard");
            remoteWhiteBoard.join("user2", remoteClient);

            TimeUnit.SECONDS.sleep(3);
            remoteWhiteBoard.quit("user2");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}