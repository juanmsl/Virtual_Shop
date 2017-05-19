package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

import Negocio.Product;

public interface InterfaceClient extends Remote {
	public void commit() throws RemoteException;
	
	public Map<Integer, Product> getCart() throws RemoteException;
	
	public Map<Integer, Product> getProducts() throws RemoteException;
	
	public String getUsername() throws RemoteException;
	
	public void getItems(boolean update) throws RemoteException;
}
