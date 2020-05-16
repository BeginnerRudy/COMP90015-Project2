package WhiteBoardServer;

import RemoteInterface.IRemoteMath;
import RemoteInterface.IRemoteShape;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RemoteWhiteBoardServer {
    public static void main(String[] args) {
        try {
            IRemoteShape remoteShape = new RemoteWhiteBoardShapeServant();
            Registry registry = LocateRegistry.createRegistry(50000);
            registry.bind("WhiteBoard", remoteShape);
            System.out.println("White Board server ready! ");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
