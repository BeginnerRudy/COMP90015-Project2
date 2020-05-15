package WhiteBoardClient;

import RemoteInterface.IRemoteMath;

import java.rmi.RMISecurityManager;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RemoteMathClient {
    public static void main(String[] args) {
        try {
//            if (System.getSecurityManager() == null) {
//                System.setSecurityManager(new RMISecurityManager());
//            }


            Registry registry = LocateRegistry.getRegistry("localhost", 50000);
            IRemoteMath remoteMath = (IRemoteMath) registry.lookup("Compute");
            System.out.println("1.7 + 2.8 = " + remoteMath.add(1.7, 2.8));
            System.out.println("6.7 - 2.3 = " + remoteMath.subtract(6.7, 2.3));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
