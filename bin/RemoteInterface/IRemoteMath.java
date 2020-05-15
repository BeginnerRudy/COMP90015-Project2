package RemoteInterface;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IRemoteMath extends Remote {
    double add(double i, double j) throws RemoteException;
    double subtract(double i, double j) throws RemoteException;
}
