package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

import Negocio.Product;

public interface InterfaceServer extends Remote {
	public boolean signup(String username, String password) throws RemoteException;
	
	public boolean login(String username, String password) throws RemoteException;
	
	public void commit(String username, Map<Integer, Product> cart) throws RemoteException;
	
	public Map<Integer, Product> getProducts() throws RemoteException;
	
	public boolean validate(Map<Integer, Product> products, Map<Integer, Product> cart) throws RemoteException;
	
	public Map<Integer, Product> getClientProducts(String username, String password) throws RemoteException;
}
