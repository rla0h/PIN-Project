package dbis;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server {
	
	public static void main (String args[]) {
		ServiceInterface server;
		ServiceInterface stub;
		Registry registry;
		try {
			server = new Entity();
			stub = (ServiceInterface) UnicastRemoteObject.exportObject((ServiceInterface) server, 0);
			registry = LocateRegistry.createRegistry(1099);
			registry.rebind("Hello", stub);
			System.out.println("Server Start!");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
