package WhiteBoardServer;

import RemoteInterface.IRemoteMath;
import RemoteInterface.IRemoteShape;
import RemoteInterface.IRemoteWhiteBoard;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RemoteWhiteBoardServer {
    public static void main(String[] args) {
        try {
//            IRemoteShape remoteShape = new RemoteWhiteBoardShapeServant();
            Registry registry = LocateRegistry.createRegistry(50000);
            IRemoteWhiteBoard remoteWhiteBoard =  new RemoteWhiteBoardServant();
            registry.bind("WhiteBoard", remoteWhiteBoard);
            System.out.println("White Board server ready! ");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
