package master;

import java.rmi.RemoteException;
import java.util.Map;

import Negocio.Product;

public class Transaction {
	private String host;
	private Map<Integer, Product> cart;
	private Map<Integer, Product> products;
	private MasterServer masterServer;
	private boolean procesed;
	
	public Transaction(String host, Map<Integer, Product> cart, Map<Integer, Product> products, MasterServer masterServer) {
		this.host = host;
		this.cart = cart;
		this.products = products;
		this.masterServer = masterServer;
		this.procesed = false;
	}
	
	public String getHost() {
		return this.host;
	}
	
	public Map<Integer, Product> getCart() {
		return this.cart;
	}
	
	public Map<Integer, Product> getProducts() {
		return this.products;
	}
	
	public boolean isProcesed() {
		return this.procesed;
	}
	
	public boolean validate() {
		try {
			if (this.masterServer.validate(this.host, this.products, this.cart)) {
				this.procesed = true;
				return true;
			}
		}
		catch (RemoteException event) {
			System.out.println("Error: [" + event.getMessage() + "]");
		}
		return false;
	}
}
