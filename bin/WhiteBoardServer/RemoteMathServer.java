package WhiteBoardServer;

import RemoteInterface.IRemoteMath;

import java.rmi.RMISecurityManager;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RemoteMathServer {
    public static void main(String[] args) {
//        System.setProperty("java.security.policy", "../HelloPolicy");
//        System.setSecurityManager(new RMISecurityManager());
        try {
            IRemoteMath remoteMath = new RemoteMathServant();
            Registry registry = LocateRegistry.createRegistry(50000);
            registry.bind("Compute", remoteMath);
            System.out.println("Math server ready! ");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
