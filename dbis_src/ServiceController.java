package dbis;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import NWT.*;
public class ServiceController {
	private static final String String = null;
	private Registry registry = null;
	private ServiceInterface server = null;
	public ServiceController() {
		try {
			registry = LocateRegistry.getRegistry("localhost");
			server = (ServiceInterface) registry.lookup("Hello");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public String printMsg(String msg) {
		return msg;
	}
	
	public void insertSQL(String a, String b, String c, String d) throws RemoteException{
		server.insertSQL(a, b, c, d);
	}

	/*
	 * public void getCount(int count) throws RemoteException {
	 * server.getCount(count); }
	 */
	/*
	 * public void insertSQL() throws RemoteException { server.insertSQL(); }
	 */
	public static void main (String[] args) {
		RecloserTopicHolder rth = new RecloserTopicHolder(new RecloserTopic());
		
		try {
			Registry registry = LocateRegistry.getRegistry("localhost");
			ServiceInterface server = (ServiceInterface) registry.lookup("Hello");
			String msg = server.printMsg("Hello");
			//int cnt = server.insertSQL(rth.value.r.io.aliasName, rth.value.r.io.description, rth.value.r.io.mRID, rth.value.r.io.name); 
			//int resultcnt = server.getCount(cnt);
			/*
			 * if (cnt == 40) { System.out.println("insert finish"); System.exit(1); }
			 */
			//server.insertSQL("this is", "me", "One day", "more");
			
		} catch (Exception e) {
			System.err.println("Client exception: " + e.toString());
			e.printStackTrace();
		}
	}
}
