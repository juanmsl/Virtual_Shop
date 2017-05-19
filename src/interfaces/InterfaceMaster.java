package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

import Negocio.Product;

public interface InterfaceMaster extends Remote {
	public void addClient(String host, InterfaceClient client) throws RemoteException;
	
	public void addServer(String host, InterfaceServer server) throws RemoteException;
	
	public void commit(String host) throws RemoteException;
	
	public boolean validate(String host, Map<Integer, Product> products, Map<Integer, Product> cart) throws RemoteException;
	
	public boolean signup(String username, String password) throws RemoteException;
	
	public boolean login(String username, String password) throws RemoteException;
	
	public Map<Integer, Product> getProducts() throws RemoteException;
	
	public Map<Integer, Product> getClientProducts(String username, String password) throws RemoteException;
}
