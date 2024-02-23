package dbis;
import java.rmi.RemoteException;
import java.rmi.Remote;

public interface ServiceInterface extends Remote {
	public String printMsg(String msg) throws RemoteException;
	//public int getCount(int count) throws RemoteException;
	public int insertSQL(String a, String b, String c, String d) throws RemoteException;
	//void dbconnect() throws RemoteException;
}
