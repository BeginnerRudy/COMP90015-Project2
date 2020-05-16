package WhiteBoardClient;

import RemoteInterface.IRemoteMath;
import RemoteInterface.IRemoteShape;
import WhiteBoardServer.RemoteWhiteBoardShapeServant;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RemoteWhiteBoardClient {
    public static void main(String[] args) {
        try {
//            if (System.getSecurityManager() == null) {
//                System.setSecurityManager(new RMISecurityManager());
//            }


            Registry registry = LocateRegistry.getRegistry("localhost", 50000);
            IRemoteShape remoteCanvas = (IRemoteShape) registry.lookup("WhiteBoard");
//            System.out.println("1.7 + 2.8 = " + remoteCanvas.add(1.7, 2.8));
//            System.out.println("6.7 - 2.3 = " + remoteCanvas.subtract(6.7, 2.3));
            new WhiteBoardGUI(remoteCanvas);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
